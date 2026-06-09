package Cli;

import Algorithm.ImportExportMap;
import Algorithm.VoronoiEngine;
import Model.*;

/**
 * CLI commands related to file import and export.
 */
public class FileCommands {

    /**
     * Imports hospitals from a CSV file. Usage: import-csv <filename>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void importCSV(String[] parts, VoronoiEngine engine) {
        if (parts.length < 2) {
            System.out.println("Usage: import-csv <filename>");
            System.out.println("Example: import-csv hospitals.csv");
            System.out.println("File must be in data/ folder");
            return;
        }
        try {
            int count = ImportExportMap.importHospitalsCSV(
                parts[1], engine.getMap());
            System.out.println("Imported " + count + " hospital(s) from: " + parts[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("Error (invalid argument): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error (import failed): " + e.getMessage());
        }
    }

    /**
     * Exports the map to a binary file. Usage: export <filename>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void export(String[] parts, VoronoiEngine engine) {
        if (parts.length < 2) {
            System.out.println("Usage: export <filename>");
            System.out.println("Example: export map.bin");
            return;
        }
        try {
            ImportExportMap.exportBinary(engine.getMap(), parts[1]);
            System.out.println("Map exported to: data/" + parts[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("Error (invalid argument): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error (export failed): " + e.getMessage());
        }
    }

    /**
     * Imports a map from a binary file. Usage: import <filename>
     * @param parts command parts
     * @param engine the Voronoi engine
     */
    public static void importBin(String[] parts, VoronoiEngine engine) {
        if (parts.length < 2) {
            System.out.println("Usage: import <filename>");
            System.out.println("Example: import map.bin");
            return;
        }
        try {
            VoronoiMap loaded = ImportExportMap.importBinary(parts[1]);
            engine.getMap().getHospitals().clear();
            engine.getMap().getUserTot().clear();
            for (Hospital h : loaded.getHospitals())
                engine.getMap().addHospital(h);
            for (User u : loaded.getUserTot())
                engine.getMap().addUsertot(u);
            System.out.println("Map imported from: data/" + parts[1]);
            System.out.println("  Hospitals : " + loaded.getHospitals().size());
            System.out.println("  Users     : " + loaded.getUserTot().size());
        } catch (ClassNotFoundException e) {
            System.out.println("Error: incompatible file version");
        } catch (IllegalArgumentException e) {
            System.out.println("Error (invalid argument): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error (import failed): " + e.getMessage());
        }
    }
}