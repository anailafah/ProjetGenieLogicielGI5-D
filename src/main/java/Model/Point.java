 package Model;

import java.io.Serializable;

/**
 * Represents a 2D point with coordinates x and y.
 */
public class Point implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private double x;
    private double y;

    /**
     * Creates a point with an identifier and coordinates.
     *
     * @param id the identifier of the point
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the identifier of the point.
     *
     * @return the point identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the x-coordinate of the point.
     *
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the point.
     *
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Updates the x-coordinate of the point.
     *
     * @param x the new x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Updates the y-coordinate of the point.
     *
     * @param y the new y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns a string representation of the point.
     *
     * @return a string containing the point identifier and coordinates
     */
    @Override
    public String toString() {
        return "Point " + id + " (" + x + ", " + y + ")";
    }

    /**
     * Checks whether this point is inside the circumcircle of a triangle.
     *
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     * @return true if this point is inside the circumcircle, false otherwise
     */
    public boolean isInCircumcircle(Point a, Point b, Point c) {
        double ax = a.getX() - this.getX();
        double ay = a.getY() - this.getY();
        double bx = b.getX() - this.getX();
        double by = b.getY() - this.getY();
        double cx = c.getX() - this.getX();
        double cy = c.getY() - this.getY();

        double d = (ax * ax + ay * ay) * (bx * cy - cx * by)
                - (bx * bx + by * by) * (ax * cy - cx * ay)
                + (cx * cx + cy * cy) * (ax * by - bx * ay);

        return d > 0;
    }
}