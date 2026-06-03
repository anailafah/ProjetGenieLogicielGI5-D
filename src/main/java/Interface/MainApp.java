package Interface;

import Algorithm.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main JavaFX application entry point.
 * Loads main.fxml and connects it to the engine.
 */
public class MainApp extends Application {

    public static final double CANVAS_WIDTH  = 880;
    public static final double CANVAS_HEIGHT = 660;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main.fxml"));

            BorderPane root = loader.load();

            MainController controller = (MainController) loader.getController();

            VoronoiEngine engine = new TriangulationDelaunay(CANVAS_WIDTH, CANVAS_HEIGHT);
            controller.setEngine(engine);
            controller.init(CANVAS_WIDTH, CANVAS_HEIGHT);

            Scene scene = new Scene(root, CANVAS_WIDTH + 220, CANVAS_HEIGHT + 60);

            try {
                scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm());
            } catch (Exception e) {
                System.err.println("CSS not found, continuing without styles");
            }

            stage.setTitle("Voronoï Hospital ING1-GI group D");
            stage.setScene(scene);
            stage.setMinWidth(700);
            stage.setMinHeight(500);
            stage.show();

        } catch (Exception e) {
            System.err.println("Fatal error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Launches the application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}