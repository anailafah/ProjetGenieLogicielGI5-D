import Algorithm.*;

import Model.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Command-line interface for testing the Voronoi/Delaunay engine without JavaFX.
 * Allows adding, removing, moving hospitals and patients, and inspecting results.
 */
public class CommandLineMain {

    private static VoronoiEngine engine;
    private static Scanner scanner;

    /**
     * Entry point for the CLI version.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        engine  = new TriangulationDelaunay(800, 600);
        scanner = new Scanner(System.in);

        printHelp();

        while (true) {
            System.out.print("\n> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String cmd = parts[0].toLowerCase();

            try {
                switch (cmd) {
                    case "help": printHelp();  break;
                    case "add-hospital": cmdAddHospital(parts); break;
                    case "remove": cmdRemoveHospital(parts); break;
                    case "move": cmdMoveHospital(parts); break;
                    case "add-patient": cmdAddPatient(parts); break;
                    case "rm-patient": cmdRemovePatient(parts); break;
                    case "list": cmdList(); break;
                    case "triangles":
                        cmdTriangles();
                        break;
                    case "nearest":
                        cmdNearest(parts);
                        break;
                    case "patients":
                        cmdPatients();
                        break;
                    case "stats":
                        cmdStats(parts);
                        break;
                    case "export":
                        cmdExport(parts);
                        break;
                    case "import":
                        cmdImport(parts);
                        break;
                    case "import-csv":
                        cmdImportCSV(parts);
                        break;
                    case "clear":
                        cmdClear();
                        break;
                    case "quit":
                    case "exit":
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command: '" + cmd + "'. Type 'help' for the list of commands.");
                }
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }


    /**
     * Adds a hospital. Usage: add-hospital <x> <y> <capacity>
     */
    private static void cmdAddHospital(String[] parts) {
        if (parts.length < 5) {
            System.out.println("Usage: add-hospital <x> <y> <name> <capacity>");
            System.out.println("Example: add-hospital 100 200 Hopital-A 50");
            return;
        }
        try {
            double x     = Double.parseDouble(parts[1]);
            double y     = Double.parseDouble(parts[2]);
            String name  = parts[3];
            int capacity = Integer.parseInt(parts[4]);

            if (capacity <= 0) {
                System.out.println("Error: capacity must be greater than 0");
                return;
            }

            Hospital h = engine.addHospital(x, y, name, capacity);
            System.out.println("Hospital added: " + h);

        } catch (NumberFormatException e) {
            System.out.println("Error: x and y must be numbers, capacity must be an integer");
            System.out.println("Example: add-hospital 100.5 200.0 50");
        }
    }

