package Algorithm;

import Model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Bowyer-Watson algorithm for Delaunay triangulation.
 * And build voronoiZones
 */
public class TriangulationDelaunay implements VoronoiEngine {
    private VoronoiMap map;
    private double width, height;

    /**
     * Construction : creates the engine with canvas dimensions.
     * @param width canvas width
     * @param height canvas height
     */
    public TriangulationDelaunay(double width, double height) {
        this.map = new VoronoiMap();
        this.width = width;
        this.height = height;
    }
    @Override
    public User addUser(double x,double y){
        User u = new User(map.generateId(), x, y);
        map.addUsertot(u);
        recompute();
        return u;
    }
    @Override
    public void removeUser(User u){
        map.removeUsertot(u);
        recompute();
    }
  
    /**
     * Adds a hospital to the map and recomputes the triangulation.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param name hospital name
     * @param maxCapacity maximum patient capacity
     * @return the created Hospital
     */
    @Override
    public Hospital addHospital(double x, double y,String name, int maxCapacity) {
        Hospital h = new Hospital(map.generateId(),name ,x, y, maxCapacity);
        map.addHospital(h);
        recompute();
        return h;
    }

    /**
     * Removes a hospital from the map and recomputes the triangulation.
     * @param hospital the hospital to remove
     */
    @Override
    public void removeHospital(Hospital hospital) {
        map.removeHospital(hospital);
        recompute();
    }

    /**
     * Moves a hospital to a new position and recomputes the triangulation.
     * @param hospital the hospital to move
     * @param x new x-coordinate
     * @param y new y-coordinate
     */
    @Override
    public void moveHospital(Hospital hospital, double x, double y) {
        hospital.setX(x);
        hospital.setY(y);
        recompute();
    }

    /**
     * Returns the current Delaunay triangles.
     * @return list of triangles
     */
    @Override
    public List<Triangle> getTriangles() { 
        return map.getTriangles(); 
    }

    /**
     * Returns the current Voronoi zones.
     * @return list of hospital zones
     */
    @Override
    public List<HospitalZone> getZones() {
        return map.getZones(); 
    }

    /**
     * Returns the full map.
     * @return the VoronoiMap
     */
    @Override
    public VoronoiMap getMap() { 
        return map; 
    }

    /**
     * Finds the nearest hospital to given coordinates.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the nearest Hospital, or null if no hospitals exist
     */
    @Override
    public Hospital getNearestHospital(double x, double y) {
        Hospital nearest = null;
        double minDist = Double.MAX_VALUE;
        Point p = new Point(-1, x, y);   
        for (Hospital h : map.getHospitals()) {
            double d = GeometryFunc.distance(h, p);
            if (d < minDist) {
                minDist = d;
                nearest = h; 
            }
        }
        return nearest;
    }

    /**
     * Recomputes the full Delaunay triangulation using Bowyer-Watson and build Voronoi Zones.
     * Called every time a hospital is added, removed, or moved.
     * Also callable after a bulk import to refresh the full geometry.
     */
    public void recompute() {
        map.clearComputed();
        List<Hospital> hospitals = map.getHospitals();
        if (hospitals.size() < 3) return;

        Hospital stA = new Hospital(-1,"stA", -width * 10, -height * 10, 0);
        Hospital stB = new Hospital(-2,"stB" , width * 10, -height * 10, 0);
        Hospital stC = new Hospital(-3, "stC" ,0.0,         height * 10, 0);
        List<Triangle> triangulation = new ArrayList<>();
        triangulation.add(new Triangle(stA, stB, stC));

        for (Hospital hospital : hospitals) {

            List<Triangle> badTriangles = new ArrayList<>();
            for (Triangle t : triangulation) {
                if (hospital.isInCircumcircle(t.getA(),t.getB(),t.getC())) badTriangles.add(t);
            }

            List<Hospital[]> polygon = new ArrayList<>();
            for (Triangle t : badTriangles) {
                Hospital[][] edges = { {t.getA(), t.getB()}, {t.getB(), t.getC()}, {t.getC(), t.getA()} }; //liste des 
                for (Hospital[] edge : edges) {
                    boolean shared = false; 
                    for (Triangle other : badTriangles) {
                        if (other == t) continue; 
                        if (triangleContainsEdge(other, edge[0], edge[1])) {
                            shared = true; 
                            break;
                        }
                    }
                    if (!shared) polygon.add(edge);
                }
            }

            triangulation.removeAll(badTriangles);

            for (Hospital[] edge : polygon){
                triangulation.add(new Triangle(edge[0], edge[1], hospital));
            }
        }
        List<Triangle> toRemoveSt = new ArrayList<>();
        for(Triangle t : triangulation){
            if( t.getA() == stA || t.getA() == stB || t.getA() == stC ||
                t.getB() == stA || t.getB() == stB || t.getB() == stC ||
                t.getC() == stA || t.getC() == stB || t.getC() == stC){
                    
                toRemoveSt.add(t);
            }
        }
        triangulation.removeAll(toRemoveSt);
        map.setTriangles(triangulation);
        buildVoronoiZones();
        updatePatientLinks();   
    }

