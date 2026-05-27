package Model;
import java.util.List;
import java.util.ArrayList;

/**
 * Represent an user (patient) as a point 
 */
public class User extends Point {
	private Hospital closestSite;
	private List<Hospital> nextHospitals;
	private boolean isRedirected;
	/**
	 * creation of an user
	 * @param id
	 * @param x
	 * @param y
	 */
	public User(int id,double x,double y) {
		super(id,x,y);
		this.closestSite=null;
		this.nextHospitals=new ArrayList<>();
		this.isRedirected=false;
	}
	/**
	 * @return the closest hospital for the user
	 */
	public Hospital getClosestSite() {
		return closestSite;
	}
	/**
	 * @return a list of the closest hospitals ordered by distance
	 */
	public List<Hospital> getNextHospitals(){
		return nextHospitals;
	}
	/**
	 * set the closest hospital to the user
	 * @param site
	 */
	public void setClosestSite(Hospital site) {
		this.closestSite=site;
	}
	/**
	 * set the list of the closest hospitals 
	 * @param listH
	 */
	public void setNextHospitals(List<Hospital> listH){
		this.nextHospitals=listH;
	}
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		User a = new User(51,6.3,1.2);
		System.out.println(a);
	}
}
