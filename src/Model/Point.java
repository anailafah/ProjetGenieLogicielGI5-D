package Model;
/**
 * Represents a 2D point 
 */
public class Point {
	
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
	public double geyY() {
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
		return "Point" + id + " (" + x +", "+y+ ")";
	}
	
}
