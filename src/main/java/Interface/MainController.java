package Interface;

import Algorithm.TriangulationDelaunay;
import java.util.Random;

/**
 * Controller that wires UI actions to the MapCanvas and the Voronoi engine.
 */
public class MainController {

    private final MapCanvas canvas;
    private final TriangulationDelaunay engine;
    private final Random random = new Random();

    public MainController(MapCanvas canvas, TriangulationDelaunay engine) {
        this.canvas = canvas;
        this.engine = engine;
    }

    /** Shows or hides the Delaunay triangulation overlay. */
    public void setShowDelaunay(boolean show) {
        canvas.setShowDelaunay(show);
    }

    /** Adds n patients at random positions within the canvas bounds, then redraws. */
    public void addRandomPatients(int n) {
        double width  = canvas.getWidth();
        double height = canvas.getHeight();
        for (int i = 0; i < n; i++) {
            double x = random.nextDouble() * width;
            double y = random.nextDouble() * height;
            engine.addUser(x, y);
        }
        canvas.redraw();
    }
}
