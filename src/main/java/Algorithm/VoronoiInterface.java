package Algorithm;

import Model.*;
import java.util.List;

/**
 * Interface defining the core operations of the Voronoi/Delaunay engine.
 */
public interface VoronoiInterface {
    /**
     * Adds a hospital and recomputes the triangulation.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the created Site
     */
    Hospital addHospital(double x, double y,int capacity);

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
     * Returns the current Voronoi cells.
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
}