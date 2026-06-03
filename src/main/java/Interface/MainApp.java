package Interface;

import Algorithm.ImportExportMap;
import Algorithm.TriangulationDelaunay;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import java.io.File;

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

        Button importCSV = new Button("Importer hopitaux CSV");
        importCSV.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir un fichier CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    ImportExportMap.importHospitalsCSV(file.getAbsolutePath(), engine);
                    canvas.redraw();
                } catch (Exception ex) {
                    System.err.println("Erreur import CSV : " + ex.getMessage());
                }
            }
        });

        ToolBar toolbar = new ToolBar(toggleDelaunay, addPatients, importCSV);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(canvas);

        Scene scene = new Scene(root, 820, 660);

        canvas.setOnMouseClicked(e -> controller.selectNearestHospital(e.getX(), e.getY()));

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) controller.deleteSelected();
            if (e.getCode() == KeyCode.ESCAPE)  controller.deselect();
        });

        primaryStage.setTitle("Voronoi Hospital Map");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** Creates the engine and populates it with sample hospitals. */
    private TriangulationDelaunay buildEngine() {
        TriangulationDelaunay e = new TriangulationDelaunay(800, 600);
        e.addHospital(200, 150, "Hopital A", 10);
        e.addHospital(400, 300, "Hopital B", 10);
        e.addHospital(600, 150, "Hopital C", 10);
        e.addHospital(300, 480, "Hopital D", 10);
        e.addHospital(550, 450, "Hopital E", 10);
        return e;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
