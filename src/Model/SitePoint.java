package Model;
import java.util.List;
import java.util.ArrayList;

/**
 * Represent a reference point (voronoi site)
 */
public class SitePoint extends Point {
	private List<UserPoint> userPoints;
	/**
	 * construction of a reference point
	 * @param id
	 * @param x
	 * @param y
	 */
	public SitePoint(int id,double x,double y) {
		super(id,x,y);
		this.userPoints=new ArrayList<>();
	}
	/**
	 * @return list of users who are affiliated to the site
	 */
	public List<UserPoint> getUserPoints(){
		return userPoints;
	}
	/**
	 * add an user to the list of users
	 * @param user
	 */
	public void addUserPoint(UserPoint user) {
		userPoints.add(user);
	}
	/**
	 * remove an user from the list of users
	 * @param user
	 */
	public void removeUserPoint(UserPoint user) {
		userPoints.remove(user);
	}
	@Override
	public String toString() {
		return "SitePoint "+getId() + " ("+getX()+" , "+getY()+") Users ="+userPoints.size();
	}
}
