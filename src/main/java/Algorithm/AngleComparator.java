package Algorithm;

import Model.Hospital;
import Model.Point;
import java.util.Comparator;

/**
 * Comparator that sorts points by angle around a center hospital.
 * Used to order Voronoi polygon vertices in clockwise order.
 */
public class AngleComparator implements Comparator<Point> {

    private Hospital center;

    /**
     * Constructs the comparator with a center hospital.
     * @param center the hospital around which to sort
     */
    public AngleComparator(Hospital center) {
        this.center = center;
    }

    /**
     * Compares two points by their angle relative to the center.
     * @param p1 first point
     * @param p2 second point
     * @return negative if p1 comes before p2, positive otherwise
     */
    @Override
    public int compare(Point p1, Point p2) {
        double angle1 = Math.atan2(
            p1.getY() - center.getY(),
            p1.getX() - center.getX()
        );
        double angle2 = Math.atan2(
            p2.getY() - center.getY(),
            p2.getX() - center.getX()
        );
        return Double.compare(angle1, angle2);
    }
}