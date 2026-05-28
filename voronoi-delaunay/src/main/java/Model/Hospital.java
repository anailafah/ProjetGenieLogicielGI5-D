package Model;
import java.util.List;
import java.util.ArrayList;

/**
 * Represent a hospital point (voronoi site)
 */
public class Hospital extends Point {
	private List<User> users;
	private final int maxCapacity;
	/**
	 * construction of a reference point
	 * @param id id of the hospital
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param maxCapacity max number of patient
	 */
	public Hospital(int id,double x,double y,int maxCapacity) {
		super(id,x,y);
		this.users=new ArrayList<>();
		this.maxCapacity = maxCapacity;
	}
	/**
	 * @return list of users who are affiliated to the hospitall
	 */
	public List<User> getUsers(){
		return users;
	}
	/**
	 * @return maxCapacity
	 */
	public int getMaxCapacity() {
		return maxCapacity;
	}
	@Override
	public String toString() {
		return "SitePoint "+getId() + " ("+getX()+" , "+getY()+") Users ="+users.size();
	}
	/**
	 * @return true if the hospital is saturated 
	 */
	public boolean isSaturated(){
		if (users.size() >= maxCapacity){
			return true;
		}
		return false;
	}
	/**
	 * @return number of room available in the hospital
	 */
	public int getAvailableRoom(){
		return this.maxCapacity-this.users.size();

	}
	/**
	 * @return percentage of available room in the hospital
	 */
	public double getPercentAvailable(){
		if (this.maxCapacity <= this.users.size() && this.maxCapacity>0){
			return (1-this.users.size()/this.maxCapacity)*100;
		}
		return 0;
	}
	/**
	 * add an user in the list of users
	 * @param u
	 */
	public void addUsers(User u){
		if (u != null){
			users.add(u);
		}
	}
	/**
	 * remove an user from the list
	 */
	public void removeUsers(User u){
		users.remove(u);
	}
}