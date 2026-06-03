package Algorithm;
import Model.*;

import java.io.*;

/**
 * Handles all import and export operations for the map.
 * All files are stored in the data/ folder automatically.
 */
public class ImportExportMap {
    /** Default folder for all files. */
    private static final String DATA_FOLDER = "data/";


    
    /**
     * Builds the full file path from a filename.
     * @param fileName the file name 
     * @return the full path 
     */
    private static String buildPath(String fileName) {
        if (fileName == null || fileName.trim().isEmpty())
            throw new IllegalArgumentException("File name cannot be null or empty");
        return DATA_FOLDER + fileName.trim();
    }

    /**
     * Exports the full VoronoiMap to a binary file in the data/ folder.
     * @param map the map to export
     * @param fileName the file name 
     * @throws IllegalArgumentException if map or fileName is invalid
     * @throws IOException if writing fails
     */
    public static void exportBinary(VoronoiMap map, String fileName)
            throws IOException {

        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");

        String filePath = buildPath(fileName);
        File file = new File(filePath);

        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs())
                throw new IOException("Cannot create data/ folder");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(map);
            oos.flush();
        } 
        catch (NotSerializableException e) {
            throw new IOException( "A class is not Serializable: " + e.getMessage(), e);
        }

        System.out.println("Map exported to: " + filePath);
    }

    /**
     * Imports the full VoronoiMap from a binary file in the data/ folder.
     * @param fileName the file name
     * @return the loaded VoronoiMap
     * @throws IllegalArgumentException if fileName is invalid
     * @throws IOException if reading fails or file is corrupted
     * @throws ClassNotFoundException if the class is not found
     */
    public static VoronoiMap importBinary(String fileName)
            throws IOException, ClassNotFoundException {

        String filePath = buildPath(fileName);
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

            System.out.println("Map imported from: " + filePath);
            return (VoronoiMap) obj;

        } catch (StreamCorruptedException e) {
            throw new IOException(
                "File is corrupted or not a binary map: " + filePath, e);
        } catch (InvalidClassException e) {
            throw new IOException(
                "File was created with an older version. Please export again.", e);
        }
    }

    /**
     * Imports hospitals from a CSV file in the data/ folder.
     * Expected format per line: name,x,y,maxCapacity
     * Lines starting with # are treated as comments and skipped.
     * @param fileName the file name (e.g. "hospitals.csv")
     * @param map      the map to populate (must not be null)
     * @return number of hospitals successfully imported
     * @throws IllegalArgumentException if fileName or map is invalid
     * @throws IOException if the file cannot be read
     */
    public static int importHospitalsCSV(String fileName, VoronoiMap map)
            throws IOException {

        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");

        String filePath = buildPath(fileName);
        File file = new File(filePath);

        if (!file.exists())
            throw new IOException("CSV file not found: " + filePath);
        if (!file.isFile())
            throw new IOException("Path is not a file: " + filePath);
        if (!file.canRead())
            throw new IOException("CSV file is not readable: " + filePath);

        int imported = 0;
        int lineNum  = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");

                if (parts.length < 4) {
                    System.err.println("Line " + lineNum
                        + " skipped (expected name,x,y,capacity): " + line);
                    continue;
                }

                try {
                    String name  = parts[0].trim();
                    double x     = Double.parseDouble(parts[1].trim());
                    double y     = Double.parseDouble(parts[2].trim());
                    int capacity = Integer.parseInt(parts[3].trim());

                    if (name.isEmpty()) {
                        System.err.println("Line " + lineNum
                            + " skipped (empty name)");
                        continue;
                    }
                    if (capacity <= 0) {
                        System.err.println("Line " + lineNum
                            + " skipped (capacity must be > 0)");
                        continue;
                    }
                    if (!Double.isFinite(x) || !Double.isFinite(y)) {
                        System.err.println("Line " + lineNum
                            + " skipped (invalid coordinates)");
                        continue;
                    }

                    map.addHospital(new Hospital(map.generateId(), name,x, y, capacity));
                    imported++;

                } catch (NumberFormatException e) {
                    System.err.println("Line " + lineNum
                        + " skipped (invalid number): " + line);
                } catch (IllegalArgumentException e) {
                    System.err.println("Line " + lineNum
                        + " skipped (" + e.getMessage() + ")");
                }
            }
        }

        System.out.println("Imported " + imported
            + " hospital(s) from: " + filePath);
        return imported;
    }
}