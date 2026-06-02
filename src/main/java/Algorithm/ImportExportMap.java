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

    // Marqueurs de section dans le fichier CSV carte complète
    private static final String SECTION_HOSPITALS = "[HOSPITALS]";
    private static final String SECTION_PATIENTS  = "[PATIENTS]";

    /**
     * Exports the full VoronoiMap to a binary file.
     * @param map      the map to export (must not be null)
     * @param filePath destination file path (must not be null or empty)
     * @throws IllegalArgumentException if map or filePath is invalid
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
                throw new IOException("Cannot create directory: "
                    + parent.getAbsolutePath());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(map);
        } catch (NotSerializableException e) {
            throw new IOException("Map contains non-serializable objects: "
                + e.getMessage(), e);
        }
    }

    /**
     * Imports a full VoronoiMap from a binary file.
     * @param filePath source file path
     * @return the loaded VoronoiMap
     * @throws IllegalArgumentException if filePath is invalid
     * @throws IOException if reading fails
     * @throws ClassNotFoundException if the class is not found
     */
    public static VoronoiMap importBinary(String filePath)
            throws IOException, ClassNotFoundException {
        if (filePath == null || filePath.trim().isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty");

        File file = new File(filePath);
        if (!file.exists())
            throw new IOException("File not found: " + filePath);
        if (!file.isFile())
            throw new IOException("Path is not a file: " + filePath);
        if (!file.canRead())
            throw new IOException("File is not readable: " + filePath);

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            Object obj = ois.readObject();
            if (!(obj instanceof VoronoiMap))
                throw new IOException("File does not contain a valid VoronoiMap");
            return (VoronoiMap) obj;
        } catch (StreamCorruptedException e) {
            throw new IOException("File is corrupted: " + filePath, e);
        } catch (InvalidClassException e) {
            throw new IOException("File was created with an older version: "
                + filePath, e);
        }
    }

    /**
     * Imports hospitals from a CSV file into an existing map.
     * Expected format per line: name,x,y,maxCapacity
     * @param filePath source CSV file path
     * @param map      the map to populate
     * @return number of hospitals successfully imported
     * @throws IllegalArgumentException if filePath or map is null
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
                    System.err.println("Line " + lineNum
                        + " skipped (expected 4 columns): " + line);
                    continue;
                }

                try {
                    String name   = parts[0].trim();
                    double x      = Double.parseDouble(parts[1].trim());
                    double y      = Double.parseDouble(parts[2].trim());
                    int capacity  = Integer.parseInt(parts[3].trim());

                    if (name.isEmpty()) {
                        System.err.println("Line " + lineNum
                            + " skipped (empty name)");
                        continue;
                    }
                    if (capacity <= 0) {
                        System.err.println("Line " + lineNum
                            + " skipped (capacity must be > 0): " + capacity);
                        continue;
                    }
                    if (!Double.isFinite(x) || !Double.isFinite(y)) {
                        System.err.println("Line " + lineNum
                            + " skipped (invalid coordinates)");
                        continue;
                    }

                    map.addHospital(new Hospital(
                        map.generateId(), x, y, capacity));
                    imported++;

                } catch (NumberFormatException e) {
                    System.err.println("Line " + lineNum
                        + " skipped (invalid number): " + line);
                } catch (IllegalArgumentException e) {
                    System.err.println("Line " + lineNum
                        + " skipped (" + e.getMessage() + "): " + line);
                }
            }
        }

        System.out.println("Imported " + imported + " hospital(s) from: " + filePath);
        return imported;
    }

    /**
     * Imports patients from a CSV file into an existing map.
     * Expected format per line: x,y
     * @param filePath source CSV file path
     * @param map      the map to populate
     * @return number of patients successfully imported
     * @throws IllegalArgumentException if filePath or map is null
     * @throws IOException if the file cannot be read
     */
    public static int importPatientsCSV(String filePath, VoronoiMap map)
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
                if (parts.length < 2) {
                    System.err.println("Line " + lineNum
                        + " skipped (expected 2 columns): " + line);
                    continue;
                }

                try {
                    double x = Double.parseDouble(parts[0].trim());
                    double y = Double.parseDouble(parts[1].trim());

                    if (!Double.isFinite(x) || !Double.isFinite(y)) {
                        System.err.println("Line " + lineNum
                            + " skipped (invalid coordinates)");
                        continue;
                    }

                    map.addUsertot(new User(map.generateId(), x, y));
                    imported++;

                } catch (NumberFormatException e) {
                    System.err.println("Line " + lineNum
                        + " skipped (invalid number): " + line);
                } catch (IllegalArgumentException e) {
                    System.err.println("Line " + lineNum
                        + " skipped (" + e.getMessage() + "): " + line);
                }
            }
        }

        System.out.println("Imported " + imported + " patient(s) from: " + filePath);
        return imported;
    }
    /**
     * Imports a full map (hospitals + patients) from a CSV file.
     * The file must contain [HOSPITALS] and [PATIENTS] section markers.
     * @param filePath source CSV file path
     * @param map      the map to populate
     * @return int[]{hospitalsImported, patientsImported}
     * @throws IllegalArgumentException if filePath or map is null
     * @throws IOException if the file cannot be read
     */
    public static int[] importFullMapCSV(String filePath, VoronoiMap map)
            throws IOException {
        validateFileAndMap(filePath, map);

        int hospitalsImported = 0;
        int patientsImported  = 0;
        int lineNum           = 0;

        // Section courante : null = avant toute section
        String currentSection = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();

                // Ignorer les lignes vides et commentaires
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Détecter les marqueurs de section
                if (line.equals(SECTION_HOSPITALS)) {
                    currentSection = SECTION_HOSPITALS;
                    continue;
                }
                if (line.equals(SECTION_PATIENTS)) {
                    currentSection = SECTION_PATIENTS;
                    continue;
                }

                // Si on n'est dans aucune section connue → ignorer
                if (currentSection == null) {
                    System.err.println("Line " + lineNum
                        + " ignored (no section declared): " + line);
                    continue;
                }

                // Parser selon la section courante
                if (currentSection.equals(SECTION_HOSPITALS)) {
                    if (parseHospitalLine(line, lineNum, map))
                        hospitalsImported++;

                } else if (currentSection.equals(SECTION_PATIENTS)) {
                    if (parsePatientLine(line, lineNum, map))
                        patientsImported++;
                }
            }
        }

        System.out.println("Full map imported from: " + filePath);
        System.out.println("  Hospitals : " + hospitalsImported);
        System.out.println("  Patients  : " + patientsImported);
        return new int[]{hospitalsImported, patientsImported};
    }

    /**
     * Exports the full map (hospitals + patients) to a CSV file.
     * @param map      the map to export
     * @param filePath destination file path
     * @throws IllegalArgumentException if map or filePath is invalid
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
                throw new IOException("Cannot create directory: "
                    + parent.getAbsolutePath());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // En-tête
            bw.write("# Voronoi Hospital Map Export");
            bw.newLine();
            bw.write("# Format : [HOSPITALS] name,x,y,maxCapacity");
            bw.newLine();
            bw.write("#          [PATIENTS]  x,y");
            bw.newLine();
            bw.newLine();

            // Section hôpitaux
            bw.write(SECTION_HOSPITALS);
            bw.newLine();
            for (Hospital h : map.getHospitals()) {
                bw.write(h.getId() + ","
                    + h.getX() + ","
                    + h.getY() + ","
                    + h.getMaxCapacity());
                bw.newLine();
            }

            bw.newLine();

            // Section patients
            bw.write(SECTION_PATIENTS);
            bw.newLine();
            for (User p : map.getUserTot()) {
                bw.write(p.getX() + "," + p.getY());
                bw.newLine();
            }
        }

        System.out.println("Map exported to CSV: " + filePath);
        System.out.println("  Hospitals : " + map.getHospitals().size());
        System.out.println("  Patients  : " + map.getUserTot().size());
    }

    
    /**
     * Validates that the file path and map are valid.
     * @throws IllegalArgumentException if invalid
     * @throws IOException if file does not exist or is not readable
     */
    private static void validateFileAndMap(String filePath, VoronoiMap map)
            throws IOException {
        if (filePath == null || filePath.trim().isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty");
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");

        File file = new File(filePath);
        if (!file.exists())
            throw new IOException("File not found: " + filePath);
        if (!file.isFile())
            throw new IOException("Path is not a file: " + filePath);
        if (!file.canRead())
            throw new IOException("File is not readable: " + filePath);
    }

    /**
     * Parses a hospital line and adds it to the map.
     * @return true if successfully parsed and added
     */
    private static boolean parseHospitalLine(
            String line, int lineNum, VoronoiMap map) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            System.err.println("Line " + lineNum
                + " skipped (expected 4 columns): " + line);
            return false;
        }
        try {
            String name  = parts[0].trim();
            double x     = Double.parseDouble(parts[1].trim());
            double y     = Double.parseDouble(parts[2].trim());
            int capacity = Integer.parseInt(parts[3].trim());

            if (name.isEmpty()) {
                System.err.println("Line " + lineNum
                    + " skipped (empty name)");
                return false;
            }
            if (capacity <= 0) {
                System.err.println("Line " + lineNum
                    + " skipped (capacity must be > 0)");
                return false;
            }
            if (!Double.isFinite(x) || !Double.isFinite(y)) {
                System.err.println("Line " + lineNum
                    + " skipped (invalid coordinates)");
                return false;
            }

            map.addHospital(new Hospital(
                map.generateId(), x, y, capacity));
            return true;

        } catch (NumberFormatException e) {
            System.err.println("Line " + lineNum
                + " skipped (invalid number): " + line);
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Line " + lineNum
                + " skipped (" + e.getMessage() + ")");
            return false;
        }
    }

    /**
     * Imports hospitals from a CSV file into the engine, then recomputes triangulation once.
     * Prefer this over importHospitalsCSV(filePath, map) to keep geometry up to date.
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
     * Imports a full map (hospitals + patients) from CSV into the engine,
     * then recomputes triangulation once.
     * @param filePath source CSV file path
     * @param engine   the Delaunay engine to populate
     * @return int[]{hospitalsImported, patientsImported}
     * @throws IOException if the file cannot be read
     */
    public static int[] importFullMapCSV(String filePath, TriangulationDelaunay engine)
            throws IOException {
        int[] counts = importFullMapCSV(filePath, engine.getMap());
        if (counts[0] > 0 || counts[1] > 0) engine.recompute();
        return counts;
    }

    /**
     * Parses a patient line and adds it to the map.
     * @return true if successfully parsed and added
     */
    private static boolean parsePatientLine(
            String line, int lineNum, VoronoiMap map) {
        String[] parts = line.split(",");
        if (parts.length < 2) {
            System.err.println("Line " + lineNum
                + " skipped (expected 2 columns): " + line);
            return false;
        }
        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());

            if (!Double.isFinite(x) || !Double.isFinite(y)) {
                System.err.println("Line " + lineNum
                    + " skipped (invalid coordinates)");
                return false;
            }

            map.addUsertot(new User(map.generateId(), x, y));
            return true;

        } catch (NumberFormatException e) {
            System.err.println("Line " + lineNum
                + " skipped (invalid number): " + line);
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Line " + lineNum
                + " skipped (" + e.getMessage() + ")");
            return false;
        }
    }
}