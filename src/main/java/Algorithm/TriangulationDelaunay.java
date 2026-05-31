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
        // On crée une carte vide
        this.map = new VoronoiMap();
        // On mémorise les dimensions pour le super-triangle
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
        // On crée un nouvel hôpital avec un ID unique généré par la carte
        Hospital h = new Hospital(map.generateId(), x, y, maxCapacity);
        // On l'ajoute à la liste des hôpitaux
        map.addHospital(h);
        // On recalcule toute la triangulation car un point a changé
        recompute();
        return h;
    }

    /**
     * Removes a hospital from the map and recomputes the triangulation.
     * @param hospital the hospital to remove
     */
    @Override
    public void removeHospital(Hospital hospital) {
        // On retire l'hôpital de la liste
        map.removeHospital(hospital);
        // On recalcule car un point a disparu
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
        // On change juste les coordonnées de l'hôpital existant
        hospital.setX(x);
        hospital.setY(y);
        // On recalcule car un point a bougé
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

    // ALGORITHME DE BOWYER-WATSON
    /**
     * Recomputes the full Delaunay triangulation using Bowyer-Watson and build Voronoi Zones.
     * Called every time a hospital is added, removed, or moved.
     */
    private void recompute() {
        // On efface tous les anciens triangles calculés
        map.clearComputed();
        List<Hospital> hospitals = map.getHospitals();

        // S'il y a moins de 3 hôpitaux, on ne peut pas faire de triangle donc on s'arrête là
        if (hospitals.size() < 3) return;

        // ÉTAPE 1 : Créer le super-triangle 
        // Le super-triangle doit contenir TOUS les hôpitaux donc 10* plus grande que le canva
        Hospital stA = new Hospital(-1, -width * 10, -height * 10, 0);
        Hospital stB = new Hospital(-2,  width * 10, -height * 10, 0);
        Hospital stC = new Hospital(-3,  0.0,         height * 10, 0);

        // La triangulation commence avec juste ce super-triangle
        List<Triangle> triangulation = new ArrayList<>();
        triangulation.add(new Triangle(stA, stB, stC));

        //  ÉTAPE 2 : Ajouter chaque hôpital un par un 
        for (Hospital hospital : hospitals) {

            //  ÉTAPE 3 : Trouver les mauvais triangles 
            // Un mauvais triangle = le nouvel hôpital est dans son cercle circonscrit
            List<Triangle> badTriangles = new ArrayList<>();
            for (Triangle t : triangulation) {
                if (hospital.isInCircumcircle(t.getA(),t.getB(),t.getC()))
                    badTriangles.add(t);
                // isInCircumcircle() vérifie si hospital est dans le cercle circonscrit du triangle t
            }

            //  ÉTAPE 4 : Trouver les bords du trou 
            // Un bord du trou = un bord qui appartient à UN SEUL mauvais triangle
            // Si un bord appartient à DEUX mauvais triangles → il disparaît dans le trou
            List<Hospital[]> polygon = new ArrayList<>();

            for (Triangle t : badTriangles) {
                // Chaque triangle a 3 bords (arêtes)
                Hospital[][] edges = {
                    {t.getA(), t.getB()}, // bord entre sommet A et sommet B
                    {t.getB(), t.getC()}, // bord entre sommet B et sommet C
                    {t.getC(), t.getA()}  // bord entre sommet C et sommet A
                };

                for (Hospital[] edge : edges) {
                    boolean shared = false; // Ce bord est-il partagé ?

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

            //  ÉTAPE 5 : Supprimer les mauvais triangles 
            // On efface tous les mauvais triangles de la triangulation
            triangulation.removeAll(badTriangles);

            //  ÉTAPE 6 : Reboucher le trou 
            // Pour chaque bord du trou, on crée un nouveau triangle
            // qui relie ce bord au nouvel hôpital
            for (Hospital[] edge : polygon)
                triangulation.add(new Triangle(edge[0], edge[1], hospital));
                // Nouveau triangle : edge[0] ─ edge[1] ─ hospital
        }

        //  ÉTAPE 7 : Supprimer le super-triangle 
        // On efface tous les triangles qui touchent un sommet du super-triangle
        // car ces triangles ne font pas partie de la vraie triangulation
        triangulation.removeIf(t ->
            t.getA() == stA || t.getA() == stB || t.getA() == stC ||
            t.getB() == stA || t.getB() == stB || t.getB() == stC ||
            t.getC() == stA || t.getC() == stB || t.getC() == stC
            // On compare avec == car c'est bien le même objet Java qu'on cherche
        );

        // On sauvegarde les triangles calculés dans la carte
        map.setTriangles(triangulation);

        // On construit les zones Voronoï depuis les triangles
        buildVoronoiZones();

        // On recalcule les liens patient → hôpital car les zones ont changé
        updatePatientLinks();

        
    }

    // AFFECTATION DES PATIENTS

    /**
     * Assigns each patient to their nearest available hospital.
     * If the nearest is saturated, redirects to the next closest one.
     */
    private void updatePatientLinks() {
        // On remet à zéro toutes les listes de patients des hôpitaux
        // car on va tout recalculer depuis le début
        for (Hospital h : map.getHospitals())
            h.getUsers().clear();

        // Pour chaque patient sur la carte
        for (User u : map.getUserTot()) {

            // On trie TOUS les hôpitaux du plus proche au plus loin
            // par rapport à ce patient
            List<Hospital> byDistance = map.getHospitals().stream()
                .sorted((a, b) -> Double.compare(
                    GeometryFunc.distance(u, a), // distance patient → hôpital a
                    GeometryFunc.distance(u, b)  // distance patient → hôpital b
                ))
                .collect(Collectors.toList());
            // Résultat : byDistance[0] = hôpital le plus proche
            //            byDistance[1] = 2e plus proche
            //            etc.

            // On mémorise cette liste dans le patient
            // (utile pour l'affichage dans le panneau latéral)
            u.setNextHospitals(byDistance);

            // On cherche le premier hôpital NON saturé dans la liste
            Hospital assigned = byDistance.stream()
                .filter(h -> !h.isSaturated()) // On ignore les hôpitaux saturés
                .findFirst()                    // On prend le premier disponible
                .orElse(byDistance.isEmpty() ? null : byDistance.get(0));
                // Si TOUS sont saturés → on prend quand même le plus proche

            // On affecte le patient à cet hôpital
            u.setClosestSite(assigned);
            // setAssignedHospital calcule aussi automatiquement isRedirected :
            // si assigned != byDistance[0] → le patient a été redirigé

            // On ajoute ce patient dans la liste de son hôpital assigné
            if (assigned != null)
                assigned.addUsers(u);
        }
    }

    // MÉTHODES UTILITAIRES PRIVÉES
    /**
     * Checks if a triangle contains a given edge (pair of hospitals).
     * Used to detect shared edges between bad triangles.
     * @param t the triangle to check
     * @param h1 first endpoint of the edge
     * @param h2 second endpoint of the edge
     * @return true if the triangle contains both h1 and h2 as vertices
     */
    private boolean triangleContainsEdge(Triangle t, Hospital h1, Hospital h2) {
        // On récupère les 3 sommets du triangle
        Hospital[] verts = {t.getA(), t.getB(), t.getC()};
        boolean hasH1 = false, hasH2 = false;

        // On vérifie si h1 et h2 sont tous les deux dans les sommets
        for (Hospital h : verts) {
            if (h == h1) hasH1 = true; // == compare les références Java, pas les valeurs
            if (h == h2) hasH2 = true; // C'est voulu : on veut le même objet exact
        }

        // Le triangle contient l'arête seulement si les DEUX sommets sont présents
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

            // Trouver tous les triangles qui ont cet hôpital comme sommet
            List<Triangle> adjacent = new ArrayList<>();
            for (Triangle t : triangles) {
                if (t.getA() == hospital ||
                    t.getB() == hospital ||
                    t.getC() == hospital) {
                    adjacent.add(t);
                }
            }

            // Récupérer les circumcenters de ces triangles
            // ce sont les sommets du polygone Voronoï
            List<Point> vertices = new ArrayList<>();
            for (Triangle t : adjacent) {
                vertices.add(t.getCircumcenter());
            }

            // Trier les sommets dans le bon ordre (sens horaire)
            // pour former un polygone correct
            vertices = sortPolygonVertices(vertices, hospital);

            // Créer la zone Voronoï pour cet hôpital
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

    // On calcule l'angle de chaque sommet par rapport au centre
    // et on trie dans l'ordre croissant des angles
    vertices.sort((p1, p2) -> {
        double angle1 = Math.atan2(
            p1.getY() - center.getY(),
            p1.getX() - center.getX()
        );
        double angle2 = Math.atan2(
            p2.getY() - center.getY(),
            p2.getX() - center.getX()
        );
        return Double.compare(angle1, angle2);
    });

    return vertices;
}
}

