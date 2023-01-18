
public class PlaneRANSAC {

    private double eps;
    private PointCloud pc;


    public PlaneRANSAC(PointCloud pc){
        this.pc = pc;
    }

    public void setEps(double eps){
        this.eps = eps;
    }

    public double getEps(){
        return eps;
    }

    //A method that returns the estimated number of iterations required to obtain a certain level
    //of confidence to identify a plane made of a certain percentage of points
    public int getNumberOfIterations(double confidence, double percentageOfPointsOnPlane){
        return (int)((Math.log(1-confidence))/(Math.log(1-Math.pow(percentageOfPointsOnPlane, 3))));
    }

    //A run method that runs the RANSAC algorithm for identifying the dominant plane of the
    //point cloud (only one plane)
    public void run(int numberOfIterations, String filename){
        PointCloud planePointCloud;
        PointCloud bestPlanePointCloud = null;
        Plane3D dominantPlane = null; 
        Plane3D currentPlane;
        int bestSupportCount = 0;
        Point3D p1, p2, p3;
        for(int i = 0; i < numberOfIterations; i++){
            p1 = this.pc.getPoint();
            p2 = this.pc.getPoint();
            p3 = this.pc.getPoint();
            currentPlane = new Plane3D(p1, p2, p3);
            planePointCloud = new PointCloud();
            planePointCloud.addPoint(p1);
            planePointCloud.addPoint(p2);
            planePointCloud.addPoint(p3);

            int currentSupportCount = 0;
            for(Point3D pt : this.pc.getPointList()){
                double distance = currentPlane.getDistance(pt);
                if(distance<this.getEps()) {
                    planePointCloud.addPoint(pt);
                    currentSupportCount++;
                }
            }
            if(currentSupportCount>bestSupportCount){
                bestSupportCount = currentSupportCount;
                dominantPlane = currentPlane;
                bestPlanePointCloud = planePointCloud;
                currentPlane = null;
                planePointCloud = null;
                
            }
        }
        bestPlanePointCloud.save(filename);
    }

    public static void main(String[] args) {

        double confidence = 0.99;
        double percentageOfPointsOnPlane = 0.1;
        double eps = 0.05;
        
        long startTime = System.nanoTime();
        PlaneRANSAC ransac = new PlaneRANSAC(new PointCloud("PointCloud1.xyz"));
        ransac.setEps(eps);
        for(int i = 1; i<=3; i++){
            ransac.run(ransac.getNumberOfIterations(confidence, percentageOfPointsOnPlane), "PointCloud1_p"+i+".xyz");
        }
        long endTime = System.nanoTime();
        System.out.println(((endTime-startTime)/1000000)+"ms");

    }

}
