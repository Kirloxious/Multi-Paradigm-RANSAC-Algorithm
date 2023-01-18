


public class Plane3D {
    
    //Points defining the plane
    private Point3D p1, p2, p3;

    //Plane equation scalars
    private double a, b, c, d;


    public Plane3D(Point3D p1, Point3D p2, Point3D p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        calculatePlaneEquation();
    }



    public Plane3D(double a, double b, double c, double d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Plane3D(){}


    public double getDistance(Point3D pt){
        
        double numerator = Math.abs((this.a*pt.getX())+(this.b*pt.getY())+(this.c*pt.getZ())+this.d);
        double denomenator = Math.sqrt(Math.pow(this.a, 2)+Math.pow(this.b, 2)+Math.pow(this.c, 2));

        double distance = numerator/denomenator;
        
        return distance;
    }

    private void calculatePlaneEquation(){
        Point3D vectorP1P2 = p1.calculatePointVector(p2);
        Point3D vectorP1P3 = p1.calculatePointVector(p3);

        vectorCrossProduct(vectorP1P2, vectorP1P3);

        this.d = (this.a * -p3.getX()) + (this.b * -p3.getY()) + (this.c * -p3.getZ());

    }

    public void vectorCrossProduct(Point3D vec1, Point3D vec2){
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

    //Prints the plane equation
    @Override
    public String toString() {
        return this.a + "x + " + this.b + "y + " + this.c + "z = " + -this.d;
    }
}
