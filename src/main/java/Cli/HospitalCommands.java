package Cli;

import Algorithm.VoronoiEngine;
import Model.*;

import java.util.List;

/**
 * CLI commands related to hospitals.
 */
public class HospitalCommands {

    /**
     * Adds a hospital. Usage: add-hospital <name> <x> <y> <capacity>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void add(String[] parts, VoronoiEngine engine) {
        if (parts.length < 5) {
            System.out.println("Usage: add-hospital <name> <x> <y> <capacity>");
            return;
        }
        try {
            String name  = parts[1];
            double x     = Double.parseDouble(parts[2]);
            double y     = Double.parseDouble(parts[3]);
            int capacity = Integer.parseInt(parts[4]);

            if (capacity <= 0) {
                System.out.println("Error: capacity must be > 0");
                return;
            }

            Hospital h = engine.addHospital(name,x, y, capacity);
            System.out.println("Hospital added: " + h);

        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number format");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Removes a hospital by ID. Usage: remove-h <id>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void remove(String[] parts, VoronoiEngine engine) {
        if (parts.length < 2) {
            System.out.println("Usage: remove-hospital <id>");
            return;
        }
        try {
            int id = Integer.parseInt(parts[1]);
            Hospital h = CLIUtils.findHospitalById(id, engine);
            if (h == null) {
                System.out.println("Error: no hospital with id " + id);
                return;
            }
            engine.removeHospital(h);
            System.out.println("Hospital removed: id=" + id);

        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer");
        }
    }

    /**
     * Moves a hospital. Usage: move-h <id> <newX> <newY>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void move(String[] parts, VoronoiEngine engine) {
        if (parts.length < 4) {
            System.out.println("Usage: move-hospital <id> <newX> <newY>");
            return;
        }
        try {
            int    id   = Integer.parseInt(parts[1]);
            double newX = Double.parseDouble(parts[2]);
            double newY = Double.parseDouble(parts[3]);

            Hospital h = CLIUtils.findHospitalById(id, engine);
            if (h == null) {
                System.out.println("Error: no hospital with id " + id);
                return;
            }
            engine.moveHospital(h, newX, newY);
            System.out.println("Hospital moved: " + h);

        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number format");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Lists all hospitals.
     * @param engine the Voronoi engine
     */
    public static void list(VoronoiEngine engine) {
        List<Hospital> hospitals = engine.getMap().getHospitals();

        if (hospitals.isEmpty()) {
            System.out.println("No hospitals on the map.");
            return;
        }

        System.out.println("Hospitals (" + hospitals.size() + "):");
        System.out.println("-".repeat(65));

        for (Hospital h : hospitals) {
            String status;
            double rate = h.getSaturationRate();
            if (rate >= 100)      status = "FULL";
            else if (rate >= 75)  status = "ALMOST FULL";
            else if (rate >= 50)  status = "HALF FULL";
            else                  status = "AVAILABLE";

            System.out.printf("  [id=%-2d] %-20s pos=(%-6.1f, %-6.1f)  %d/%d  [%s]%n",
                h.getId(),
                h.getName(),
                h.getX(),
                h.getY(),
                h.getUsers().size(),
                h.getMaxCapacity(),
                status);
        }
    }

    /**
     * Shows statistics for a hospital. Usage: stats <id>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void stats(String[] parts, VoronoiEngine engine) {
        if (parts.length < 2) {
            System.out.println("Usage: stats <id>");
            return;
        }
        try {
            int id = Integer.parseInt(parts[1]);
            Hospital h = CLIUtils.findHospitalById(id, engine);
            if (h == null) {
                System.out.println("Error: no hospital with id " + id);
                return;
            }

            int redirected = 0;
            for (User u : h.getUsers()) {
                if (u.getIsRedirected()) redirected++;
            }

            System.out.println("Stats for: " + h.getName());
            System.out.println("-".repeat(40));
            System.out.printf("  Position       : (%.1f, %.1f)%n",
                h.getX(), h.getY());
            System.out.printf("  Capacity       : %d / %d%n",
                h.getUsers().size(), h.getMaxCapacity());
            System.out.printf("  Saturation     : %.1f%%%n",
                h.getSaturationRate());
            System.out.printf("  Available slots: %d%n",
                h.getAvailableRoom());
            System.out.printf("  Status         : %s%n",
                h.isSaturated() ? "FULL" :
                h.getSaturationRate() >= 75 ? "ALMOST FULL" : "AVAILABLE");
            System.out.printf("  Redirected in  : %d user(s)%n",
                redirected);

            if (!h.getUsers().isEmpty()) {
                System.out.println("  Users assigned:");
                for (User u : h.getUsers()) {
                    System.out.printf("    - [id=%d] pos=(%.1f, %.1f) %s%n",
                        u.getId(), u.getX(), u.getY(),
                        u.getIsRedirected()
                            ? "(redirected rank=" + u.getRedirectionRank() + ")"
                            : "");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer");
        }
    }

    /**
     * Finds the nearest hospital to a point. Usage: nearest <x> <y>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void nearest(String[] parts, VoronoiEngine engine) {
        if (parts.length < 3) {
            System.out.println("Usage: nearest <x> <y>");
            return;
        }
        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);

            if (engine.getMap().getHospitals().isEmpty()) {
                System.out.println("No hospitals on the map.");
                return;
            }

            Hospital nearest = engine.getNearestHospital(x, y);
            if (nearest == null) {
                System.out.println("No hospital found.");
                return;
            }

            System.out.println("Nearest to (" + x + ", " + y + "):");
            System.out.printf("  [id=%d] %s  pos=(%.1f, %.1f)  %s%n",
                nearest.getId(), nearest.getName(),
                nearest.getX(), nearest.getY(),
                nearest.isSaturated() ? "FULL" : "AVAILABLE");

        } catch (NumberFormatException e) {
            System.out.println("Error: x and y must be numbers");
        }
    }
}
