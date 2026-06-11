package Model;
import java.io.Serializable;
/**
 * represents a triangle in delaunay triangulation
 */
public class Triangle implements Serializable {
    private static final long serialVersionUID = 4L;
    private Hospital a,b,c;
    private Point circumcenter;
    private double circumradius;
    /**
     * construction of a triangle
     * @param a
     * @param b
     * @param c
     */
    public Triangle(Hospital a, Hospital b, Hospital c) {
        this.a = a;
        this.b = b;
        this.c = c;
        computeCircumcircle();
    }
    /**
     * Computes the circumcenter and circumradius of this triangle.
     */
    private void computeCircumcircle() {
        double ax = a.getX(), ay = a.getY();
        double bx = b.getX(), by = b.getY();
        double cx = c.getX(), cy = c.getY();

        double D = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));
        if (Math.abs(D) < 1e-10) {
            circumcenter = new Point(-1, 0, 0);
            circumradius = Double.MAX_VALUE;
            return;
        }

        double ux = ((ax*ax + ay*ay) * (by - cy) + (bx*bx + by*by) * (cy - ay) + (cx*cx + cy*cy) * (ay - by)) / D;

        double uy = ((ax*ax + ay*ay) * (cx - bx)+ (bx*bx + by*by) * (ax - cx)+ (cx*cx + cy*cy) * (bx - ax)) / D;

        circumcenter = new Point(-1, ux, uy);
        circumradius = Math.sqrt((ax - ux)*(ax - ux) + (ay - uy)*(ay - uy));
    }
     /**
     * Checks if this triangle shares an edge with another triangle.
     * @param other the other triangle
     * @return true if they share exactly 2 vertices
     */
    public boolean sharesEdge(Triangle other) {
        int shared = 0;
        Hospital[] thisHospitals = {a, b, c};
        Hospital[] otherHospitals = {other.a, other.b, other.c};
        for (Hospital h : thisHospitals)
            for (Hospital o : otherHospitals)
                if (h == o) shared++;
        return shared == 2;
    }
    /**
     * @return one of the vertices of the triangle
    */
    public Hospital getA(){ 
        return a; 
    }
    /**
     * @return one of the vertices of the triangle
    */
    public Hospital getB(){ 
        return b; 
    }
    /**
     * @return one of the vertices of the triangle
    */
    public Hospital getC(){
        return c; 
    }
    /**
     * @return the circumcenter of the triangle
    */
    public Point getCircumcenter(){ 
        return circumcenter; 
    }
    /**
     * @return the circumradius of the triangle
    */
    public double getCircumradius(){
        return circumradius; 
    }
    /**
     * @return a string that contains a triangle
    */
     @Override
    public String toString() {
        return "Triangle[" + a.getId() + ", " + b.getId() + ", " + c.getId() + "]";
    }
       
}
