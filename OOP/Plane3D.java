
import java.util.HashMap;


public class Plane3D {
    

    Point3D p1;
    Point3D p2;
    Point3D p3;

    //Plane equation values
    double planeX;
    double planeY;
    double planeZ;
    double d;


    public Plane3D(Point3D p1, Point3D p2, Point3D p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        calculatePlaneEquation();
    }



    public Plane3D(double a, double b, double c, double d){

    }


    public double getDistance(Point3D pt){
        return 0;
    }

    private void calculatePlaneEquation(){
        Point3D vectorP1P2 = p1.calculatePointVector(p2);
        Point3D vectorP1P3 = p1.calculatePointVector(p3);

        HashMap<String, Double> crossProductResult = vectorP1P2.vectorCrossProduct(vectorP1P3);

        this.planeX = crossProductResult.get("i");
        this.planeY = crossProductResult.get("j");
        this.planeZ = crossProductResult.get("k");

        this.d = (planeX * p3.getX()) + (planeY * p3.getY()) + (planeZ * p3.getZ());

    }

    @Override
    public String toString() {
        return this.planeX + "x + " + this.planeY + "y + " + this.planeZ + "z = " + this.d;
    }
}
