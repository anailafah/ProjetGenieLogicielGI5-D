package Model;

import java.io.Serializable;
import java.util.List;
/**
 * represent a voronoi zone
*/

public class HospitalZone implements Serializable{
    private static final long serialVersionUID = 5L;
	private List<Point> vertices;
    private double area;
    private double density;
    private double minDistance;
    private double maxDistance;
    private double avgDistance;
    private int redirectedCount;
    private Hospital centerHospital;
    private int nbUser;
    /**
     * construction of a hospital zone
     * @param hospital 
     * @param vertices
     */
    public HospitalZone(List<Point> vertices,Hospital h) {
        this.vertices = vertices;
        this.redirectedCount=0;
        this.centerHospital=h;
        this.nbUser=h.getUsers().size();
        computeStats();
        this.density=nbUser/area;

    }
    /**
     * computes all statistics for this zone
     */
    private void computeStats() {
        List<User> users = this.centerHospital.getUsers();
        redirectedCount = 0;

        if (users.isEmpty()) {
            minDistance = 0;
            maxDistance = 0;
            avgDistance = 0;
        } else {
            double min = Double.MAX_VALUE, max = 0, sum = 0;
            for (User u : users) {
                double d = dist(u);
                if (d < min) min = d;
                if (d > max) max = d;
                sum += d;
                if (u.getIsRedirected()) redirectedCount++;
            }
            this.minDistance = min;
            this.maxDistance = max;
            this.avgDistance = sum / users.size();
        }
        this.area = computePolygonArea();
    }
    /**
     * @return nbUser
     */
    public int getNbUser(){
        return nbUser;
    }

    /**
     * @return density
     */
    public double getDensity(){
        return density;
    }
        
    /**
     * @param u users
     * @return distance between an user and hospital
     */
     private double dist(User u) {
        double dx = this.centerHospital.getX() - u.getX();
        double dy = this.centerHospital.getY() - u.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Computes the polygon area using the shoelace formula.
     * @return the area
    */
    private double computePolygonArea() {
        if (vertices == null || vertices.size() < 3) return 0;
        double sumRight = 0;
        double sumLeft = 0;
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % n);
            sumRight += p1.getX() * p2.getY(); 
        }
        for (int i = 0; i < n; i++) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % n);
            sumLeft += p2.getX() * p1.getY();
        }
        return Math.abs(sumRight-sumLeft) / 2.0;
    }
    /**
     * set an hospital as the center of the zone
     * @param h hospital
    */
    public void setCenterHospital(Hospital h){
        this.centerHospital=h;
    }
    /**
     * @return centerHospital
    */
    public Hospital getCenterHospital(){
        return centerHospital;
    }

    /** 
     * Refreshes statistics after modifications. 
    */
    public void refresh() { 
        computeStats(); 
    }
    /**
     * @return list of vertices of the zone
    */
    public List<Point> getVertices(){ 
        return vertices; 
    }
    /**
     * @return area of the zone
    */
    public double getArea(){ 
        return area;
    }
    /**
     * @return minDistance
    */
    public double getMinDistance(){
        return minDistance; 
    }
    /**
     * @return maxDistance
    */
    public double getMaxDistance(){
        return maxDistance; 
    }
    /**
     * @return avgDistance
    */
    public double getAvgDistance(){
        return avgDistance; 
    }
    /**
     * @return the count of user redirected in the zone
    */
    public int getRedirectedCount(){ 
        return redirectedCount; 
    }
}


