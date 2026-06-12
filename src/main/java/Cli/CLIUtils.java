package Cli;

import Algorithm.VoronoiEngine;
import Model.*;

/**
 * Utility class used by the command line interface.
 * It groups helper methods used to find hospitals and users
 * from the current Voronoi engine.
 */
public class CLIUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CLIUtils() {
    }

    /**
     * Finds a hospital by its ID.
     *
     * @param id the ID to search for
     * @param engine the Voronoi engine
     * @return the hospital with the given ID, or null if no hospital is found
     */
    public static Hospital findHospitalById(int id, VoronoiEngine engine) {
        for (Hospital h : engine.getMap().getHospitals()) {
            if (h.getId() == id) {
                return h;
            }
        }
        return null;
    }

    /**
     * Finds a user by its ID.
     *
     * @param id the ID to search for
     * @param engine the Voronoi engine
     * @return the user with the given ID, or null if no user is found
     */
    public static User findUserById(int id, VoronoiEngine engine) {
        for (User u : engine.getMap().getUserTot()) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }
}