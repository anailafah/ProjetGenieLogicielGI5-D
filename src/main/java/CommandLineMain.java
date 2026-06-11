import Algorithm.*;

import Cli.*;

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
                    case "add-hospital": HospitalCommands.add(parts,engine); break;
                    case "remove-hospital": HospitalCommands.remove(parts,engine); break;
                    case "move-hospital": HospitalCommands.move(parts,engine); break;
                    case "list-hospital": HospitalCommands.list(engine); break;
                    case "stats": HospitalCommands.stats(parts, engine); break;
                    case "nearest": HospitalCommands.nearest(parts,engine);break;

                    case "add-user": UserCommands.add(parts,engine); break;
                    case "remove-user": UserCommands.remove(parts, engine); break;
                    case "move-user": UserCommands.move(parts,engine); break;
                    case "list-user": UserCommands.list(engine); break;
                    case "triangles": UserCommands.triangles(engine);break;
                    case "random-user":UserCommands.random(parts ,engine);break;

                    case "export": FileCommands.exportBin(parts, engine); break;
                    case "import": FileCommands.importBin(parts, engine); break;
                    case "import-csv": FileCommands.importCSV(parts, engine); break;
                    case "clear": cmdClear(); break;
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
     * Prints the list of available commands.
     */
    private static void printHelp() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       Voronoi Hospital — Command Line Interface      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  HOSPITALS:");
        System.out.println("  add-hospital <name> <x> <y> <capacity>  Add a hospital");
        System.out.println("  remove-hospital <id>                    Remove a hospital");
        System.out.println("  move-hospital <id> <x> <y>              Move a hospital");
        System.out.println("  list-hospital                           List all hospitals");
        System.out.println("  stats <id>                              Show hospital stats");
        System.out.println();
        System.out.println("  USER");
        System.out.println("  add-user <x> <y>                         Add an user");
        System.out.println("  remove-user <id>                         Remove an user");
        System.out.println("  move-user <id>                           Remove an user");
        System.out.println("  random-user <count>                      add random users");
        System.out.println("  list-user                                List all users");
        System.out.println("  nearest <x> <y>                          find nearest hospital");
        System.out.println();
        System.out.println("  TRIANGULATION");
        System.out.println("  triangles                                Show Delaunay triangles");
        System.out.println();
        System.out.println("  SAVE / LOAD");
        System.out.println("  export <file>                        Export map to binary");
        System.out.println("  import <file>                        Import map from binary");
        System.out.println("  import-csv <file>                    Import hospitals from CSV");
        System.out.println();
        System.out.println("  OTHER");
        System.out.println("  clear                                    Clear the map");
        System.out.println("  help                                     Show this help");
        System.out.println("  quit                                     Exit");
        System.out.println();
    }
    /**
    * Clears the entire map by creating a new engine.
    */
    private static void cmdClear() {
        engine = new TriangulationDelaunay(800, 600);
        System.out.println("Map cleared. All hospitals and users removed.");
    }
}