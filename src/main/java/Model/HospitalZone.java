package Model;

import java.util.List;

public class HospitalZone extends Hospital{
	private List<Point> vertices;
    private double area;
    private double minDistance;
    private double maxDistance;
    private double avgDistance;
    private int redirectedCount;
    /**
     * construction of a hospital zone
     * @param hospital
     * @param vertices
     */
    public HospitalZone(Hospital hospital, List<Point> vertices) {
        super(hospital.getId(), hospital.getX(), hospital.getY(),hospital.getMaxCapacity());
        for (User u : hospital.getUsers()) addUsers(u);
        this.vertices = vertices;
        computeStats();
    }
    /**
     * computes all statistics for this zone
     */
    private void computeStats() {
        List<User> users = getUsers();
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
            minDistance = min;
            maxDistance = max;
            avgDistance = sum / users.size();
        }
        area = computePolygonArea();
    }
    /**
     * @param u users
     * @return distance between an user and hospital
     */
     private double dist(User u) {
        double dx = getX() - u.getX();
        double dy = getY() - u.getY();
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
    

    /** Refreshes statistics after modifications. */
    public void refresh() { 
        computeStats(); 
    }

    public List<Point> getVertices(){ 
        return vertices; 
    }
    public double getArea(){ 
        return area;
    }
    public double getMinDistance(){
        return minDistance; 
    }
    public double getMaxDistance(){
        return maxDistance; 
    }
    public double getAvgDistance(){
        return avgDistance; 
    }
    public int getRedirectedCount(){ 
        return redirectedCount; 
    }
    public int getUsersCount(){ 
        return getUsers().size(); 
    }
    public double getDensity(){ 
        return area > 0 ? getUsersCount() / area : 0; 
    }
}


