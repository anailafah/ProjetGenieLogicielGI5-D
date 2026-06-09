import Algorithm.TriangulationDelaunay;
import Algorithm.VoronoiEngine;
import Cli.*;

import java.util.Scanner;

/**
 * Command-line interface entry point.
 */
public class CommandLineMain {

    public static VoronoiEngine engine;
    public static Scanner scanner;

    /**
     * Entry point for the CLI version.
     * @param args command line arguments
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
            String   cmd   = parts[0].toLowerCase();

            try {
                switch (cmd) {
                    case "help":       printHelp();                          break;
                    case "add-hospital":      HospitalCommands.add(parts, engine);  break;
                    case "remove-hospital":   HospitalCommands.remove(parts, engine);break;
                    case "move-hospital":     HospitalCommands.move(parts, engine);  break;
                    case "list-hospital":     HospitalCommands.list(engine);         break;
                    case "stats":      HospitalCommands.stats(parts, engine); break;
                    case "nearest":    HospitalCommands.nearest(parts,engine);break;
                    
                    case "add-user":      UserCommands.add(parts, engine);       break;
                    case "remove-user":   UserCommands.remove(parts, engine);    break;
                    case "move-user":     UserCommands.move(parts, engine);      break;
                    case "random-user":   UserCommands.random(parts, engine);    break;
                    case "list-user":     UserCommands.list(engine);             break;
                    case "triangles":  UserCommands.triangles(engine);        break;
                    
                    case "import-csv": FileCommands.importCSV(parts, engine); break;
                    case "export":     FileCommands.export(parts, engine);    break;
                    case "import":     FileCommands.importBin(parts, engine); break;
                    case "clear":      cmdClear();                            break;
                    case "quit":
                    case "exit":
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help'.");
                }
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    /** Clears the entire map. */
    private static void cmdClear() {
        engine = new TriangulationDelaunay(800, 600);
        System.out.println("Map cleared.");
    }

    /** Prints the list of available commands. */
    private static void printHelp() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║     Voronoi Hospital — Command Line          ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  HOSPITALS");
        System.out.println("  add-hospital <x> <y> <name> <capacity>");
        System.out.println("  remove-hospital <id>");
        System.out.println("  move-hospital <id> <x> <y>");
        System.out.println("  list-hospital");
        System.out.println("  stats <id>");
        System.out.println("  nearest <x> <y>");
        System.out.println();
        System.out.println("  USERS");
        System.out.println("  add-user <x> <y>");
        System.out.println("  remove-user <id>");
        System.out.println("  move-user <id> <x> <y>");
        System.out.println("  random-user <count>");
        System.out.println("  list-user");
        System.out.println("  triangles");
        System.out.println();
        System.out.println("  FILES");
        System.out.println("  import-csv <filename>");
        System.out.println("  export <filename>");
        System.out.println("  import <filename>");
        System.out.println();
        System.out.println("  OTHER");
        System.out.println("  clear");
        System.out.println("  help");
        System.out.println("  quit");
    }
}