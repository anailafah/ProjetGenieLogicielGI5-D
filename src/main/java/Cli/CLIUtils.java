package Cli;

import Algorithm.VoronoiEngine;
import Model.*;

/**
 * Shared utility methods for all CLI command classes.
 */
public class CLIUtils {

    /**
     * Finds a hospital by its ID.
     * @param id     the ID to search for
     * @param engine the Voronoi engine
     * @return the Hospital, or null if not found
     */
    public static Hospital findHospitalById(int id, VoronoiEngine engine) {
        for (Hospital h : engine.getMap().getHospitals()) {
            if (h.getId() == id) return h;
        }
        return null;
    }

    /**
     * Finds a user by its ID.
     * @param id     the ID to search for
     * @param engine the Voronoi engine
     * @return the User, or null if not found
     */
    public static User findUserById(int id, VoronoiEngine engine) {
        for (User u : engine.getMap().getUserTot()) {
            if (u.getId() == id) return u;
        }
        return null;
    }
}