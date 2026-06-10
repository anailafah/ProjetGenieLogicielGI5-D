package Algorithm;

import Model.*;
import java.util.List;

/**
 * Interface defining the core operations of the Voronoi/Delaunay engine.
 */
public interface VoronoiEngine {
    /**
     * Adds a hospital and recomputes the triangulation.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the created Site
     */
    Hospital addHospital(String name, double x, double y, int maxCapacity) ;

    /**
     * Removes a hospital and recomputes the triangulation.
     * @param h the hospital to remove
     */
    void removeHospital(Hospital h);

    /**
     * Moves a hospital to a new position and recomputes.
     * @param h the hospital to move
     * @param x new x-coordinate
     * @param y new y-coordinate
     */
    void moveHospital(Hospital h, double x, double y);

    /**
     * Returns the current Delaunay triangles.
     * @return list of triangles
     */
    List<Triangle> getTriangles();

    /**
     * Returns the current Voronoi zones.
     * @return list of cells
     */
    List<HospitalZone> getZones();

    /**
     * Finds the nearest hospital to a given point.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the nearest hospital
     */
    Hospital getNearestHospital(double x, double y);

    /**
     * Returns the full map.
     * @return the VoronoiMap
     */
    VoronoiMap getMap();

    /**
     * add an user
     * @param x
     * @param y
     * @return user
     */
    User addUser(double x,double y);
    /**
     * remove an user
     * @param u
     */
    void removeUser(User u);
    /**
     * move an user
     * @param u
     * @param newX
     * @param newY
     */
    void moveUser(User u, double newX,double newY);

    /**
     * Updates the visible area boundaries.
     * @param x1 top-left x
     * @param y1 top-left y
     * @param x2 bottom-right x
     * @param y2 bottom-right y
     */
    void updateViewport(double x1, double y1, double x2, double y2);

    /** Recomputes the geometry. */
    void recompute();
}