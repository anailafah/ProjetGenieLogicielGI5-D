package Algorithm;

import Model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the Bowyer-Watson algorithm for Delaunay triangulation.
 */
public class TriangulationDelaunay implements VoronoiEngine {
    private VoronoiMap map;
    // Les dimensions du canvas 
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
  
    /**
     * Adds a hospital to the map and recomputes the triangulation.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param name hospital name
     * @param maxCapacity maximum patient capacity
     * @return the created Hospital
     */
    @Override
    public Hospital addHospital(double x, double y, int maxCapacity) {
        Hospital h = new Hospital(map.generateId(), x, y, maxCapacity);
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

    // ALGORITHME DE BOWYER-WATSON with voronoizone building 
    /**
     * Recomputes the full Delaunay triangulation using Bowyer-Watson and build Voronoi Zones.
     * Called every time a hospital is added, removed, or moved.
     */
    private void recompute() {
        map.clearComputed();
        List<Hospital> hospitals = map.getHospitals();
        if (hospitals.size() < 3) return;

        // Step 1 : create a super-triangle that contains all hospitals
        Hospital stA = new Hospital(-1, -width * 10, -height * 10, 0);
        Hospital stB = new Hospital(-2,  width * 10, -height * 10, 0);
        Hospital stC = new Hospital(-3,  0.0,         height * 10, 0);
        List<Triangle> triangulation = new ArrayList<>();
        triangulation.add(new Triangle(stA, stB, stC));

        //Step 2 : add each hospital 1 by 1
        for (Hospital hospital : hospitals) {

            //Step 3: find badTriangles (triangles which contains a hospital in his circumcircle)
            List<Triangle> badTriangles = new ArrayList<>();
            for (Triangle t : triangulation) {
                if (hospital.isInCircumcircle(t.getA(),t.getB(),t.getC()))
                    badTriangles.add(t);
            }

            //  ÉTAPE 4 : Trouver les bords du trou 
            // Un bord du trou = un bord qui appartient à UN SEUL mauvais triangle
            // Si un bord appartient à DEUX mauvais triangles → il disparaît dans le trou
            List<Hospital[]> polygon = new ArrayList<>();
            for (Triangle t : badTriangles) {
                Hospital[][] edges = { {t.getA(), t.getB()}, {t.getB(), t.getC()}, {t.getC(), t.getA()} }; //liste des 
                for (Hospital[] edge : edges) {
                    boolean shared = false; 
                    // On vérifie si ce bord est partagé avec un AUTRE mauvais triangle
                    for (Triangle other : badTriangles) {
                        if (other == t) continue; // On ne compare pas le triangle avec lui-même
                        if (triangleContainsEdge(other, edge[0], edge[1])) {
                            shared = true; // Le bord est partagé → il disparaît dans le trou
                            break;
                        }
                    }
                    // Si le bord n'est PAS partagé → c'est un bord du trou → on le garde
                    if (!shared) polygon.add(edge);
                }
            }

            //Step 5: remove bad triangle
            triangulation.removeAll(badTriangles);

            //  ÉTAPE 6 : Reboucher le trou 
            // Pour chaque bord du trou, on crée un nouveau triangle
            // qui relie ce bord au nouvel hôpital
            for (Hospital[] edge : polygon)
                triangulation.add(new Triangle(edge[0], edge[1], hospital));
        }

        //  ÉTAPE 7 : Supprimer le super-triangle 
        // On efface tous les triangles qui touchent un sommet du super-triangle
        // car ces triangles ne font pas partie de la vraie triangulation
        triangulation.removeIf(t ->
            t.getA() == stA || t.getA() == stB || t.getA() == stC ||
            t.getB() == stA || t.getB() == stB || t.getB() == stC ||
            t.getC() == stA || t.getC() == stB || t.getC() == stC
        );

        // On sauvegarde les triangles calculés dans la carte
        map.setTriangles(triangulation);

        // On construit les zones Voronoï depuis les triangles
        buildVoronoiZones();

        // On recalcule les liens patient → hôpital car les zones ont changé
        updatePatientLinks();

        
    }
    /**
     * Assigns each patient to their nearest available hospital.
     * If the nearest is saturated, redirects to the next closest one.
     */
    private void updatePatientLinks() {
        // remise a 0 de la liste d'utilisateurs de chaque hopital
        for (Hospital h : map.getHospitals())
            h.getUsers().clear();
        for (User u : map.getUserTot()) {

            // On trie TOUS les hôpitaux du plus proche au plus loin
            // par rapport à ce patient
            List<Hospital> byDistance = map.getHospitals().stream()
                .sorted((a, b) -> Double.compare(
                    GeometryFunc.distance(u, a), // distance patient → hôpital a
                    GeometryFunc.distance(u, b)  // distance patient → hôpital b
                ))
                .collect(Collectors.toList());
            
            u.setNextHospitals(byDistance);

            // On cherche le premier hôpital NON saturé dans la liste
            Hospital assigned = byDistance.stream()
                .filter(h -> !h.isSaturated()) // On ignore les hôpitaux saturés
                .findFirst()                    // On prend le premier disponible
                .orElse(byDistance.isEmpty() ? null : byDistance.get(0));

            u.setClosestSite(assigned);
            // setAssignedHospital calcule aussi automatiquement isRedirected :
            // si assigned != byDistance[0] → le patient a été redirigé

            if (assigned != null)
                assigned.addUsers(u);
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

            // find all triangle that contains hospital
            List<Triangle> adjacent = new ArrayList<>();
            for (Triangle t : triangles) {
                if (t.getA() == hospital ||
                    t.getB() == hospital ||
                    t.getC() == hospital) {
                    adjacent.add(t);
                }
            }
            // retrieve all circumcenters 
            List<Point> vertices = new ArrayList<>();
            for (Triangle t : adjacent) {
                vertices.add(t.getCircumcenter());
            }
            // sort vertices in  clockwise direction in order to form a correct polygone 
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

        // On calcule l'angle de chaque sommet par rapport au centre et on trie dans l'ordre croissant des angles
        vertices.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - center.getY(), p1.getX() - center.getX());
            double angle2 = Math.atan2(p2.getY() - center.getY(),p2.getX() - center.getX()
            );
        return Double.compare(angle1, angle2);
        });
    return vertices;
    }
 
}

