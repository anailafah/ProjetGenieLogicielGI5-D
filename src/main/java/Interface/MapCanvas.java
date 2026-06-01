package Interface;

import Model.Hospital;
import Model.User;
import Model.VoronoiMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Zone de dessin JavaFX pour afficher la carte Voronoi.
 */
public class MapCanvas extends Canvas {

    private VoronoiMap map;

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

    public void redraw() {
        if (map == null) {
            drawEmptyMap();
            return;
        }

        GraphicsContext gc = getGraphicsContext2D();

        clear(gc);
        drawHospitals(gc);
        drawUsers(gc);
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