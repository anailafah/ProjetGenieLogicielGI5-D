package Model;

import java.io.Serializable;

/**
 * Represents a 2D point with coordinates x and y
 */
public class Point implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private final int id;
	private double x;
	private double y;
	
	/**
	 * construction of a Point
	 * @param id
	 * @param x
	 * @param y
	 */
	public Point(int id,double x,double y) {
		this.id = id;
		this.x=x;
		this.y=y;
	}
	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @return x
	 */
	public double getX() {
		return x;
	}
	/**
	 * @return y
	 */
	public double getY() {
		return y;
	}
	/**
	 * set the new coordinate x
	 * @param x
	 */
	public void setX(double x) {
		this.x=x;
	}
	/**
	 * set the new coordinate y
	 * @param y
	 */
	public void setY(double y) {
		this.y=y;
	}
	
	@Override
	public String toString() {
		return "Point " + id + " (" + x +", "+y+ ")";
	}
	
	/**
     * Checks if point is inside the circumcircle of triangle (a, b, c).
     * @param a first vertex
     * @param b second vertex
     * @param c third vertex
     * @return true if the point is inside the circumcircle
     */
    public boolean isInCircumcircle(Point a, Point b, Point c) {
        double ax = a.getX() - this.getX(), ay = a.getY() - this.getY();
        double bx = b.getX() - this.getX(), by = b.getY() - this.getY();
        double cx = c.getX() - this.getX(), cy = c.getY() - this.getY();
        double d = (ax*ax + ay*ay) * (bx*cy - cx*by)
                 - (bx*bx + by*by) * (ax*cy - cx*ay)
                 + (cx*cx + cy*cy) * (ax*by - bx*ay);
        return d > 0;
	}
	
}
