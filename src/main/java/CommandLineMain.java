import Algorithm.TriangulationDelaunay;
import Algorithm.ImportExportMap;
import Algorithm.VoronoiEngine;
import Model.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Command-line interface for testing the Voronoi/Delaunay engine without JavaFX.
 * Allows adding, removing, moving hospitals and patients, and inspecting results.
 */
public class CommandLineMain {


    /**
     * Prints the list of available commands.
     */
    private static void printHelp() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       Voronoi Hospital — Command Line Interface      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  HOSPITALS");
        System.out.println("  add-hospital <x> <y> <name> <capacity>  Add a hospital");
        System.out.println("  remove <id>                              Remove a hospital");
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