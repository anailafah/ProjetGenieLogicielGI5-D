package Model;

import java.util.ArrayList;
import java.util.List;

public class VoronoiMap {
    private List<Hospital> hospitals;
    private List<User> userTot;
    private List<Triangle> triangles;
    private List<HospitalZone> zones;
    private int nextId;
    /**
     * construction of an empty map
     */
    public VoronoiMap(){
        hospitals = new ArrayList<>();
        userTot  = new ArrayList<>();
        triangles = new ArrayList<>();
        zones     = new ArrayList<>();
        nextId    = 0;
    }
    public VoronoiMap(List<Hospital> hospitals, List<User> userTot){
        this.hospitals=hospitals;
        this.userTot=userTot;
        this.triangles = new ArrayList<>();
        this.zones = new ArrayList<>();
        this.nextId = hospitals.size() + userTot.size();
    }
    /** 
     * Generates a unique ID for a new point. 
     * */
    public int generateId() {
        return nextId++;
    }
    /**
     * @return list of hosptals
     */
    public List<Hospital> getHospitals() { 
        return hospitals;
     }
     /** 
      * @return list of users
      */
    public List<User> getUserTot()  { 
        return userTot; 
    }
    /**
     * @return list of triangles
     */
    public List<Triangle> getTriangles() { 
        return triangles;
     }
     
    public List<HospitalZone> getZones(){ 
        return zones; 
    }

    /**
     * add hospital to the list
     * @param h hospital
     */
    public void addHospital(Hospital h){ 
        hospitals.add(h);
    }
    /** remove hospital from the list
     * @param h hospitak
     */
    public void removeHospital(Hospital h) { 
        hospitals.remove(h);
    }
    /**
     * add an user to the list
     * @param u user
     */
    public void addUsertot(User u){ 
        userTot.add(u); 
    }
    /**
     * remove an user from the list
     * @param u users
     */
    public void removeUsertot(User u){ 
        userTot.remove(u); 
    }

    /**
     * set a list of triangles
     * @param t list of triangles
     */
    public void setTriangles(List<Triangle> t){
        triangles = t; 
    }
    public void setZones(List<HospitalZone> z){
        zones = z; 
    }

    /** 
     * Clears all computed triangles and zones. 
     */
    public void clearComputed() {
        triangles.clear();
        zones.clear();
    }

    @Override
    public String toString() {
        return "VoronoiMap[hospitals=" + hospitals.size()
             + ", user=" + userTot.size() + "]";
    }
}
