package Interface;

import Algorithm.*;
import Model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Random;

/**
 * Controller linked to main.fxml via SceneBuilder.
 * Handles all user interactions and connects the UI to the engine.
 */
public class MainController {

    @FXML private Pane         mapPane;
    @FXML private CheckBox     checkDelaunay;
    @FXML private CheckBox     checkZones;
    @FXML private CheckBox     checkLinks;
    @FXML private CheckMenuItem menuDelaunay;
    @FXML private CheckMenuItem menuZones;
    @FXML private CheckMenuItem menuLinks;
    @FXML private Label         labelHospitalName;
    @FXML private ProgressBar   progressSaturation;
    @FXML private Label         labelStats;
    @FXML private Label         labelNbHospitals;
    @FXML private Label         labelNbUsers;
    @FXML private Label         labelNbTriangles;
    @FXML private Label         labelZoom;
    @FXML private Label         labelMessage;

    
    private VoronoiEngine engine;
    private MapCanvas     canvas;

    private Hospital selectedHospital = null;
    private Hospital draggedHospital  = null;
    private boolean  isDragging       = false;
    private double   dragStartX, dragStartY;
    private double   scale = 1.0;

    /**
     * Injects the Voronoi engine into the controller.
     * Must be called before init().
     * @param engine the Voronoi engine
     */
    public void setEngine(VoronoiEngine engine) {
        this.engine = engine;
    }

    /**
     * Initializes the canvas and adds it to the mapPane.
     * Must be called after setEngine().
     * @param width  canvas width
     * @param height canvas height
     */
    public void init(double width, double height) {
        canvas = new MapCanvas(width, height, engine);

        mapPane.getChildren().add(canvas);

        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());

        canvas.widthProperty().addListener(e  -> canvas.redraw());
        canvas.heightProperty().addListener(e -> canvas.redraw());

