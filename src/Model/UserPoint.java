package Model;
/**
 * Represent an user as a point 
 */
public class UserPoint extends Point {
	private SitePoint closestSite;
	/**
	 * creation of an user
	 * @param id
	 * @param x
	 * @param y
	 */
	public UserPoint(int id,double x,double y) {
		super(id,x,y);
		this.closestSite=null;
	}
	/**
	 * @return the closest site for the user
	 */
	public SitePoint getClosestSite() {
		return closestSite;
	}
	/**
	 * set the closest site for the user
	 * @param site
	 */
	public void setClosestSite(SitePoint site) {
		this.closestSite=site;
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