    /**
     * sort hospital by dist with a bubble sort
     * Assigns each patient to their nearest available hospital. 
     * If the nearest is saturated, redirects to the next closest one.
     */
    private void updatePatientLinks() {
        for (Hospital h : map.getHospitals()) h.getUsers().clear();

        for (User u : map.getUserTot()) {

            List<Hospital> byDistance = new ArrayList<Hospital>(map.getHospitals());
            
            for(int i=0;i<byDistance.size()-1;i++){
                for(int j=0;j<byDistance.size()-1-i;j++){
                    double distA = GeometryFunc.distance(u, byDistance.get(j)); 
                    double distB = GeometryFunc.distance(u, byDistance.get(j+1)); 
                    if (Double.compare(distA, distB)>0){
                        Hospital temp = byDistance.get(j);
                        byDistance.set(j,byDistance.get(j+1));
                        byDistance.set(j+1,temp);
                    }
                }
            }
            u.setNextHospitals(byDistance);
            u.setClosestSite(byDistance.get(0));
            u.setRedirection();  // met à jour closestSite

            if (u.getClosestSite() != null) {
                 u.getClosestSite().addUsers(u);
            }
        }
    }
    /**
     * Checks if a triangle contains a given edge (pair of hospitals).
     * Used to detect shared edges between bad triangles.
     * @param t the triangle to check
     * @param h1 first endpoint of the edge
     * @param h2 second endpoint of the edge
     * @return true if the triangle contains both h1 and h2 as vertices
     */
    private boolean triangleContainsEdge(Triangle t, Hospital h1, Hospital h2) {
        Hospital[] verts = {t.getA(), t.getB(), t.getC()};
        boolean hasH1 = false, hasH2 = false;
        for (Hospital h : verts) {
            if (h == h1) hasH1 = true; 
            if (h == h2) hasH2 = true; 
        }
        return hasH1 && hasH2;
    }

    /**
    * Builds Voronoi zones from the Delaunay triangulation.
    * For each hospital, collects the circumcenters of all triangles
    * that contain it as a vertex, then sorts them to form a polygon.
    */
    private void buildVoronoiZones() {
        List<HospitalZone> zones = new ArrayList<>();
        List<Triangle> triangles = map.getTriangles();

        for (Hospital hospital : map.getHospitals()) {
            List<Triangle> adjacent = new ArrayList<>();
            for (Triangle t : triangles) {
                if (t.getA() == hospital ||
                    t.getB() == hospital ||
                    t.getC() == hospital) {
                    adjacent.add(t);
                }
            }
            List<Point> vertices = new ArrayList<>();
            for (Triangle t : adjacent) {
                vertices.add(t.getCircumcenter());
            }
            vertices = sortPolygonVertices(vertices, hospital);

            if (!vertices.isEmpty()) {
             zones.add(new HospitalZone(vertices,hospital));
            }
        }
        map.setZones(zones);
    }

    /**
    * Sorts polygon vertices in clockwise order around a center point.
    * Necessary to draw a proper polygon from circumcenters.
    * @param vertices the unsorted list of vertices
    * @param center the hospital around which to sort
    * @return the sorted list of vertices
    */
    private List<Point> sortPolygonVertices(List<Point> vertices, Hospital center) {
        if (vertices.size() <= 1) return vertices;
        vertices.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - center.getY(), p1.getX() - center.getX());
            double angle2 = Math.atan2(p2.getY() - center.getY(),p2.getX() - center.getX()
            );
        return Double.compare(angle1, angle2);
        });
    return vertices;
    } 
}

