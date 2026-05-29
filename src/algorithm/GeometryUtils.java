package algorithm;

import Model.Point;
import Model.Triangle;

/**
 * Utility class for geometric calculations used in Voronoi algorithms.
 */
public final class GeometryUtils {

    private static final double EPSILON = 1e-9;
    private static final int GENERATED_POINT_ID = -1;

    private GeometryUtils() {
        // Utility class: no instances allowed
    }

    /**
     * Calculates the Euclidean distance between two points.
     */
    public static double distance(Point p1, Point p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the squared distance between two points.
     * Useful when comparing distances without using Math.sqrt.
     */
    public static double distanceSquared(Point p1, Point p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();

        return dx * dx + dy * dy;
    }

    /**
     * Calculates the midpoint between two points.
     */
    public static Point midpoint(Point p1, Point p2) {
        double x = (p1.getX() + p2.getX()) / 2.0;
        double y = (p1.getY() + p2.getY()) / 2.0;

        return new Point(GENERATED_POINT_ID, x, y);
    }

    /**
     * Calculates the orientation of three points.
     *
     * @return positive if counter-clockwise, negative if clockwise, zero if collinear
     */
    public static double orientation(Point a, Point b, Point c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY())
             - (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    /**
     * Checks whether three points are collinear.
     */
    public static boolean areCollinear(Point a, Point b, Point c) {
        return Math.abs(orientation(a, b, c)) < EPSILON;
    }

    /**
     * Calculates the circumcenter of the circle passing through three points.
     */
    public static Point circumcenter(Point a, Point b, Point c) {
        double ax = a.getX();
        double ay = a.getY();

        double bx = b.getX();
        double by = b.getY();

        double cx = c.getX();
        double cy = c.getY();

        double d = 2 * (ax * (by - cy)
                 + bx * (cy - ay)
                 + cx * (ay - by));

        if (Math.abs(d) < EPSILON) {
            throw new IllegalArgumentException("The three points are collinear. Circumcenter does not exist.");
        }

        double ux = ((ax * ax + ay * ay) * (by - cy)
                  + (bx * bx + by * by) * (cy - ay)
                  + (cx * cx + cy * cy) * (ay - by)) / d;

        double uy = ((ax * ax + ay * ay) * (cx - bx)
                  + (bx * bx + by * by) * (ax - cx)
                  + (cx * cx + cy * cy) * (bx - ax)) / d;

        return new Point(GENERATED_POINT_ID, ux, uy);
    }

    /**
     * Calculates the circumcenter of a triangle.
     */
    public static Point circumcenter(Triangle triangle) {
        return circumcenter(
                triangle.getA(),
                triangle.getB(),
                triangle.getC()
        );
    }

    /**
     * Checks if a point is inside or on the circumcircle defined by three points.
     */
    public static boolean isInsideCircumcircle(Point point, Point a, Point b, Point c) {
        Point center = circumcenter(a, b, c);
        double radiusSquared = distanceSquared(center, a);
        double pointDistanceSquared = distanceSquared(center, point);

        return pointDistanceSquared <= radiusSquared + EPSILON;
    }

    /**
     * Checks if a point is inside or on the circumcircle of a triangle.
     */
    public static boolean isInsideCircumcircle(Point point, Triangle triangle) {
        return isInsideCircumcircle(
                point,
                triangle.getA(),
                triangle.getB(),
                triangle.getC()
        );
    }
}
  