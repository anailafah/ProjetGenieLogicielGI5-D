package Model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a hospital point, also used as a Voronoi site.
 */
public class Hospital extends Point {

    private static final long serialVersionUID = 2L;

    private List<User> users;
    private final int maxCapacity;
    private String name;

    /**
     * Creates a hospital with an identifier, name, position and maximum capacity.
     *
     * @param id the hospital identifier
     * @param name the hospital name
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param maxCapacity the maximum number of users the hospital can receive
     */
    public Hospital(int id, String name, double x, double y, int maxCapacity) {
        super(id, x, y);
        this.users = new ArrayList<>();
        this.maxCapacity = maxCapacity;
        this.name = name;
    }

    /**
     * Returns the name of the hospital.
     *
     * @return the name of the hospital
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the hospital.
     *
     * @param name the new name of the hospital
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the users assigned to the hospital.
     *
     * @return the list of users assigned to the hospital
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Returns the maximum capacity of the hospital.
     *
     * @return the maximum capacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Returns a string representation of the hospital.
     *
     * @return a string containing the hospital information
     */
    @Override
    public String toString() {
        return "id =" + getId()
                + " name= " + getName()
                + " (" + getX() + " , " + getY() + ")"
                + " Capacity =" + getMaxCapacity()
                + " Users =" + users.size();
    }

    /**
     * Checks whether the hospital is saturated.
     *
     * @return true if the hospital is saturated, false otherwise
     */
    public boolean isSaturated() {
        return users.size() >= maxCapacity;
    }

    /**
     * Returns the number of available rooms in the hospital.
     *
     * @return the number of available rooms
     */
    public int getAvailableRoom() {
        return this.maxCapacity - this.users.size();
    }

    /**
     * Returns the saturation rate of the hospital.
     *
     * @return the saturation rate as a percentage
     */
    public double getSaturationRate() {
        if (maxCapacity <= 0) {
            return 0;
        }
        return ((double) users.size() / maxCapacity) * 100;
    }

    /**
     * Adds a user to the hospital.
     *
     * @param u the user to add
     */
    public void addUsers(User u) {
        if (u != null) {
            users.add(u);
        }
    }

    /**
     * Removes a user from the hospital.
     *
     * @param u the user to remove
     */
    public void removeUsers(User u) {
        users.remove(u);
    }
}