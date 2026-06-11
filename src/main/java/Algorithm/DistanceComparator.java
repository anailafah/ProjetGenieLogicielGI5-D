package Algorithm;

import Model.Point;
import java.util.Comparator;
/**
 * Comparator that sorts points by distance to a reference point
 */
public class DistanceComparator implements Comparator<Point> {
    private Point reference;
    /**
     * construction of the comparator
     * @param reference the point from which the distances are calculated
     */
    public DistanceComparator(Point reference){
        this.reference = reference;
    }
    /** 
     * compare 2 points by their distance to the reference
     * @param p1 first point
     * @param p2 second point
     * @return negative if p1 is closer, positif if p2 is closer
     */
    @Override
    public int compare(Point p1,Point p2){
        double dist1 = GeometryFunc.distance(reference, p1);
        double dist2 = GeometryFunc.distance(reference, p2);
        return Double.compare(dist1, dist2);
    }
    
}
