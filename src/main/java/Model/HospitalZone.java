package Model;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a Voronoi zone associated with a hospital.
 */
public class HospitalZone implements Serializable {

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
     * Creates a hospital zone with its vertices and center hospital.
     *
     * @param vertices the list of vertices that define the zone
     * @param h the hospital associated with the zone
     */
    public HospitalZone(List<Point> vertices, Hospital h) {
        this.vertices = vertices;
        this.redirectedCount = 0;
        this.centerHospital = h;
        this.nbUser = h.getUsers().size();
        computeStats();

        if (area == 0) {
            this.density = 0;
        } else {
            this.density = nbUser / area;
        }
    }

    /**
     * Computes all statistics for this zone.
     */
    private void computeStats() {
        List<User> users = this.centerHospital.getUsers();
        redirectedCount = 0;

        if (users.isEmpty()) {
            minDistance = 0;
            maxDistance = 0;
            avgDistance = 0;
        } else {
            double min = Double.MAX_VALUE;
            double max = 0;
            double sum = 0;

            for (User u : users) {
                double d = dist(u);

                if (d < min) {
                    min = d;
                }

                if (d > max) {
                    max = d;
                }

                sum += d;

                if (u.getIsRedirected()) {
                    redirectedCount++;
                }
            }

            this.minDistance = min;
            this.maxDistance = max;
            this.avgDistance = sum / users.size();
        }

        this.area = computePolygonArea();
    }

    /**
     * Returns the number of users assigned to the zone.
     *
     * @return the number of users
     */
    public int getNbUser() {
        return nbUser;
    }

    /**
     * Returns the density of users in the zone.
     *
     * @return the user density
     */
    public double getDensity() {
        return density;
    }

    /**
     * Computes the distance between a user and the center hospital.
     *
     * @param u the user used for the distance computation
     * @return the distance between the user and the center hospital
     */
    private double dist(User u) {
        double dx = this.centerHospital.getX() - u.getX();
        double dy = this.centerHospital.getY() - u.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Computes the polygon area using the shoelace formula.
     *
     * @return the area of the polygon
     */
    private double computePolygonArea() {
        if (vertices == null || vertices.size() < 3) {
            return 0;
        }

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

        return Math.abs(sumRight - sumLeft) / 2.0;
    }

    /**
     * Updates the center hospital of the zone.
     *
     * @param h the new center hospital
     */
    public void setCenterHospital(Hospital h) {
        this.centerHospital = h;
    }

    /**
     * Returns the center hospital of the zone.
     *
     * @return the center hospital
     */
    public Hospital getCenterHospital() {
        return centerHospital;
    }

    /**
     * Refreshes statistics after modifications.
     */
    public void refresh() {
        computeStats();
    }

    /**
     * Returns the vertices of the zone.
     *
     * @return the list of vertices of the zone
     */
    public List<Point> getVertices() {
        return vertices;
    }

    /**
     * Returns the area of the zone.
     *
     * @return the area of the zone
     */
    public double getArea() {
        return area;
    }

    /**
     * Returns the minimum distance between the center hospital and users.
     *
     * @return the minimum distance
     */
    public double getMinDistance() {
        return minDistance;
    }

    /**
     * Returns the maximum distance between the center hospital and users.
     *
     * @return the maximum distance
     */
    public double getMaxDistance() {
        return maxDistance;
    }

    /**
     * Returns the average distance between the center hospital and users.
     *
     * @return the average distance
     */
    public double getAvgDistance() {
        return avgDistance;
    }

    /**
     * Returns the number of redirected users in the zone.
     *
     * @return the count of redirected users in the zone
     */
    public int getRedirectedCount() {
        return redirectedCount;
    }
}