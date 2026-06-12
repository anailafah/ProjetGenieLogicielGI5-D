package Algorithm;

import Model.Hospital;
import Model.VoronoiMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages import and export of VoronoiMap data.
 *
 * This class can save and load hospitals using CSV files and binary files.
 */
public final class ImportExportManager {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ImportExportManager() {
    }

    /**
     * Exports hospitals from a VoronoiMap to a CSV file.
     *
     * CSV format: {@code id,name,x,y,maxCapacity}.
     *
     * @param map the map containing the hospitals to export
     * @param filePath the path of the CSV file
     * @throws IllegalArgumentException if the map is null
     * @throws IOException if writing the file fails
     */
    public static void exportHospitalsToCSV(VoronoiMap map, String filePath) throws IOException {
        if (map == null) {
            throw new IllegalArgumentException("VoronoiMap cannot be null.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("id,name,x,y,maxCapacity");
            writer.newLine();

            for (Hospital hospital : map.getHospitals()) {
                writer.write(
                        hospital.getId() + ","
                        + hospital.getName() + ","
                        + hospital.getX() + ","
                        + hospital.getY() + ","
                        + hospital.getMaxCapacity()
                );
                writer.newLine();
            }
        }
    }

    /**
     * Imports hospitals from a CSV file into a new VoronoiMap.
     *
     * Expected CSV format: {@code id,name,x,y,maxCapacity}.
     *
     * @param filePath the path of the CSV file
     * @return a new VoronoiMap containing the imported hospitals
     * @throws IOException if reading the file fails or if a line is invalid
     */
    public static VoronoiMap importHospitalsFromCSV(String filePath) throws IOException {
        VoronoiMap map = new VoronoiMap();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split(",");

                if (values.length != 5) {
                    throw new IOException("Invalid CSV line: " + line);
                }

                int id = Integer.parseInt(values[0].trim());
                String name = values[1].trim();
                double x = Double.parseDouble(values[2].trim());
                double y = Double.parseDouble(values[3].trim());
                int maxCapacity = Integer.parseInt(values[4].trim());

                Hospital hospital = new Hospital(id, name, x, y, maxCapacity);
                map.addHospital(hospital);
            }
        }

        return map;
    }

    /**
     * Exports hospitals from a VoronoiMap to a binary file.
     *
     * Binary files are not human-readable but are more compact.
     *
     * @param map the map containing the hospitals to export
     * @param filePath the path of the binary file
     * @throws IllegalArgumentException if the map is null
     * @throws IOException if writing the file fails
     */
    public static void exportHospitalsToBinary(VoronoiMap map, String filePath) throws IOException {
        if (map == null) {
            throw new IllegalArgumentException("VoronoiMap cannot be null.");
        }

        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(filePath))) {
            output.writeInt(map.getHospitals().size());

            for (Hospital hospital : map.getHospitals()) {
                output.writeInt(hospital.getId());
                output.writeUTF(hospital.getName());
                output.writeDouble(hospital.getX());
                output.writeDouble(hospital.getY());
                output.writeInt(hospital.getMaxCapacity());
            }
        }
    }

    /**
     * Imports hospitals from a binary file into a new VoronoiMap.
     *
     * @param filePath the path of the binary file
     * @return a new VoronoiMap containing the imported hospitals
     * @throws IOException if reading the file fails
     */
    public static VoronoiMap importHospitalsFromBinary(String filePath) throws IOException {
        VoronoiMap map = new VoronoiMap();

        try (DataInputStream input = new DataInputStream(new FileInputStream(filePath))) {
            int hospitalCount = input.readInt();

            for (int i = 0; i < hospitalCount; i++) {
                int id = input.readInt();
                String name = input.readUTF();
                double x = input.readDouble();
                double y = input.readDouble();
                int maxCapacity = input.readInt();

                Hospital hospital = new Hospital(id, name, x, y, maxCapacity);
                map.addHospital(hospital);
            }
        }

        return map;
    }
}