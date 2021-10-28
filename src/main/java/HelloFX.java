import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloFX extends Application {

  private ImageHelper imageHelper;

  @Override
  public void start(Stage stage) throws Exception {
    imageHelper = new ImageHelper(getParameters().getRaw().get(0));
    Canvas canvas = new Canvas(imageHelper.getWidth(), imageHelper.getWidth());
    GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
    graphicsContext2D.setFill(Color.rgb(46, 45, 36));
    graphicsContext2D.fillRect(0, 0, imageHelper.getWidth(), imageHelper.getWidth());
    Group root = new Group(canvas);
    Scene scene = new Scene(root, imageHelper.getWidth(), imageHelper.getHeight());
    stage.setScene(scene);
    stage.show();
    imageHelper.draw(pixels -> {
      pixels.forEach(pixel -> {
        graphicsContext2D.getPixelWriter().setArgb(pixel.x, pixel.y, pixel.rgb);
      });

    });
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalStateException("Image path is missing");
    }
    launch(args);
  }
}