package Interface;

import Model.Hospital;
import Model.Triangle;
import Model.User;
import Model.VoronoiMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * JavaFX drawing zone for displaying the Voronoi map.
 */
public class MapCanvas extends Canvas {

    private VoronoiMap map;
    private boolean showDelaunay = true;

    public MapCanvas(double width, double height) {
        super(width, height);
        drawEmptyMap();
    }

    public void setMap(VoronoiMap map) {
        this.map = map;
        redraw();
    }

    public VoronoiMap getMap() {
        return map;
    }

    /** Toggles the Delaunay triangulation overlay on or off. */
    public void setShowDelaunay(boolean show) {
        this.showDelaunay = show;
        redraw();
    }

    public boolean isShowDelaunay() {
        return showDelaunay;
    }

    public void redraw() {
        if (map == null) {
            drawEmptyMap();
            return;
        }

        GraphicsContext gc = getGraphicsContext2D();

        clear(gc);
        if (showDelaunay) drawTriangles(gc);
        drawHospitals(gc);
        drawUsers(gc);
    }

    private void drawTriangles(GraphicsContext gc) {
        gc.setStroke(Color.CORNFLOWERBLUE);
        gc.setLineWidth(1.0);
        for (Triangle t : map.getTriangles()) {
            double ax = t.getA().getX(), ay = t.getA().getY();
            double bx = t.getB().getX(), by = t.getB().getY();
            double cx = t.getC().getX(), cy = t.getC().getY();
            gc.strokeLine(ax, ay, bx, by);
            gc.strokeLine(bx, by, cx, cy);
            gc.strokeLine(cx, cy, ax, ay);
        }
        gc.setLineWidth(1.0);
    }

    private void drawEmptyMap() {
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        gc.setFill(Color.GRAY);
        gc.fillText("Aucune carte à afficher", 20, 30);
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeRect(0, 0, getWidth(), getHeight());
    }

    private void drawHospitals(GraphicsContext gc) {
        for (Hospital hospital : map.getHospitals()) {
            double x = hospital.getX();
            double y = hospital.getY();

            if (hospital.isSaturated()) {
                gc.setFill(Color.RED);
            } else if (hospital.getAvailableRoom() <= hospital.getMaxCapacity() * 0.3) {
                gc.setFill(Color.ORANGE);
            } else {
                gc.setFill(Color.BLUE);
            }

            gc.fillOval(x - 6, y - 6, 12, 12);
            gc.setFill(Color.BLACK);
            gc.fillText("H" + hospital.getId(), x + 8, y - 8);
        }
    }

    private void drawUsers(GraphicsContext gc) {
        for (User user : map.getUserTot()) {
            double x = user.getX();
            double y = user.getY();

            gc.setFill(user.getIsRedirected() ? Color.RED : Color.GREEN);
            gc.fillOval(x - 4, y - 4, 8, 8);
            gc.setFill(Color.BLACK);
            gc.fillText("U" + user.getId(), x + 6, y - 6);
        }
    }
}