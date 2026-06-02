package Interface;

/**
 * Controller that wires UI actions to the MapCanvas.
 */
public class MainController {

    private final MapCanvas canvas;

    public MainController(MapCanvas canvas) {
        this.canvas = canvas;
    }

    /** Shows or hides the Delaunay triangulation overlay. */
    public void setShowDelaunay(boolean show) {
        canvas.setShowDelaunay(show);
    }
}
