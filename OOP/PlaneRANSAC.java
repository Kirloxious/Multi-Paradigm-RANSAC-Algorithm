

public class PlaneRANSAC {
    public PlaneRANSAC(PointCloud pc){

    }

    public void setEps(double eps){

    }

    public double getEps(){
        return 0;
    }

    public int getNumberOfIterations(double confidence, double percentageOfPointsOnPlane){
        return 0;
    }

    public void run(int numberOfIterations, String filename){

    }

    public static void main(String[] args) {
        Point3D A = new Point3D(3, 1, 1);
        Point3D B = new Point3D(1, 4, 2);
        Point3D C = new Point3D(1, 3, 4);

        Plane3D plane = new Plane3D(A, B, C);
        System.out.println(plane);
    }

}
