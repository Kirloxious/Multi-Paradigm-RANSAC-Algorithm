/*
 * Nom: Alexandre Ringuette
 * Student number: 300251252
 */

/**
 * This class defines a 3D plane using 3 points to calculate it's equation.
 * @author Alexandre Ringuette
 */
public class Plane3D {
    
    /******************** Instances variables ********************/

    //Points defining the plane
    private Point3D p1, p2, p3;

    //Plane equation coefficients
    private double a, b, c, d;

    //Plane point cloud
    private PointCloud pc;

    /******************** Contructors ********************/

    /**
     * Creates the plane with 3 point given as arguments.
     * @param p1
     * @param p2
     * @param p3
     */
    public Plane3D(Point3D p1, Point3D p2, Point3D p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        calculatePlaneEquation();
        pc = new PointCloud();
        pc.addPoint(p1);
        pc.addPoint(p2);
        pc.addPoint(p3);
        
    }


    /**
     * Creates the plane using the coefficients.
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public Plane3D(double a, double b, double c, double d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * Empty Contructor
     */
    public Plane3D(){}

    /******************** Class Methods ********************/

    
    /** 
     * Calculates the distance between a point and this plane.
     * @param pt
     * @return double
     */
    public double getDistance(Point3D pt){
        
        double numerator = Math.abs((this.a*pt.getX())+(this.b*pt.getY())+(this.c*pt.getZ())+this.d);
        double denomenator = Math.sqrt(Math.pow(this.a, 2)+Math.pow(this.b, 2)+Math.pow(this.c, 2));

        double distance = numerator/denomenator;
        
        return distance;
    }

    
    /** 
     * Calculates the cross prodcut of two vectors.
     * @param vec1
     * @param vec2
     */
    private void vectorCrossProduct(Point3D vec1, Point3D vec2){
        // a x b = i(a2b3 - a3b2) + j(a3b1 - a1b3) + k(a1b2 - a2b1) = N
        double a1 = vec1.getX();
        double a2 = vec1.getY();
        double a3 = vec1.getZ();
        
        double b1 = vec2.getX();
        double b2 = vec2.getY();
        double b3 = vec2.getZ();
        
        
        this.a = (a2*b3 - a3*b2);
        this.b = (a3*b1 - a1*b3);
        this.c = (a1*b2 - a2*b1);
        
    }
    
    /**
     * Calculates the plane equation.
     */
    private void calculatePlaneEquation(){
        Point3D vectorP1P2 = p1.calculatePointVector(p2);
        Point3D vectorP1P3 = p1.calculatePointVector(p3);

        vectorCrossProduct(vectorP1P2, vectorP1P3);

        this.d = (this.a * -p3.getX()) + (this.b * -p3.getY()) + (this.c * -p3.getZ());

    }
    

    /******************** Getters & Setters ********************/

    /** 
     * Set the plane point cloud.
     * @param pc
     */
    public void setPlanePointCloud(PointCloud pc){
        this.pc = pc;
    }

    
    /** 
     * Get the plane point cloud.
     * @return {@code PointCloud}
     */
    public PointCloud getPlanePointCloud(){
        return this.pc;
    }

    
    /** 
     * Prints the plane equation
     * @return {@code String}
     */
    @Override
    public String toString() {
        return this.a + "x + " + this.b + "y + " + this.c + "z = " + -this.d;
    }
}
