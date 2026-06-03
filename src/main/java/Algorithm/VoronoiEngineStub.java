package Algorithm;

import Model.Hospital;
import Model.HospitalZone;
import Model.Triangle;
import Model.User;
import Model.VoronoiMap;

import java.util.List;

/**
 * Stub implementation of the Voronoi engine with fake data.
 */
public class VoronoiEngineStub implements VoronoiEngine {

    private final VoronoiMap map;

    public VoronoiEngineStub() {
        this.map = new VoronoiMap();
        addHospital(100, 100, "Hopital A", 50);
        addHospital(300, 150, "Hopital B", 80);
        addHospital(200, 350, "Hopital C", 60);
        createFakeTriangles();
    }

    @Override
    public Hospital addHospital(double x, double y, String name, int capacity) {
        int id = map.generateId();
        Hospital hospital = new Hospital(id, name, x, y, capacity);
        map.addHospital(hospital);
        return hospital;
    }

    @Override
    public void removeHospital(Hospital h) {
        if (h != null) map.removeHospital(h);
    }

    @Override
    public void moveHospital(Hospital h, double x, double y) {
        if (h != null) { h.setX(x); h.setY(y); }
    }

    @Override
    public User addUser(double x, double y) {
        User u = new User(map.generateId(), x, y);
        map.addUsertot(u);
        return u;
    }

    @Override
    public void removeUser(User u) {
        if (u != null) map.removeUsertot(u);
    }

    @Override
    public List<Triangle> getTriangles() { return map.getTriangles(); }

    @Override
    public List<HospitalZone> getZones() { return map.getZones(); }

    @Override
    public Hospital getNearestHospital(double x, double y) {
        if (map.getHospitals().isEmpty()) return null;
        Hospital nearest = map.getHospitals().get(0);
        double minDist = distanceSquared(x, y, nearest.getX(), nearest.getY());
        for (Hospital h : map.getHospitals()) {
            double d = distanceSquared(x, y, h.getX(), h.getY());
            if (d < minDist) { minDist = d; nearest = h; }
        }
        return nearest;
    }

    @Override
    public VoronoiMap getMap() { return map; }

    private double distanceSquared(double x1, double y1, double x2, double y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }

    private void createFakeTriangles() {
        List<Hospital> hospitals = map.getHospitals();
        if (hospitals.size() >= 3) {
            map.getTriangles().add(new Triangle(hospitals.get(0), hospitals.get(1), hospitals.get(2)));
        }
    }
}
