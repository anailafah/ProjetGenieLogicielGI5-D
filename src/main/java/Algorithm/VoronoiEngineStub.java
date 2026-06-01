package Algorithm;

import Model.Hospital;
import Model.HospitalZone;
import Model.Triangle;
import Model.VoronoiMap;

import java.util.List;

/**
 * Stub implementation of the Voronoi engine.
 *
 * This class provides fake data and simple behavior to unblock the UI
 * while the real Bowyer-Watson / Voronoi implementation is being developed.
 */
public class VoronoiEngineStub implements VoronoiEngine {

    private final VoronoiMap map;

    /**
     * Creates a fake Voronoi engine with sample hospitals.
     */
    public VoronoiEngineStub() {
        this.map = new VoronoiMap();

        addHospital(100, 100, 50);
        addHospital(300, 150, 80);
        addHospital(200, 350, 60);
    }

    /**
     * Adds a hospital to the fake map.
     */
    @Override
    public Hospital addHospital(double x, double y, int capacity) {
        int id = map.generateId();
        Hospital hospital = new Hospital(id, x, y, capacity);

        map.addHospital(hospital);
        refreshFakeMap();

        return hospital;
    }

    /**
     * Removes a hospital from the fake map.
     */
    @Override
    public void removeHospital(Hospital h) {
        if (h != null) {
            map.removeHospital(h);
            refreshFakeMap();
        }
    }

    /**
     * Moves an existing hospital to a new position.
     */
    @Override
    public void moveHospital(Hospital h, double x, double y) {
        if (h != null) {
            h.setX(x);
            h.setY(y);
            refreshFakeMap();
        }
    }

    /**
     * Returns fake/current triangles.
     */
    @Override
    public List<Triangle> getTriangles() {
        return map.getTriangles();
    }

    /**
     * Returns fake/current Voronoi zones.
     */
    @Override
    public List<HospitalZone> getZones() {
        return map.getZones();
    }

    /**
     * Returns the nearest hospital using a simple distance comparison.
     */
    @Override
    public Hospital getNearestHospital(double x, double y) {
        List<Hospital> hospitals = map.getHospitals();

        if (hospitals.isEmpty()) {
            return null;
        }

        Hospital nearest = hospitals.get(0);
        double minDistance = distanceSquared(x, y, nearest.getX(), nearest.getY());

        for (Hospital hospital : hospitals) {
            double distance = distanceSquared(x, y, hospital.getX(), hospital.getY());

            if (distance < minDistance) {
                minDistance = distance;
                nearest = hospital;
            }
        }

        return nearest;
    }

    /**
     * Returns the fake Voronoi map.
     */
    @Override
    public VoronoiMap getMap() {
        return map;
    }

    /**
     * Computes the squared distance between two coordinates.
     *
     * We use squared distance because it is enough to compare distances
     * and avoids using Math.sqrt.
     */
    private double distanceSquared(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        return dx * dx + dy * dy;
    }

    /**
     * Fake recomputation method.
     *
     * In the real engine, this method would recompute Delaunay triangles
     * and Voronoi zones. In this stub, we only clear computed data so that
     * the UI can still call the engine without crashing.
     */
    private void refreshFakeMap() {
        map.clearComputed();
    }
}

