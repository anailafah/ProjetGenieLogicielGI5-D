package Interface;

import Algorithm.VoronoiEngine;
import Model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * JavaFX Canvas responsible for rendering the Voronoi diagram,
 * Delaunay triangulation, hospitals and users.
 */
public class MapCanvas extends Canvas {

    private VoronoiEngine engine;
    private Hospital      selectedHospital;

    private boolean showDelaunay = false;
    private boolean showZones    = false;
    private boolean showLinks    = false;

    private double offsetX = 0;
    private double offsetY = 0;
    private double scale   = 1.0;

    private static final Color[] ZONE_COLORS = {
        Color.web("#AEE8D0", 0.4),
        Color.web("#AEC8E8", 0.4),
        Color.web("#E8D0AE", 0.4),
        Color.web("#D0AEE8", 0.4),
        Color.web("#E8AEB0", 0.4),
        Color.web("#D0E8AE", 0.4)
    };

    /**
     * Constructs the MapCanvas.
     * @param width  initial canvas width
     * @param height initial canvas height
     * @param engine the Voronoi engine
     */
    public MapCanvas(double width, double height, VoronoiEngine engine) {
        super(width, height);
        this.engine = engine;
    }

    /**
     * Sets the engine (used when importing a map).
     * @param engine the new engine
     */
    public void setEngine(VoronoiEngine engine) {
        this.engine = engine;
    }

    /**
     * Sets the currently selected hospital (highlighted with halo).
     * @param h the selected hospital, or null to clear
     */
    public void setSelectedHospital(Hospital h) {
        this.selectedHospital = h;
    }

    /**
     * Sets whether the Delaunay triangulation is shown.
     * @param show true to show
     */
    public void setShowDelaunay(boolean show) {
        this.showDelaunay = show;
    }

    /**
     * Sets whether the Voronoi zones are shown.
     * @param show true to show
     */
    public void setShowZones(boolean show) {
        this.showZones = show;
    }

    /**
     * Sets whether the user-to-hospital links are shown.
     * @param show true to show
     */
    public void setShowLinks(boolean show) {
        this.showLinks = show;
    }

    /**
     * Sets the zoom scale.
     * @param scale the new scale
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Adds an offset to the view (pan).
     * @param dx horizontal offset
     * @param dy vertical offset
     */
    public void addOffset(double dx, double dy) {
        this.offsetX += dx;
        this.offsetY += dy;
    }

    /**
     * Converts world X coordinate to screen X.
     * @param wx world x
     * @return screen x
     */
    public double toScreenX(double wx) {
        return wx * scale + offsetX;
    }

    /**
     * Converts world Y coordinate to screen Y.
     * @param wy world y
     * @return screen y
     */
    public double toScreenY(double wy) {
        return wy * scale + offsetY;
    }

    /**
     * Converts screen X coordinate to world X.
     * @param sx screen x
     * @return world x
     */
    public double toWorldX(double sx) {
        return (sx - offsetX) / scale;
    }