        updateStatusBar();
    }
    /**
     * Handles mouse press — prepares drag detection.
     * @param e mouse event
     */
    @FXML
    private void onMapPressed(MouseEvent e) {
        dragStartX      = e.getX();
        dragStartY      = e.getY();
        isDragging      = false;
        double wx       = canvas.toWorldX(e.getX());
        double wy       = canvas.toWorldY(e.getY());
        draggedHospital = getHospitalAt(wx, wy);
    }

    /**
     * Handles mouse click on the map.
     * Left click = add hospital or select.
     * Ctrl + Left click = add user.
     * Right click = remove hospital.
     * @param e mouse event
     */
    @FXML
    private void onMapClicked(MouseEvent e) {
        if (isDragging) { isDragging = false; return; }

        double wx = canvas.toWorldX(e.getX());
        double wy = canvas.toWorldY(e.getY());

        try {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (e.isControlDown()) {
                    // Ctrl + clic gauche = ajouter un user
                    if (engine.getMap().getHospitals().isEmpty()) {
                        showMessage("Ajoutez d'abord un hôpital !");
                        return;
                    }
                    engine.addUser(wx, wy);
                    showMessage("User ajouté");

                } else {
                    // Clic gauche = sélectionner ou ajouter hôpital
                    Hospital clicked = getHospitalAt(wx, wy);
                    if (clicked != null) {
                        selectHospital(clicked);
                    } else {
                        String name = showInputDialog(
                            "Nom de l'hôpital :",
                            "Hopital-" + (engine.getMap().getHospitals().size() + 1));
                        if (name == null || name.trim().isEmpty()) return;

                        String capStr = showInputDialog("Capacité maximale :", "50");
                        if (capStr == null) return;

                        int capacity = Integer.parseInt(capStr.trim());
                        if (capacity <= 0) {
                            showMessage("La capacité doit être > 0");
                            return;
                        }

                        Hospital h = engine.addHospital(wx, wy, name.trim(), capacity);
                        selectHospital(h);
                        showMessage("Hôpital ajouté : " + name);
                    }
                }

            } else if (e.getButton() == MouseButton.SECONDARY) {
                // Clic droit = supprimer hôpital
                Hospital clicked = getHospitalAt(wx, wy);
                if (clicked != null) {
                    engine.removeHospital(clicked);
                    if (selectedHospital == clicked) clearSelection();
                    showMessage("Hôpital supprimé");
                }
            }

        } catch (NumberFormatException ex) {
            showMessage("Erreur : capacité invalide");
        } catch (IllegalArgumentException ex) {
            showMessage("Erreur : " + ex.getMessage());
        }

        canvas.redraw();
        updateStatusBar();
    }

    /**
     * Handles mouse drag — moves a hospital or pans the view.
     * @param e mouse event
     */
    @FXML
    private void onMapDragged(MouseEvent e) {
        isDragging = true;
        double wx  = canvas.toWorldX(e.getX());
        double wy  = canvas.toWorldY(e.getY());

        try {
            if (draggedHospital != null) {
                engine.moveHospital(draggedHospital, wx, wy);
                updateSidePanel();
            } else {
                double dx = e.getX() - dragStartX;
                double dy = e.getY() - dragStartY;
                canvas.addOffset(dx, dy);
                dragStartX = e.getX();
                dragStartY = e.getY();
            }
        } catch (IllegalArgumentException ex) {
            showMessage("Erreur déplacement : " + ex.getMessage());
        }

        canvas.redraw();
    }

    /**
     * Handles mouse scroll — zooms in or out.
     * @param e scroll event
     */
    @FXML
    private void onMapScroll(ScrollEvent e) {
        double factor = e.getDeltaY() > 0 ? 1.1 : 0.9;
        scale *= factor;
        scale = Math.max(0.2, Math.min(5.0, scale));
        canvas.setScale(scale);
        canvas.redraw();
        labelZoom.setText("Zoom : " + (int)(scale * 100) + "%");
    }

    /**
     * Adds a random number of users to the map.
     */
    @FXML
    private void onRandomUsers() {
        if (engine.getMap().getHospitals().isEmpty()) {
            showMessage("Ajoutez d'abord des hôpitaux !");
            return;
        }
        try {
            String input = showInputDialog("Nombre de users à ajouter :", "20");
            if (input == null) return;

            int count = Integer.parseInt(input.trim());
            if (count <= 0) {
                showMessage("Le nombre doit être > 0");
                return;
            }
            if (count > 10000) {
                showMessage("Maximum 10 000 users à la fois");
                return;
            }

            Random rnd = new Random();
            double w   = canvas.getWidth();
            double h   = canvas.getHeight();

            for (int i = 0; i < count; i++) {
                double x = canvas.toWorldX(rnd.nextDouble() * w);
                double y = canvas.toWorldY(rnd.nextDouble() * h);
                engine.addUser(x, y);
            }

            canvas.redraw();
            updateStatusBar();
            showMessage(count + " users ajoutés aléatoirement");

        } catch (NumberFormatException e) {
            showMessage("Erreur : entrez un entier");
        } catch (IllegalArgumentException e) {
            showMessage("Erreur : " + e.getMessage());
        }
    }

    /**
     * Opens a FileChooser to import hospitals from a CSV file.
     */
    @FXML
    private void onImportCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Importer des hôpitaux");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));

        File dataDir = new File("data");
        if (dataDir.exists()) fc.setInitialDirectory(dataDir);

        File file = fc.showOpenDialog(mapPane.getScene().getWindow());
        if (file == null) return;

        try {
            int count = ImportExportMap.importHospitalsCSV(
                file.getAbsolutePath(), engine.getMap());
            canvas.redraw();
            updateStatusBar();
            showMessage(count + " hôpitaux importés depuis " + file.getName());
        } catch (IllegalArgumentException e) {
            showMessage("Erreur (argument) : " + e.getMessage());
        } catch (Exception e) {
            showMessage("Erreur import CSV : " + e.getMessage());
        }
    }

    /**
     * Opens a FileChooser to export the full map in binary format.
     */
    @FXML
    private void onExportBinary() {
        if (engine.getMap().getHospitals().isEmpty()) {
            showMessage("La carte est vide, rien à exporter");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter la carte");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Carte binaire", "*.bin"));
        fc.setInitialFileName("map.bin");

        File file = fc.showSaveDialog(mapPane.getScene().getWindow());
        if (file == null) return;

        try {
            ImportExportMap.exportBinary(
                engine.getMap(), file.getAbsolutePath());
            showMessage("Carte exportée : " + file.getName());
        } catch (IllegalArgumentException e) {
            showMessage("Erreur (argument) : " + e.getMessage());
        } catch (Exception e) {
            showMessage("Erreur export : " + e.getMessage());
        }
    }

    /**
     * Opens a FileChooser to import a full map from binary format.
     */
    @FXML
    private void onImportBinary() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Importer une carte");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Carte binaire", "*.bin"));

        File file = fc.showOpenDialog(mapPane.getScene().getWindow());
        if (file == null) return;

        try {
            VoronoiMap loaded = ImportExportMap.importBinary(
                file.getAbsolutePath());

            engine = new TriangulationDelaunay(canvas.getWidth(), canvas.getHeight());
            for (Hospital h : loaded.getHospitals())
                engine.getMap().addHospital(h);
            for (User u : loaded.getUserTot())
                engine.getMap().addUsertot(u);

            canvas.setEngine(engine);
            clearSelection();
            canvas.redraw();
            updateStatusBar();
            showMessage("Carte importée : " + file.getName());

        } catch (ClassNotFoundException e) {
            showMessage("Erreur : fichier incompatible avec cette version");
        } catch (IllegalArgumentException e) {
            showMessage("Erreur (argument) : " + e.getMessage());
        } catch (Exception e) {
            showMessage("Erreur import : " + e.getMessage());
        }
    }

    /**
     * Clears the entire map after confirmation.
     */
    @FXML
    private void onClearAll() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Effacer tout");
        confirm.setHeaderText("Voulez-vous vraiment effacer toute la carte ?");
        confirm.setContentText("Cette action est irréversible.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                engine = new TriangulationDelaunay(canvas.getWidth(), canvas.getHeight());
                canvas.setEngine(engine);
                clearSelection();
                canvas.redraw();
                updateStatusBar();
                showMessage("Carte effacée");
            }
        });
    }

    /**
     * Toggles Delaunay triangulation display.
     * Synchronizes CheckBox and CheckMenuItem.
     */
    @FXML
    private void onToggleDelaunay() {
        boolean show = checkDelaunay.isSelected();
        canvas.setShowDelaunay(show);
        if (menuDelaunay != null) menuDelaunay.setSelected(show);
        canvas.redraw();
    }

    /**
     * Toggles Voronoi zones display.
     * Synchronizes CheckBox and CheckMenuItem.
     */
    @FXML
    private void onToggleZones() {
        boolean show = checkZones.isSelected();
        canvas.setShowZones(show);
        if (menuZones != null) menuZones.setSelected(show);
        canvas.redraw();
    }

    /**
     * Toggles user-to-hospital links display.
     * Synchronizes CheckBox and CheckMenuItem.
     */
    @FXML
    private void onToggleLinks() {
        boolean show = checkLinks.isSelected();
        canvas.setShowLinks(show);
        if (menuLinks != null) menuLinks.setSelected(show);
        canvas.redraw();
    }

    /**
     * Shows the about dialog.
     */
    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Voronoï Hôpital");
        alert.setContentText(
            "Projet PGL — ING1-GI CYTech 2025-2026\n" +
            "Répartition des patients vers les hôpitaux\n" +
            "Algorithme de Bowyer-Watson");
        alert.showAndWait();
    }

    /**
     * Selects a hospital and updates the side panel.
     * @param h the hospital to select
     */
    private void selectHospital(Hospital h) {
        selectedHospital = h;
        canvas.setSelectedHospital(h);
        updateSidePanel();
        canvas.redraw();
    }

    /** Clears the current hospital selection. */
    private void clearSelection() {
        selectedHospital = null;
        canvas.setSelectedHospital(null);
        labelHospitalName.setText("Aucun hôpital sélectionné");
        progressSaturation.setProgress(0);
        progressSaturation.setStyle("");
        labelStats.setText("");
    }

    /** Updates the side panel with the selected hospital's stats. */
    private void updateSidePanel() {
        if (selectedHospital == null) return;

        labelHospitalName.setText(selectedHospital.getName());

        double rate = selectedHospital.getSaturationRate() / 100.0;
        progressSaturation.setProgress(rate);

        if (rate >= 1.0)
            progressSaturation.setStyle("-fx-accent: #CC2200;");
        else if (rate >= 0.75)
            progressSaturation.setStyle("-fx-accent: #E07700;");
        else
            progressSaturation.setStyle("-fx-accent: #1D9E75;");

        long redirected = selectedHospital.getUsers().stream()
            .filter(User::getIsRedirected).count();

        labelStats.setText(
            "Patients : " + selectedHospital.getUsers().size()
                + " / " + selectedHospital.getMaxCapacity() + "\n" +
            "Saturation : "
                + String.format("%.1f", selectedHospital.getSaturationRate()) + "%\n" +
            "Places libres : " + selectedHospital.getAvailableRoom() + "\n" +
            "Redirigés : " + redirected
        );
    }

    /** Updates the status bar labels at the bottom. */
    private void updateStatusBar() {
        labelNbHospitals.setText(
            "Hôpitaux : " + engine.getMap().getHospitals().size());
        labelNbUsers.setText(
            "Users : " + engine.getMap().getUserTot().size());
        labelNbTriangles.setText(
            "Triangles : " + engine.getTriangles().size());
    }

    /**
     * Displays a message in the status bar.
     * @param msg the message to display
     */
    private void showMessage(String msg) {
        if (labelMessage != null)
            labelMessage.setText(msg);
    }

    /**
     * Shows a simple text input dialog.
     * @param prompt       the question to ask
     * @param defaultValue the default value
     * @return the user input, or null if cancelled
     */
    private String showInputDialog(String prompt, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Saisie");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        return dialog.showAndWait().orElse(null);
    }

    /**
     * Finds a hospital near the given world coordinates (within 15px).
     * @param wx world x
     * @param wy world y
     * @return the hospital if found, null otherwise
     */
    private Hospital getHospitalAt(double wx, double wy) {
        for (Hospital h : engine.getMap().getHospitals()) {
            double dx = h.getX() - wx;
            double dy = h.getY() - wy;
            if (Math.sqrt(dx*dx + dy*dy) < 15.0 / scale)
                return h;
        }
        return null;
    }
}