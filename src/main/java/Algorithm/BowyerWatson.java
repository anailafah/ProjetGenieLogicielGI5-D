package Algorithm;

import Model.*;
import java.util.*;

/**
 * Implements the Bowyer-Watson algorithm for Delaunay triangulation.
 */
public class BowyerWatson implements VoronoiInterface {

    private VoronoiMap map;
    private double width, height;

    /**
     * Constructs the engine with the canvas dimensions.
     * @param width canvas width
     * @param height canvas height
     */
    public BowyerWatson(double width, double height) {
        this.map = new VoronoiMap();
        this.width = width;
        this.height = height;
    }

    @Override
    public Hospital addHospital(double x, double y,int capacity) {
        Point p = new Point(map.generateId(), x, y);
        Hospital h = new Hospital(p.getId(),p.getX(),p.getY(),capacity);
        map.addHospital(h);
        recompute();
        return h;
    }

    @Override
    public void removeHospital(Hospital h) {
        map.removeHospital(h);
        recompute();
    }

    @Override
    public void moveHospital(Hospital h, double x, double y) {
        h.setX(x);
        h.setY(y);
        recompute();
    }

    @Override
    public List<Triangle> getTriangles() { 
        return map.getTriangles(); 
    }

    @Override
    public List<HospitalZone> getZones() {
        return map.getZones(); 
    }

    @Override
    public VoronoiMap getMap() { 
        return map; 
    }

    @Override
    public Hospital getNearestHospital(double x, double y) {
        Hospital nearest = null;
        double minDist = Double.MAX_VALUE;
        Point p = new Point(-1, x, y);
        for (Hospital h : map.getHospitals()) {
            double d = GeometryFunc.distance(h, p);
            if (d < minDist) { minDist = d; nearest = h; }
        }
        return nearest;
    }

    /**
     * Recomputes the full Delaunay triangulation using Bowyer-Watson.
     */
    private void recompute() {
        map.clearComputed();
        List<Hospital> hospitals = map.getHospitals();
        if (hospitals.size() < 3) return;

        // Super-triangle large enough to contain all points
        Hospital hA = new Hospital(-1, -width * 10, -height * 10,0);
        Hospital hB = new Hospital(-2, width * 10, -height * 10,0);
        Hospital hC = new Hospital(-3, 0.0, height * 10,0);

        List<Triangle> triangulation = new ArrayList<>();
        triangulation.add(new Triangle(hA, hB, hC));

        for (Hospital h : hospitals) {
            List<Triangle> badTriangles = new ArrayList<>();
            for (Triangle t : triangulation) {
                //if (t.isInCircumcircle(hospitals.getPosition()))
                    badTriangles.add(t);
            }

            // Find boundary polygon of bad triangles
            List<Hospital[]> polygon = new ArrayList<>();
            for (Triangle t : badTriangles) {
                Hospital[][] edges = {{t.getA(), t.getB()}, {t.getB(), t.getC()}, {t.getC(), t.getA()}};
                for (Hospital[] edge : edges) {
                    boolean shared = false;
                    for (Triangle other : badTriangles) {
                        if (other == t) continue;
                        if (triangleContainsEdge(other, edge[0], edge[1])) { shared = true; break; }
                    }
                    if (!shared) polygon.add(edge);
                }
            }

            triangulation.removeAll(badTriangles);
            for (Hospital[] edge : polygon)
                triangulation.add(new Triangle(edge[0], edge[1], h));
        }

        // Remove triangles sharing vertices with super-triangle
        triangulation.removeIf(t ->
            t.getA() == hA || t.getA() == hB || t.getA() == hC ||
            t.getB() == hA || t.getB() == hB || t.getB() == hC ||
            t.getC() == hA || t.getC() == hB || t.getC() == hC
        );

        map.setTriangles(triangulation);
        updateUsersLinks();
    }

    /**
     * Checks if a triangle contains a given edge.
     */
    private boolean triangleContainsEdge(Triangle t, Hospital s1, Hospital s2) {
        Hospital[] edges = {t.getA(), t.getB(), t.getC()};
        boolean hasS1 = false, hasS2 = false;
        for (Hospital h : edges) {
            if (h == s1) hasS1 = true;
            if (h == s2) hasS2 = true;
        }
        return hasS1 && hasS2;
    }

    /**
     * Updates each user point's link to its nearest site.
     */
    private void updateUsersLinks() {
    for (Hospital h : map.getHospitals()) h.getUsers().clear();

    for (User u : map.getUserTot()) {
        User user = (User) u;

        // Trier tous les hôpitaux par distance
        List<Hospital> byDistance = map.getHospitals().stream()
            .map(s -> (Hospital) s)
            .sorted((a, b) -> Double.compare(
                GeometryFunc.distance(user, a),
                GeometryFunc.distance(user, b)
            ))
            .collect(java.util.stream.Collectors.toList());

            // Stocker la liste ordonnée dans le patient
            user.setNextHospitals(byDistance);
            // Affecter au premier non saturé
            Hospital assigned = byDistance.stream()
                .filter(h -> !h.isSaturated())
                .findFirst()
                .orElse(byDistance.isEmpty() ? null : byDistance.get(0));

            user.setClosestSite(assigned);
            if (assigned != null) assigned.addUsers(user);
        }
    }
}