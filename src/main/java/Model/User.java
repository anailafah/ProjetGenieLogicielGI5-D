package Model;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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
		this.setRedirectionRank(this.setRedirection());
		if(this.redirectionRank!=0){
			this.setIsRedirected(true);
		}

	}
	private int setRedirection(){
		int cpt=0;
		if(closestSite.isSaturated()){
			for(Hospital h: nextHospitals){
				if(h.isSaturated()!=true){
					this.setClosestSite(h);
					this.setRedirectionRank(cpt);
					h.addUsers(this);
					return redirectionRank;
				}
				cpt++;
			}
		}
		return 0;
	}
	/**
	 * test
	 * @param args
	 */

    public static void main(String[] args) {

        Hospital h1 = new Hospital(1,"hop1" ,0, 0,5);
        Hospital h2 = new Hospital(2,"hop2", 10, 10,50);
        Hospital h3 = new Hospital(3,"hop3", 5, 5,3);

       
        User user = new User(100, 2, 3);

        System.out.println("User initial:");
        System.out.println("Closest hospital: " + user.getClosestSite());
        System.out.println("Is redirected: " + user.getIsRedirected());
        System.out.println("Next hospitals: " + user.getNextHospitals());

        
        user.setClosestSite(h1);
        System.out.println("\nAfter setting closest hospital:");
        System.out.println("Closest hospital: " + user.getClosestSite());

        user.setNextHospitals(Arrays.asList(h1, h3, h2));

        System.out.println("\nNext hospitals list:");
        for (Hospital h : user.getNextHospitals()) {
            System.out.println("Hospital id: " + h.getId() + " (" + h.getX() + ", " + h.getY() + ")");
        }

        System.out.println("\nRedirect flag (before): " + user.getIsRedirected());

        System.out.println("Redirect flag (after manual change if implemented): " + user.getIsRedirected());
    }
}
