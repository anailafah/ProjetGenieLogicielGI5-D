package Cli;

import Algorithm.VoronoiEngine;
import Model.*;

import java.util.List;
import java.util.Random;

/**
 * CLI commands related to users.
 */
public class UserCommands {

    /**
     * Adds a user. Usage: add-u <x> <y>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void add(String[] parts, VoronoiEngine engine) {
        if (parts.length < 3) {
            System.out.println("Usage: add-user <x> <y>");
            return;
        }
        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);

            if (engine.getMap().getHospitals().isEmpty()) {
                System.out.println("Error: add at least one hospital first");
                return;
            }

            User u = engine.addUser(x, y);
            System.out.println("User added: " + u);
            if (u.getClosestSite() != null) {
                System.out.println("  Assigned to: " + u.getClosestSite().getName()
                    + (u.getIsRedirected()
                        ? " (REDIRECTED — nearest was full)"
                        : ""));
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: x and y must be numbers");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Removes a user by ID. Usage: remove-u <id>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void remove(String[] parts, VoronoiEngine engine) {
        if (parts.length < 2) {
            System.out.println("Usage: remove-user <id>");
            return;
        }
        try {
            int id = Integer.parseInt(parts[1]);
            User u = CLIUtils.findUserById(id, engine);
            if (u == null) {
                System.out.println("Error: no user with id " + id);
                return;
            }
            engine.removeUser(u);
            System.out.println("User removed: id=" + id);

        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer");
        }
    }

    /**
     * Moves a user. Usage: move-u <id> <newX> <newY>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void move(String[] parts, VoronoiEngine engine) {
        if (parts.length < 4) {
            System.out.println("Usage: move-user <id> <newX> <newY>");
            return;
        }
        try {
            int    id   = Integer.parseInt(parts[1]);
            double newX = Double.parseDouble(parts[2]);
            double newY = Double.parseDouble(parts[3]);

            User u = CLIUtils.findUserById(id, engine);
            if (u == null) {
                System.out.println("Error: no user with id " + id);
                return;
            }
            engine.moveUser(u, newX, newY);
            System.out.println("User moved: " + u);

        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number format");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds random users. Usage: random-u <count>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void random(String[] parts, VoronoiEngine engine) {
        if (parts.length < 2) {
            System.out.println("Usage: random-user <count>");
            return;
        }
        try {
            int count = Integer.parseInt(parts[1]);
            if (count <= 0) {
                System.out.println("Error: count must be > 0");
                return;
            }
            if (engine.getMap().getHospitals().isEmpty()) {
                System.out.println("Error: add at least one hospital first");
                return;
            }

            Random rnd = new Random();
            for (int i = 0; i < count; i++) {
                double x = rnd.nextDouble() * 800;
                double y = rnd.nextDouble() * 600;
                engine.addUser(x, y);
            }
            System.out.println(count + " users added randomly");

        } catch (NumberFormatException e) {
            System.out.println("Error: count must be an integer");
        }
    }

    /**
     * Lists all users and their assignments.
     * @param engine the Voronoi engine
     */
    public static void list(VoronoiEngine engine) {
        List<User> users = engine.getMap().getUserTot();

        if (users.isEmpty()) {
            System.out.println("No users on the map.");
            return;
        }

        System.out.println("Users (" + users.size() + "):");
        System.out.println("-".repeat(65));

        for (User u : users) {
            System.out.printf("  [id=%-2d] pos=(%-6.1f, %-6.1f)  assigned=%-20s %s%n",
                u.getId(),
                u.getX(),
                u.getY(),
                u.getClosestSite() != null ? u.getClosestSite().getName() : "none",
                u.getIsRedirected()
                    ? "(REDIRECTED rank=" + u.getRedirectionRank() + ")"
                    : "");
        }
    }

    /**
     * Lists all Delaunay triangles.
     * @param engine the Voronoi engine
     */
    public static void triangles(VoronoiEngine engine) {
        List<Triangle> triangles = engine.getTriangles();

        if (triangles.isEmpty()) {
            System.out.println("No triangles yet (need at least 3 hospitals).");
            return;
        }

        System.out.println("Delaunay triangles (" + triangles.size() + "):");
        System.out.println("-".repeat(65));

        for (int i = 0; i < triangles.size(); i++) {
            Triangle t = triangles.get(i);
            System.out.printf("  Triangle %d : %s — %s — %s%n",
                i + 1,
                t.getA().getName(),
                t.getB().getName(),
                t.getC().getName());
        }
    }
}