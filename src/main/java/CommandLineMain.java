import Algorithm.VoronoiEngineStub;
import Model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineMain {

    private static VoronoiEngineStub engine = new VoronoiEngineStub();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        afficherAide();
        System.out.println("3 hopitaux de test chargés. Tapez 'list' pour les voir.\n");

        while (true) {
            System.out.print("> ");
            String ligne = sc.nextLine().trim();

            if (ligne.isEmpty()) continue;

            String[] mots = ligne.split("\\s+");
            String cmd = mots[0];

            if (cmd.equals("add-hospital")) {
                ajouterHopital(mots);

            } else if (cmd.equals("remove")) {
                supprimerHopital(mots);

            } else if (cmd.equals("move")) {
                deplacerHopital(mots);

            } else if (cmd.equals("list")) {
                listerHopitaux();

            } else if (cmd.equals("stats")) {
                afficherStats(mots);

            } else if (cmd.equals("add-patient")) {
                ajouterPatient(mots);

            } else if (cmd.equals("rm-patient")) {
                supprimerPatient(mots);

            } else if (cmd.equals("patients")) {
                listerPatients();

            } else if (cmd.equals("nearest")) {
                hopitalLePlusProche(mots);

            } else if (cmd.equals("triangles")) {
                afficherTriangles();

            } else if (cmd.equals("clear")) {
                viderCarte();

            } else if (cmd.equals("help")) {
                afficherAide();

            } else if (cmd.equals("quit") || cmd.equals("exit")) {
                System.out.println("Au revoir !");
                break;

            } else {
                System.out.println("Commande inconnue. Tapez 'help' pour voir les commandes disponibles.");
            }
        }
    }

    private static void ajouterHopital(String[] mots) {
        if (mots.length < 5) {
            System.out.println("Usage : add-hospital <x> <y> <nom> <capacite>");
            return;
        }
        try {
            double x = Double.parseDouble(mots[1]);
            double y = Double.parseDouble(mots[2]);
            // mots[3] = nom, pas encore utilisé dans le modèle
            int capacite = Integer.parseInt(mots[4]);
            Hospital h = engine.addHospital(x, y, capacite);
            System.out.println("Hopital ajouté : " + h);
        } catch (NumberFormatException e) {
            System.out.println("Erreur : les coordonnées et la capacité doivent être des nombres.");
        }
    }

    private static void supprimerHopital(String[] mots) {
        if (mots.length < 2) {
            System.out.println("Usage : remove <id>");
            return;
        }
        try {
            int id = Integer.parseInt(mots[1]);
            Hospital h = trouverHopital(id);
            if (h == null) {
                System.out.println("Aucun hopital avec l'id " + id);
                return;
            }
            engine.removeHospital(h);
            System.out.println("Hopital " + id + " supprimé.");
        } catch (NumberFormatException e) {
            System.out.println("Erreur : l'id doit être un nombre entier.");
        }
    }

    private static void deplacerHopital(String[] mots) {
        if (mots.length < 4) {
            System.out.println("Usage : move <id> <x> <y>");
            return;
        }
        try {
            int id = Integer.parseInt(mots[1]);
            Hospital h = trouverHopital(id);
            if (h == null) {
                System.out.println("Aucun hopital avec l'id " + id);
                return;
            }
            double x = Double.parseDouble(mots[2]);
            double y = Double.parseDouble(mots[3]);
            engine.moveHospital(h, x, y);
            System.out.println("Hopital " + id + " déplacé en (" + x + ", " + y + ")");
        } catch (NumberFormatException e) {
            System.out.println("Erreur : les valeurs doivent être des nombres.");
        }
    }

    private static void listerHopitaux() {
        List<Hospital> hopitaux = engine.getMap().getHospitals();
        if (hopitaux.isEmpty()) {
            System.out.println("Aucun hopital enregistré.");
            return;
        }
        for (Hospital h : hopitaux) {
            String statut;
            if (h.isSaturated()) {
                statut = "SATURÉ";
            } else {
                statut = h.getAvailableRoom() + " places disponibles";
            }
            System.out.println("  [" + h.getId() + "] (" + (int)h.getX() + ", " + (int)h.getY() + ")  capacité:" + h.getMaxCapacity() + "  " + statut);
        }
    }

    private static void afficherStats(String[] mots) {
        if (mots.length < 2) {
            System.out.println("Usage : stats <id>");
            return;
        }
        try {
            int id = Integer.parseInt(mots[1]);
            Hospital h = trouverHopital(id);
            if (h == null) {
                System.out.println("Aucun hopital avec l'id " + id);
                return;
            }
            System.out.println("Hopital " + h.getId() + " à (" + (int)h.getX() + ", " + (int)h.getY() + ")");
            System.out.println("  Patients : " + h.getUsers().size() + " / " + h.getMaxCapacity());
            if (h.isSaturated()) {
                System.out.println("  Statut   : SATURÉ");
            } else {
                System.out.println("  Statut   : OK — " + h.getAvailableRoom() + " places libres");
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur : l'id doit être un nombre entier.");
        }
    }
