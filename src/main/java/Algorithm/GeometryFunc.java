package Algorithm;

import Model.Point;

/**
 * Utility class for geometric computations.
 */
public class GeometryFunc {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GeometryFunc() {
    }

    /**
     * Computes the Euclidean distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the distance between the two points
     */
    public static double distance(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}