package Algorithm;
import Model.Point;
/**
 *class for geometric computations.
 */

public class GeometryFunc {
    /**
     * Computes the Euclidean distance between two points.
     * @param p1 first point
     * @param p2 second point
     * @return the distance
     */
    public static double distance(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Computes the area of a triangle given three points.
     * @param a first point
     * @param b second point
     * @param c third point
     * @return the area
     
    public static double triangleArea(Point a, Point b, Point c) {
        return Math.abs(
            (b.getX() - a.getX()) * (c.getY() - a.getY()) -
            (c.getX() - a.getX()) * (b.getY() - a.getY())
        ) / 2.0;
    
    }
    */
}