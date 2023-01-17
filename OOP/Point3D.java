/*
 * Nom: Alexandre Ringuette
 * Student number: 300251252
 */

import java.lang.Math;
import java.util.HashMap;

/**
 * This class defines a 3d point using x, y, z coordinates. 
 * The distance between two points can be calculated using the method {@code distance()}.
 * @author Alexandre Ringuette 
 */
public class Point3D {

    /********************Instances variables  ******************/
    
    //Point Coords
    private double x, y, z;

    //Cluster label
    private int clusterLabel;





    /******************** Contructor ******************/

    /**
     * Creates a new point and sets cluster label to -1 indicating its undefined.
     * 
     * @param x
     * @param y
     * @param z
     */
    public Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
        
        //initialise cluster label -1 to indicate undefined
        this.clusterLabel = -1;

        //initialise RGB to 0,0,0 as default values

    }

    /******************** Class Methods  ******************/
    
    /** 
     * Computes the Euclidean distance betweeen two points.
     * @param pt
     * @return double
     */
    public double distance(Point3D pt){

        //Points value of point 1
        double otherX = this.getX();
        double otherY = this.getY();
        double otherZ = this.getZ();

        //Point values of point 2
        double x = pt.getX();
        double y = pt.getY();
        double z = pt.getZ();

        //Get the differences between each point
        double diffX = Math.pow((x - otherX), 2);
        double diffY = Math.pow((y - otherY), 2);
        double diffZ = Math.pow((z - otherZ),2);
        
        //calculate the distances between the two points
        double distance = Math.sqrt(diffX + diffY + diffZ);

        return distance;
    }

    public Point3D calculatePointVector(Point3D pt){
        
        double x = pt.getX() - this.getX();
        double y = pt.getY() - this.getY();
        double z = pt.getZ() - this.getZ();
        
        return new Point3D(x, y, z);

    }

    public HashMap<String, Double> vectorCrossProduct(Point3D pt){
        // a x b = i(a2b3 - a3b2) + j(a3b1 - a1b3) + k(a1b2 - a2b1) = N
        HashMap<String, Double> result = new HashMap<>();
        double a1 = this.getX();
        double a2 = this.getY();
        double a3 = this.getZ();
        
        double b1 = pt.getX();
        double b2 = pt.getY();
        double b3 = pt.getZ();
        
        
        double i = (a2*b3 - a3*b2);
        double j = (a3*b1 - a1*b3);
        double k = (a1*b2 - a2*b1);
        
        result.put("i", i);
        result.put("j", j);
        result.put("k", k);
        
        return result;
    }


    /*********** SETTERS & GETTERS  **************/

    /**
     * Returns x value of a {@link Point3D}.
     * @return
     */
    public double getX() {
        return this.x;
    }
    
    
    /** 
     * Returns y value of a {@link Point3D}.
     * 
     * @return double
     */
    public double getY() {
        return this.y;
    }

    
    /** 
     * Returns z value of a {@link Point3D}.
     * 
     * 
     * @return double
     */
    public double getZ() {
        return this.z;
    }

    
    
    /** 
     * Returns the value of clusterLabel.
     * @return int
     */
    public int getClusterLabel(){
        return this.clusterLabel;
    }

    
    /** 
     * Set the value of clusterLabel.
     * @param clusterLabel
     */
    public void setClusterlabel(int clusterLabel){
        this.clusterLabel = clusterLabel;
    }
    
    
    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return getX() + ", " + getY() + ", " + getZ() + ", " + getClusterLabel();
    }
    
}