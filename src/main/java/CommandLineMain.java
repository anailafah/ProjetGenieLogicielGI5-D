import Model.*;

import java.util.List;
import java.util.ArrayList;
public class CommandLineMain {

    public static void main(String[] args) {

        System.out.println("=== Test du modèle ===\n");

        // Test Point
        System.out.println("-- Test Point --");
        Point p = new Point(0, 10.0, 20.0);
        System.out.println("Point créé : " + p);
        System.out.println("X=" + p.getX() + " Y=" + p.getY() + " ID=" + p.getId());

        // Test Hospital 
        System.out.println("\n-- Test Hospital --");
        Hospital h1 = new Hospital(1, 100.0, 150.0, 3);
        Hospital h2 = new Hospital(2, 400.0, 300.0, 2);
        Hospital h3 = new Hospital(3, 200.0, 400.0, 5);
        System.out.println(h1);
        System.out.println("Saturé ? " + h1.isSaturated());
        System.out.println("Places libres : " + h1.getAvailableRoom());
        System.out.println("Taux de place libre : " + h1.getPercentAvailable() + "%");

        // Test Patient
        System.out.println("\n-- Test Patient --");
        User u1 = new User(4, 120.0, 160.0);
        User u2 = new User(5, 380.0, 290.0);
        User u3 = new User(6, 105.0, 145.0);
        User u4 = new User(7, 110.0, 155.0);
        System.out.println("user créé : " + u1);

        // Test VoronoiMap 
        System.out.println("\n-- Test VoronoiMap --");
        VoronoiMap map = new VoronoiMap();
        map.addHospital(h1);
        map.addHospital(h2);
        map.addHospital(h3);
        map.addUsertot(u1);
        map.addUsertot(u2);
        map.addUsertot(u3);
        map.addUsertot(u4);
        System.out.println(map);
        System.out.println("Nb hôpitaux : " + map.getHospitals().size());
        System.out.println("Nb patients : " + map.getUserTot().size());
        System.out.println("ID généré : " + map.generateId());

        // Test Triangle 
        System.out.println("\n-- Test Triangle --");
        Triangle t = new Triangle(h1, h2, h3);
        System.out.println(t);
        System.out.println("Surface : " + t.getArea());
        System.out.println("Circumcenter : " + t.getCircumcenter());
        System.out.println("Circumradius : " + t.getCircumradius());
        System.out.println("Point dans circumcircle ? " + u1.isInCircumcircle(t.getA(),t.getB(),t.getC()));
        System.out.println("Déséquilibre : " + t.getImbalance());

        //  Test affectation patient → hôpital 
        System.out.println("\n-- Test affectation patient → hôpital --");
        List<Hospital> byDist = new ArrayList<>();
        byDist.add(h1);
        byDist.add(h2);
        byDist.add(h3);
        u1.setNextHospitals(byDist);
        u1.setClosestSite(h1);
        h1.addUsers(u1);
        System.out.println("Patient 1 assigné à : " + u1.getClosestSite().getId());
        System.out.println("Redirigé ? " + u1.getIsRedirected());

        //  Test saturation et redirection 
        System.out.println("\n-- Test saturation --");
        u2.setNextHospitals(byDist);
        u2.setClosestSite(h1);
        h1.addUsers(u2);

        u3.setNextHospitals(byDist);
        u3.setClosestSite(h1);
        h1.addUsers(u3);

        System.out.println("Hôpital A saturé ? " + h1.isSaturated());
        System.out.println("Taux place dispo A  : " + h1.getPercentAvailable() + "%");

        // pat4 doit être redirigé vers h2
        u4.setNextHospitals(byDist);
        if (h1.isSaturated()) {
            u4.setClosestSite(h2);
            h2.addUsers(u4);
        } else {
            u4.setClosestSite(h3);
            h1.addUsers(u4);
        }
        System.out.println("Patient 4 assigné à : " + u4.getClosestSite().getId());
        System.out.println("Patient 4 redirigé ? " + u4.getIsRedirected());
        System.out.println("Rang de redirection : " + u4.getRedirectionRank());

        //  Test GeometryFunc
        System.out.println("\n-- Test GeometryFunc --");
        double dist = Algorithm.GeometryFunc.distance(h1, h2);
        System.out.println("Distance H1-H2 : " + dist);
        double area = Algorithm.GeometryFunc.triangleArea(h1, h2, h3);
        System.out.println("Surface triangle : " + area);

    }
}