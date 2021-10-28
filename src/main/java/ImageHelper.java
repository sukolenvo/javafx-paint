import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import javafx.application.Platform;
import javax.imageio.ImageIO;

public class ImageHelper {

  private final BufferedImage original;
  private final BufferedImage grayscale;

  public ImageHelper(String image) throws IOException {
    original = ImageIO.read(new File(image));
    grayscale = new BufferedImage(original.getWidth(), original.getHeight(),
        BufferedImage.TYPE_BYTE_GRAY);
    Graphics g = grayscale.getGraphics();
    g.drawImage(original, 0, 0, null);
    g.dispose();
  }

  public int getWidth() {
    return original.getWidth();
  }

  public int getHeight() {
    return original.getHeight();
  }

  public void draw(Consumer<List<Pixel>> callback) {
    int durationSec = 60;
    new Thread(() -> {
      List<Pixel> pixels = new ArrayList<>();
      for (int i = 0; i < grayscale.getWidth(); i++) {
        for (int j = 0; j < grayscale.getHeight(); j++) {
          Pixel pixel = new Pixel();
          pixel.x = i;
          pixel.y = j;
          pixel.rgb = original.getRGB(i, j);
          pixel.position = (grayscale.getRGB(i, j) & 0xff) / 2;
          pixels.add(pixel);
        }
      }
      Map<Integer, Integer> stableRandomValues = new HashMap<>();
      Random random = new Random(0);
      int batchSize = pixels.size() / durationSec / 60;
      pixels.sort(Comparator.<Pixel, Integer>comparing(pixel -> pixel.position)
          .thenComparing(pixel -> stableRandomValues.getOrDefault(pixel.x * pixel.y, random.nextInt()))
          .reversed());
      for (int i = 0; i < pixels.size(); i += batchSize) {
        int finalI = i;
        Platform.runLater(() -> callback.accept(pixels.subList(finalI, Math.min(pixels.size(), finalI + batchSize))));
        try {
          Thread.sleep(1000/70);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  public static class Pixel {
    public int x;
    public int y;
    public int rgb;
    public int position;
  }
}
