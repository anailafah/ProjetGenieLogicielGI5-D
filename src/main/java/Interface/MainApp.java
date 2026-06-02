package Interface;

import Algorithm.TriangulationDelaunay;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the Voronoi hospital map application.
 */
public class MainApp extends Application {

    private TriangulationDelaunay engine;

    @Override
    public void start(Stage primaryStage) {
        engine = buildEngine();

        MapCanvas canvas = new MapCanvas(800, 600);
        canvas.setMap(engine.getMap());

        MainController controller = new MainController(canvas, engine);

        CheckBox toggleDelaunay = new CheckBox("Show Delaunay triangulation");
        toggleDelaunay.setSelected(true);
        toggleDelaunay.setOnAction(e -> controller.setShowDelaunay(toggleDelaunay.isSelected()));

        Button addPatients = new Button("+ Patients aleatoires");
        addPatients.setOnAction(e -> controller.addRandomPatients(3));

        ToolBar toolbar = new ToolBar(toggleDelaunay, addPatients);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(canvas);

        primaryStage.setTitle("Voronoi Hospital Map");
        primaryStage.setScene(new Scene(root, 820, 660));
        primaryStage.show();
    }

    /** Creates the engine and populates it with sample hospitals. */
    private TriangulationDelaunay buildEngine() {
        TriangulationDelaunay e = new TriangulationDelaunay(800, 600);
        e.addHospital(200, 150, 10);
        e.addHospital(400, 300, 10);
        e.addHospital(600, 150, 10);
        e.addHospital(300, 480, 10);
        e.addHospital(550, 450, 10);
        return e;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
