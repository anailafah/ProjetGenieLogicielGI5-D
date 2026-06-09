package Model;
import java.util.List;
import java.util.ArrayList;


/**
 * Represent an user (patient) as a point 
 */
public class User extends Point {
	private static final long serialVersionUID = 3L;
	private Hospital closestSite;
	private List<Hospital> nextHospitals;
	private boolean isRedirected;
	private int redirectionRank;
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
		this.redirectionRank=-1;
	}
	public int getRedirectionRank(){
		return redirectionRank;
	}
	public void setRedirectionRank(int r){
		this.redirectionRank=r;
	}
	/**
	 * @return true if the user is redirected to another hospital
	 */
	public boolean getIsRedirected(){
		return isRedirected;
	}

	/**
	 * Sets whether this user has been redirected from their nearest hospital.
	 * @param redirected true if redirected
	 */
	public void setIsRedirected(boolean redirected){
		this.isRedirected = redirected;
	}
	/**
	 * @return the closest hospital for the user
	 */
	public Hospital getClosestSite() {
		return closestSite;
	}
	/**
	 * set the closest hospital to the user
	 * @param site
	 */
	public void setClosestSite(Hospital site) {
		this.closestSite=site;
	}
	/**
	 * @return a list of the closest hospitals ordered by distance
	 */
	public List<Hospital> getNextHospitals(){
		return nextHospitals;
	}
	/**
	 * set the list of the closest hospitals 
	 * @param listH
	 */
	public void setNextHospitals(List<Hospital> listH){
		if (listH == null) throw new IllegalArgumentException("Hospital list can't be empty");
		this.nextHospitals=new ArrayList<>(listH);
		

	}
	public int setRedirection(){
		int rank=0;
		if(nextHospitals.isEmpty()){
			this.isRedirected = false;
			this.redirectionRank=0;
			return 0;
		}
		for(Hospital h: nextHospitals){
			if(h.isSaturated()!=true){
				this.setClosestSite(h);
				this.setRedirectionRank(rank);
				h.addUsers(this);
				return rank;
			}
			rank++;
		}
		this.closestSite     = nextHospitals.get(0);
    	this.redirectionRank = 0;
    	this.isRedirected    = false;
    	return 0;
	}
}
