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
	 * @param id id of user
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public User(int id,double x,double y) {
		super(id,x,y);
		this.closestSite=null;
		this.nextHospitals=new ArrayList<>();
		this.isRedirected=false;
		this.redirectionRank=-1;
	}
	/**
	 * return the rank of the hospital in which user is redirectes
	 * @return redirectionRank
	*/
	public int getRedirectionRank(){
		return redirectionRank;
	}
	/**
	 * set the rank of the hospital in which user is redirected
	 * @param r rank
	*/
	private void setRedirectionRank(int r){
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
	private void setIsRedirected(boolean redirected){
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
	 * @param site hospital
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
	 * @param listH list of hospitals
	 */
	public void setNextHospitals(List<Hospital> listH){
		if (listH == null) throw new IllegalArgumentException("Hospital list can't be empty");
		this.nextHospitals=new ArrayList<>(listH);
		

	}
	/**
	 * set the redirection of an user
	 * @return rank if the user is redirected rank 0 if not
	*/
	public int setRedirection(){
		int rank=0;
		for(Hospital h: this.nextHospitals){
			if(h.isSaturated()!=true){
				this.setClosestSite(h);
				this.setRedirectionRank(rank);
				this.setIsRedirected(rank!=0);
				return rank;
			}
			rank++;
		}
    	return 0;
	}
	/**
	 * @return a string that contains user
	*/
	@Override
	public String toString() {
		if (this.getIsRedirected()==true){
			return "userId: "+getId()+"("+getX()+","+getY()+")"+ "hospital : "+getClosestSite() + "redirerction:"+getRedirectionRank();
			
		}
		return "userId: "+getId()+"("+getX()+","+getY()+")"+ "hospital : "+getClosestSite();
	}
}