    /**
     * Removes a hospital by ID. Usage: remove <id>
     */
    private static void cmdRemoveHospital(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: remove <id>");
            return;
        }
        try {
            int id = Integer.parseInt(parts[1]);

            // On cherche l'hôpital avec cet ID
            Hospital found = findHospitalById(id);
            if (found == null) {
                System.out.println("Error: no hospital found with id " + id);
                return;
            }

            engine.removeHospital(found);
            System.out.println("Hospital removed: id=" + id);

        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer");
        }
    }

    /**
     * Moves a hospital. Usage: move <id> <newX> <newY>
     */
    private static void cmdMoveHospital(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: move <id> <newX> <newY>");
            return;
        }
        try {
            int id     = Integer.parseInt(parts[1]);
            double newX = Double.parseDouble(parts[2]);
            double newY = Double.parseDouble(parts[3]);

            Hospital found = findHospitalById(id);
            if (found == null) {
                System.out.println("Error: no hospital found with id " + id);
                return;
            }

            engine.moveHospital(found, newX, newY);
            System.out.println("Hospital moved: " + found);

        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer, x and y must be numbers");
        }
    }

    /**
     * Adds a patient. Usage: add-patient <x> <y>
     */
    private static void cmdAddPatient(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: add-patient <x> <y>");
            return;
        }
        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);

            // Vérifier qu'il y a au moins un hôpital
            if (engine.getMap().getHospitals().isEmpty()) {
                System.out.println("Error: add at least one hospital before adding patients");
                return;
            }

            User p = engine.addUser(x, y);
            System.out.println("Patient added: " + p);

            // Afficher l'hôpital assigné
            if (p.getClosestSite() != null) {
                System.out.println("  → Assigned to: " + p.getClosestSite().getId()
                    + (p.getIsRedirected() ? " (REDIRECTED — nearest was full)" : ""));
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: x and y must be numbers");
        }
    }

    /**
     * Removes a patient by ID. Usage: rm-patient <id>
     */
    private static void cmdRemovePatient(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: rm-patient <id>");
            return;
        }
        try {
            int id = Integer.parseInt(parts[1]);

            User found = findPatientById(id);
            if (found == null) {
                System.out.println("Error: no patient found with id " + id);
                return;
            }

            engine.removeUser(found);
            System.out.println("Patient removed: id=" + id);

        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer");
        }
    }

    /**
     * Lists all hospitals.
     */
    private static void cmdList() {
        List<Hospital> hospitals = engine.getMap().getHospitals();

        if (hospitals.isEmpty()) {
            System.out.println("No hospitals on the map.");
            return;
        }

        System.out.println("Hospitals (" + hospitals.size() + "):");
        System.out.println("─".repeat(60));
        for (Hospital h : hospitals) {
            String satStatus;
            double rate = h.getMaxCapacity() > 0
                ? (double) h.getUsers().size() / h.getMaxCapacity() * 100
                : 0;
            if (rate >= 100)       satStatus = "FULL";
            else if (rate >= 75)   satStatus = "ALMOST FULL";
            else if (rate >= 50)   satStatus = "HALF FULL";
            else                   satStatus = "AVAILABLE";

            System.out.printf("  [id=%d] pos=(%.1f, %.1f)  patients=%d/%d  [%s]%n",
                h.getId(), h.getX(), h.getY(),
                h.getUsers().size(), h.getMaxCapacity(), satStatus);
        }
    }

    /**
     * Lists all Delaunay triangles.
     */
    private static void cmdTriangles() {
        List<Triangle> triangles = engine.getTriangles();

        if (triangles.isEmpty()) {
            System.out.println("No triangles yet (need at least 3 hospitals).");
            return;
        }

        System.out.println("Delaunay triangles (" + triangles.size() + "):");
        System.out.println("─".repeat(60));
        for (int i = 0; i < triangles.size(); i++) {
            Triangle t = triangles.get(i);
            System.out.printf("  Triangle %d : %s — %s — %s%n",
                i + 1,
                t.getA().getId(),
                t.getB().getId(),
                t.getC().getId());
            System.out.printf("    Area=%.1f  Imbalance=%d patients%n",
                t.getArea(), t.getImbalance());
            System.out.printf("    Circumcenter=(%.1f, %.1f)  Radius=%.1f%n",
                t.getCircumcenter().getX(),
                t.getCircumcenter().getY(),
                t.getCircumradius());
        }
    }

    /**
     * Finds the nearest hospital to a point. Usage: nearest <x> <y>
     */
    private static void cmdNearest(String[] parts) {
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

            System.out.println("Nearest hospital to (" + x + ", " + y + "):");
            System.out.printf("  → [id=%d] pos=(%.1f, %.1f)  %s%n",
                nearest.getId(),
                nearest.getX(), nearest.getY(),
                nearest.isSaturated() ? "FULL" : "AVAILABLE");

        } catch (NumberFormatException e) {
            System.out.println("Error: x and y must be numbers");
        }
    }

    /**
     * Lists all patients and their assignments.
     */
    private static void cmdPatients() {
        List<User> patients = engine.getMap().getUserTot();

        if (patients.isEmpty()) {
            System.out.println("No patients on the map.");
            return;
        }

        System.out.println("Patients (" + patients.size() + "):");
        System.out.println("─".repeat(60));
        for (User p : patients) {
            System.out.printf("  [id=%d] pos=(%.1f, %.1f)%n",
                p.getId(), p.getX(), p.getY());

            if (p.getClosestSite() != null) {
                boolean redirected = !p.getNextHospitals().isEmpty()
                    && p.getClosestSite() != p.getNextHospitals().get(0);
                int rank = p.getNextHospitals().indexOf(p.getClosestSite());
                System.out.printf("    Assigned to : %s%s%n",
                    p.getClosestSite().getId(),
                    redirected ? " ⚠ REDIRECTED (rank=" + rank + ")" : "");
            } else {
                System.out.println("    Not assigned (no available hospital)");
            }

            // Afficher la liste des hôpitaux par distance
            if (!p.getNextHospitals().isEmpty()) {
                System.out.println("    Hospitals by distance:");
                int rank = 1;
                for (Hospital h : p.getNextHospitals()) {
                    double dist = GeometryFunc.distance(p, h);
                    System.out.printf("      %d. %-20s dist=%.1f  %s%n",
                        rank++, h.getId(), dist,
                        h.isSaturated() ? "FULL" : "available (" + h.getAvailableRoom() + " slots)");
                }
            }
        }
    }

    /**
     * Shows statistics for a hospital. Usage: stats <id>
     */
    private static void cmdStats(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: stats <id>");
            return;
        }
        try {
            int id = Integer.parseInt(parts[1]);

            Hospital found = findHospitalById(id);
            if (found == null) {
                System.out.println("Error: no hospital found with id " + id);
                return;
            }

            System.out.println("Stats for: " + found.getId());
            System.out.println("─".repeat(40));
            System.out.printf("  Position       : (%.1f, %.1f)%n", found.getX(), found.getY());
            System.out.printf("  Capacity       : %d / %d%n",
                found.getUsers().size(), found.getMaxCapacity());
            System.out.printf("  Saturation     : %.1f%%%n", found.getSaturationRate());
            System.out.printf("  Available slots: %d%n", found.getAvailableRoom());
            System.out.printf("  Status         : %s%n",
                found.isSaturated() ? "FULL" :
                found.getSaturationRate() >= 75 ? "ALMOST FULL" : "AVAILABLE");

            // Compter les patients redirigés
            long redirected = found.getUsers().stream()
                .filter(User::getIsRedirected).count();
            System.out.printf("  Redirected in  : %d patient(s)%n", redirected);

        } catch (NumberFormatException e) {
            System.out.println("Error: id must be an integer");
        }
    }

    /**
     * Exports the map to a binary file. Usage: export <filepath>
     */
    private static void cmdExport(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: export <filepath>");
            System.out.println("Example: export saves/my-map.vmap");
            return;
        }
        String path = parts[1];
        try {
            ImportExportMap.exportBinary(engine.getMap(), path);
            System.out.println("Map exported successfully to: " + path);
        } catch (IllegalArgumentException e) {
            System.out.println("Error (invalid argument): " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error (export failed): " + e.getMessage());
        }
    }

    /**
     * Imports a map from a binary file. Usage: import <filepath>
     */
    private static void cmdImport(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: import <filepath>");
            System.out.println("Example: import saves/my-map.vmap");
            return;
        }
        String path = parts[1];
        try {
            VoronoiMap loaded = ImportExportMap.importBinary(path);
            // On recrée le moteur avec la carte chargée
            engine = new TriangulationDelaunay(800, 600);
            engine.getMap().getHospitals().addAll(loaded.getHospitals());
            engine.getMap().getUserTot().addAll(loaded.getUserTot());
            System.out.println("Map imported successfully from: " + path);
            System.out.println("  Hospitals: " + loaded.getHospitals().size());
            System.out.println("  Patients : " + loaded.getUserTot().size());
        } catch (IllegalArgumentException e) {
            System.out.println("Error (invalid argument): " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error (import failed): " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Error (incompatible file): " + e.getMessage());
        }
    }

    /**
     * Imports hospitals from a CSV file. Usage: import-csv <filepath>
     */
    private static void cmdImportCSV(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: import-csv <filepath>");
            System.out.println("Expected CSV format: name,x,y,maxCapacity");
            System.out.println("Example: import-csv data/hospitals.csv");
            return;
        }
        String file = parts[1];
        try {
            int count = ImportExportMap.importHospitalsCSV(file,engine.getMap());
            System.out.println("Imported " + count + " hospital(s) from: " + file);
        } catch (IllegalArgumentException e) {
            System.out.println("Error (invalid argument): " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error (CSV import failed): " + e.getMessage());
        }
    }

    /**
     * Clears the entire map.
     */
    private static void cmdClear() {
        engine = new TriangulationDelaunay(800, 600);
        System.out.println("Map cleared. All hospitals and patients removed.");
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES
    // ═══════════════════════════════════════════════════════════

    /**
     * Finds a hospital by its ID.
     * @param id the ID to search for
     * @return the Hospital, or null if not found
     */
    private static Hospital findHospitalById(int id) {
        for (Hospital h : engine.getMap().getHospitals())
            if (h.getId() == id) return h;
        return null;
    }

    /**
     * Finds a patient by its ID.
     * @param id the ID to search for
     * @return the Patient, or null if not found
     */
    private static User findPatientById(int id) {
        for (User u : engine.getMap().getUserTot())
            if (u.getId() == id) return u;
        return null;
    }

    /**
     * Prints the list of available commands.
     */
    private static void printHelp() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       Voronoi Hospital — Command Line Interface      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  HOSPITALS:");
        System.out.println("  add-hospital <x> <y> <name> <capacity>  Add a hospital");
        System.out.println("  remove <id>                             Remove a hospital");
        System.out.println("  move <id> <x> <y>                       Move a hospital");
        System.out.println("  list                                     List all hospitals");
        System.out.println("  stats <id>                               Show hospital stats");
        System.out.println();
        System.out.println("  PATIENTS");
        System.out.println("  add-patient <x> <y>                     Add a patient");
        System.out.println("  rm-patient <id>                         Remove a patient");
        System.out.println("  patients                                 List all patients");
        System.out.println("  nearest <x> <y>                         Find nearest hospital");
        System.out.println();
        System.out.println("  TRIANGULATION");
        System.out.println("  triangles                                Show Delaunay triangles");
        System.out.println();
        System.out.println("  SAVE / LOAD");
        System.out.println("  export <filepath>                        Export map to binary");
        System.out.println("  import <filepath>                        Import map from binary");
        System.out.println("  import-csv <filepath>                    Import hospitals from CSV");
        System.out.println();
        System.out.println("  OTHER");
        System.out.println("  clear                                    Clear the map");
        System.out.println("  help                                     Show this help");
        System.out.println("  quit                                     Exit");
        System.out.println();
    }
}