package Interface;

import Algorithm.TriangulationDelaunay;
import Model.Hospital;
import java.util.Random;

/**
 * Controller that wires UI actions to the MapCanvas and the Voronoi engine.
 */
public class MainController {

    private final MapCanvas canvas;
    private final TriangulationDelaunay engine;
    private final Random random = new Random();
    private Hospital selectedHospital = null;

    public MainController(MapCanvas canvas, TriangulationDelaunay engine) {
        this.canvas = canvas;
        this.engine = engine;
    }

    /** Shows or hides the Delaunay triangulation overlay. */
    public void setShowDelaunay(boolean show) {
        canvas.setShowDelaunay(show);
    }

    /**
     * Left click : selects hospital if within 15px, otherwise adds a new one.
     */
    public void handleLeftClick(double x, double y) {
        Hospital nearest = null;
        double minDist = 15;
        for (Hospital h : engine.getMap().getHospitals()) {
            double dist = Math.sqrt(Math.pow(h.getX() - x, 2) + Math.pow(h.getY() - y, 2));
            if (dist < minDist) {
                minDist = dist;
                nearest = h;
            }
        }
        if (nearest != null) {
            selectedHospital = nearest;
            canvas.setSelectedHospital(selectedHospital);
        } else {
            engine.addHospital(x, y, "Hopital", 10);
            canvas.redraw();
        }
    }

    /** Deletes the hospital closest to the click if within 15 pixels. */
    public void deleteNearestHospital(double x, double y) {
        Hospital nearest = null;
        double minDist = 15;
        for (Hospital h : engine.getMap().getHospitals()) {
            double dist = Math.sqrt(Math.pow(h.getX() - x, 2) + Math.pow(h.getY() - y, 2));
            if (dist < minDist) {
                minDist = dist;
                nearest = h;
            }
        }
        if (nearest != null) {
            if (nearest == selectedHospital) selectedHospital = null;
            engine.removeHospital(nearest);
            canvas.setSelectedHospital(null);
        }
    }

    /** Deletes the currently selected hospital. */
    public void deleteSelected() {
        if (selectedHospital != null) {
            engine.removeHospital(selectedHospital);
            selectedHospital = null;
            canvas.setSelectedHospital(null);
        }
    }

    /** Clears the current selection. */
    public void deselect() {
        selectedHospital = null;
        canvas.setSelectedHospital(null);
    }

    /** Adds n patients at random positions within the canvas bounds, then redraws. */
    public void addRandomPatients(int n) {
        double width  = canvas.getWidth()  > 0 ? canvas.getWidth()  : 800;
        double height = canvas.getHeight() > 0 ? canvas.getHeight() : 600;
        for (int i = 0; i < n; i++) {
            double x = 20 + random.nextDouble() * (width - 40);
            double y = 20 + random.nextDouble() * (height - 40);
            engine.addUser(x, y);
        }
        canvas.redraw();
    }
}
