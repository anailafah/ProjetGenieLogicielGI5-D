package Interface;

import Algorithm.TriangulationDelaunay;
import Model.VoronoiMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the Voronoi hospital map application.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MapCanvas canvas = new MapCanvas(800, 600);
        MainController controller = new MainController(canvas);

        CheckBox toggleDelaunay = new CheckBox("Show Delaunay triangulation");
        toggleDelaunay.setSelected(true);
        toggleDelaunay.setOnAction(e -> controller.setShowDelaunay(toggleDelaunay.isSelected()));

        ToolBar toolbar = new ToolBar(toggleDelaunay);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(canvas);

        canvas.setMap(buildSampleMap());

        primaryStage.setTitle("Voronoi Hospital Map");
        primaryStage.setScene(new Scene(root, 820, 660));
        primaryStage.show();
    }

    /** Builds a small sample map with hospitals and Delaunay triangles for demo. */
    private VoronoiMap buildSampleMap() {
        TriangulationDelaunay engine = new TriangulationDelaunay(800, 600);
        engine.addHospital(200, 150, 10);
        engine.addHospital(400, 300, 10);
        engine.addHospital(600, 150, 10);
        engine.addHospital(300, 480, 10);
        engine.addHospital(550, 450, 10);
        return engine.getMap();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
