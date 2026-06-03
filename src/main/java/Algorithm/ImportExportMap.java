package Algorithm;

import Model.*;

import java.io.*;
import java.util.List;


/**
 * Handles all import and export operations for the map.
 * Supports CSV import of hospitals, patients, and full maps.
 * Supports binary export/import of the full map.
 */
public class ImportExportMap {

    private static final String SECTION_HOSPITALS = "[HOSPITALS]";
    private static final String SECTION_PATIENTS  = "[PATIENTS]";

    /**
     * Exports the full VoronoiMap to a binary file.
     * @param map      the map to export
     * @param filePath destination file path
     * @throws IOException if writing fails
     */
    public static void exportBinary(VoronoiMap map, String filePath)
            throws IOException {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        if (filePath == null || filePath.trim().isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty");

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs())
                throw new IOException("Cannot create directory: " + parent.getAbsolutePath());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(map);
        }
    }

    /**
     * Imports a full VoronoiMap from a binary file.
     * @param filePath source file path
     * @return the loaded VoronoiMap
     * @throws IOException if reading fails
     * @throws ClassNotFoundException if the class is not found
     */
    public static VoronoiMap importBinary(String filePath)
            throws IOException, ClassNotFoundException {
        if (filePath == null || filePath.trim().isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty");

        File file = new File(filePath);
        if (!file.exists()) throw new IOException("File not found: " + filePath);

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            Object obj = ois.readObject();
            if (!(obj instanceof VoronoiMap))
                throw new IOException("File does not contain a valid VoronoiMap");
            return (VoronoiMap) obj;
        }
    }

    /**
     * Imports hospitals from a CSV file into an existing map.
     * Expected format per line: name,x,y,maxCapacity
     * @param filePath source CSV file path
     * @param map      the map to populate
     * @return number of hospitals successfully imported
     * @throws IOException if the file cannot be read
     */
    public static int importHospitalsCSV(String filePath, VoronoiMap map)
            throws IOException {
        validateFileAndMap(filePath, map);

        int imported = 0;
        int lineNum  = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) {
                    System.err.println("Line " + lineNum + " skipped: " + line);
                    continue;
                }

                try {
                    String name  = parts[0].trim();
                    double x     = Double.parseDouble(parts[1].trim());
                    double y     = Double.parseDouble(parts[2].trim());
                    int capacity = Integer.parseInt(parts[3].trim());

                    if (name.isEmpty() || capacity <= 0) continue;

                    map.addHospital(new Hospital(map.generateId(), name, x, y, capacity));
                    imported++;
                } catch (NumberFormatException e) {
                    System.err.println("Line " + lineNum + " skipped (invalid number): " + line);
                }
            }
        }

        System.out.println("Imported " + imported + " hospital(s) from: " + filePath);
        return imported;
    }

    /**
     * Imports hospitals from a CSV file into the engine, then recomputes triangulation.
     * @param filePath source CSV file path
     * @param engine   the Delaunay engine to populate
     * @return number of hospitals successfully imported
     * @throws IOException if the file cannot be read
     */
    public static int importHospitalsCSV(String filePath, TriangulationDelaunay engine)
            throws IOException {
        int count = importHospitalsCSV(filePath, engine.getMap());
        if (count > 0) engine.recompute();
        return count;
    }

    /**
     * Exports the full map (hospitals + patients) to a CSV file.
     * @param map      the map to export
     * @param filePath destination file path
     * @throws IOException if writing fails
     */
    public static void exportFullMapCSV(VoronoiMap map, String filePath)
            throws IOException {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        if (filePath == null || filePath.trim().isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty");

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs())
                throw new IOException("Cannot create directory: " + parent.getAbsolutePath());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("# Voronoi Hospital Map Export");
            bw.newLine();
            bw.write("# Format : [HOSPITALS] name,x,y,maxCapacity");
            bw.newLine();
            bw.write("#          [PATIENTS]  x,y");
            bw.newLine();
            bw.newLine();

            bw.write(SECTION_HOSPITALS);
            bw.newLine();
            for (Hospital h : map.getHospitals()) {
                bw.write(h.getName() + ","
                    + h.getX() + ","
                    + h.getY() + ","
                    + h.getMaxCapacity());
                bw.newLine();
            }

            bw.newLine();

            bw.write(SECTION_PATIENTS);
            bw.newLine();
            for (User p : map.getUserTot()) {
                bw.write(p.getX() + "," + p.getY());
                bw.newLine();
            }
        }

        System.out.println("Map exported to: " + filePath);
    }

    private static void validateFileAndMap(String filePath, VoronoiMap map)
            throws IOException {
        if (filePath == null || filePath.trim().isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty");
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");

        File file = new File(filePath);
        if (!file.exists()) throw new IOException("File not found: " + filePath);
        if (!file.canRead()) throw new IOException("File is not readable: " + filePath);
    }
}