    /**
     * Converts screen Y coordinate to world Y.
     * @param sy screen y
     * @return world y
     */
    public double toWorldY(double sy) {
        return (sy - offsetY) / scale;
    }
    /**
     * Redraws the entire canvas.
     * Called every time the map changes.
     */
    public void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();

        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.web("#e3d4d4"));
        gc.fillRect(0, 0, w, h);

        if (engine == null) return;

        List<Triangle>  triangles = engine.getTriangles();
        List<HospitalZone> zones  = engine.getZones();
        List<Hospital>  hospitals = engine.getMap().getHospitals();
        List<User>      users     = engine.getMap().getUserTot();

        if (showZones)    drawZones(gc, zones);
        if (showDelaunay) drawDelaunay(gc, triangles);
        if (showLinks)    drawLinks(gc, users);
        drawHospitals(gc, hospitals);
        drawUsers(gc, users);
    }

    /**
     * Draws pre-computed Voronoi zones.
     * @param gc        graphics context
     * @param zones     list of hospital zones
     */
    private void drawZones(GraphicsContext gc, List<HospitalZone> zones) {
    int colorIdx = 0;
    for (HospitalZone zone : zones) {
        gc.setFill(ZONE_COLORS[colorIdx % ZONE_COLORS.length]);
        colorIdx++;

        List<Point> vertices = zone.getVertices();
        if (vertices == null || vertices.isEmpty()) continue;

        double[] xs = vertices.stream().mapToDouble(v -> toScreenX(v.getX())).toArray();
        double[] ys = vertices.stream().mapToDouble(v -> toScreenY(v.getY())).toArray();

        gc.fillPolygon(xs, ys, vertices.size());

        gc.setStroke(Color.web("#19a09b", 0.6));
        gc.setLineWidth(1.0);
        gc.strokePolygon(xs, ys, vertices.size());
    }
}

    /**
     * Draws the Delaunay triangulation edges.
     * @param gc        graphics context
     * @param triangles list of triangles
     */
    private void drawDelaunay(GraphicsContext gc, List<Triangle> triangles) {
        gc.setStroke(Color.web("#5588AA", 0.6));
        gc.setLineWidth(1.0 / scale);
        for (Triangle t : triangles) {
            double[] xs = {
                toScreenX(t.getA().getX()),
                toScreenX(t.getB().getX()),
                toScreenX(t.getC().getX())
            };
            double[] ys = {
                toScreenY(t.getA().getY()),
                toScreenY(t.getB().getY()),
                toScreenY(t.getC().getY())
            };
            gc.strokePolygon(xs, ys, 3);
        }
    }

    /**
     * Draws lines between users and their assigned hospital.
     * Green = normal, Red = redirected.
     * @param gc    graphics context
     * @param users list of users
     */
    private void drawLinks(GraphicsContext gc, List<User> users) {
        gc.setLineWidth(0.8 / scale);
        for (User u : users) {
            if (u.getClosestSite() == null) continue;
            double ux = toScreenX(u.getX());
            double uy = toScreenY(u.getY());
            double hx = toScreenX(u.getClosestSite().getX());
            double hy = toScreenY(u.getClosestSite().getY());
            gc.setStroke(u.getIsRedirected() ? Color.web("#D85A30", 0.4): Color.web("#1D9E75", 0.3));
            gc.strokeLine(ux, uy, hx, hy);
        }
    }

    /**
     * Draws hospitals with color based on saturation level.
     * Blue = available, Orange = almost full, Red = saturated.
     * Highlighted with a halo if selected.
     * @param gc        graphics context
     * @param hospitals list of hospitals
     */
    private void drawHospitals(GraphicsContext gc, List<Hospital> hospitals) {
        for (Hospital h : hospitals) {
            double sx = toScreenX(h.getX());
            double sy = toScreenY(h.getY());
            double r  = 8 * Math.max(0.5, Math.min(scale, 2.0));

            double rate = h.getSaturationRate();
            Color color;
            if (rate >= 100)     color = Color.web("#CC2200");
            else if (rate >= 75) color = Color.web("#E07700");
            else                 color = Color.web("#1D5C8A");

            if (h == selectedHospital) {
                gc.setFill(Color.web("#EF9F27", 0.3));
                gc.fillOval(sx - r*2, sy - r*2, r*4, r*4);
            }

            gc.setFill(color);
            gc.fillOval(sx - r, sy - r, r*2, r*2);

            gc.setFill(Color.WHITE);
            gc.fillOval(sx - r*0.4, sy - r*0.4, r*0.8, r*0.8);

            if (scale > 0.5) {
                gc.setFill(Color.web("#222222"));
                gc.fillText(h.getName(), sx + r + 3, sy + 4);
            }
        }
    }

    /**
     * Draws users with color based on redirection status.
     * Green = assigned to nearest, Red = redirected.
     * @param gc    graphics context
     * @param users list of users
     */
    private void drawUsers(GraphicsContext gc, List<User> users) {
        for (User u : users) {
            double ux = toScreenX(u.getX());
            double uy = toScreenY(u.getY());
            double r  = 4 * Math.max(0.5, Math.min(scale, 2.0));

            gc.setFill(u.getIsRedirected() ? Color.web("#D85A30") : Color.web("#1D9E75"));
            gc.fillOval(ux - r, uy - r, r*2, r*2);
        }
    }
}