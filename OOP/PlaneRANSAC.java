/*
 * Nom: Alexandre Ringuette
 * Student number: 300251252
 */
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the Ransac algorithm to find the most dominant plane in a point cloud.
 * @author Alexandre Ringuette
 */
public class PlaneRANSAC {
    
    /******************** Instances variables ********************/

    private double eps;
    private PointCloud pc;
    
    private Plane3D dominantPlane = null; 
    private ArrayList<Plane3D> dominantPlaneList = new ArrayList<>();

    private int bestSupportCount;

    /******************** Contructors ********************/

    public PlaneRANSAC(PointCloud pc){
        this.pc = pc;
    }

    /******************** Class Methods ********************/

    /**
     * Sets the epsilon value.
     * @param eps
     */
    public void setEps(double eps){
        this.eps = eps;
    }

    
    /** 
     * Gets the epsilon value.
     * @return double
     */
    public double getEps(){
        return eps;
    }

    /**Method that returns the estimated number of iterations required to obtain a certain level
    *  of confidence to identify a plane made of a certain percentage of points.
    */
    public int getNumberOfIterations(double confidence, double percentageOfPointsOnPlane){
        return (int)((Math.log(1-confidence))/(Math.log(1-Math.pow(percentageOfPointsOnPlane, 3))));
    }

    /**Method that runs the RANSAC algorithm for identifying the dominant plane of the
    *  point cloud (only one plane).
    */
    public void run(int numberOfIterations, String filename){
        PointCloud planePointCloud;
        Plane3D currentPlane;
        this.bestSupportCount = 0;
        final int INITIAL_POINT_AMOUNT = 3;
        for(int i = 0; i < numberOfIterations; i++){
            List<Point3D> initalPoints = new ArrayList<>();
            for(int j = 0; j< INITIAL_POINT_AMOUNT; j++){
                Point3D pt = this.pc.getPoint();
                initalPoints.add(pt);
            }
            currentPlane = new Plane3D(initalPoints.get(0), 
                                       initalPoints.get(1), 
                                       initalPoints.get(2));

            planePointCloud = currentPlane.getPlanePointCloud();
            
            //find all points on current plane
            int currentSupportCount = 0;
            for(Point3D pt : this.pc.getPointList()){
                double distance = currentPlane.getDistance(pt);
                if(distance<this.getEps()) {
                    planePointCloud.addPoint(pt);
                    currentSupportCount++;
                }
            }
            if(currentSupportCount>this.bestSupportCount){
                this.bestSupportCount = currentSupportCount;
                this.dominantPlane = currentPlane;
                dominantPlaneList.add(currentPlane);
                currentPlane = null;
                planePointCloud = null;
                
            }
        }
        
        for (Point3D pt : dominantPlane.getPlanePointCloud().getPointList()) {
            this.pc.remove(pt);
        }

        System.out.println(this.dominantPlane);
        dominantPlane.getPlanePointCloud().save(filename);
    }

    

    public static void main(String[] args) {

        double confidence = 0.99;
        double percentageOfPointsOnPlane = 0.1;
        double eps = 0.1;
        String FILENAME = "PointCloud1.xyz";
        String shortFileName = FILENAME.substring(0, FILENAME.length()-4); //removes the .xyz file extension
        
        long startTime = System.nanoTime();
        PlaneRANSAC ransac = new PlaneRANSAC(new PointCloud(FILENAME));
        ransac.setEps(eps);
        for(int i = 1; i<=3; i++){
            ransac.run(ransac.getNumberOfIterations(confidence, percentageOfPointsOnPlane), shortFileName+ "_p"+i+".xyz");
        }
        long endTime = System.nanoTime();
        System.out.println(((endTime-startTime)/1000000)+"ms");

    }

}
