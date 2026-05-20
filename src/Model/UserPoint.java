package Model;
/**
 * Represent an user as a point 
 */
public class UserPoint extends Point {
	/**
	 * creation of an user
	 * @param id
	 * @param x
	 * @param y
	 */
	public UserPoint(int id,double x,double y) {
		super(id,x,y);
	}
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		UserPoint a = new UserPoint(51,6.3,1.2);
		System.out.println(a);
	}
}
