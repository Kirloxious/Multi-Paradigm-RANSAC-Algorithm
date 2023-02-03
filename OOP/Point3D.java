/*
 * Nom: Alexandre Ringuette
 * Student number: 300251252
 */

import java.lang.Math;


/**
 * This class defines a 3d point using x, y, z coordinates. 
 * The distance between two points can be calculated using the method {@code distance()}.
 * @author Alexandre Ringuette 
 */
public class Point3D {

    /********************Instances variables  ******************/
    
    //Point Coords
    private double x, y, z;


    /******************** Contructor ******************/

    /**
     * Creates a new point.
     * 
     * @param x
     * @param y
     * @param z
     */
    public Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;

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

    /**
     * Calculates the vector between two points.
     * @param pt
     * @return {@code Point3D}
     */
    public Point3D calculatePointVector(Point3D pt){
        
        double x = pt.getX() - this.getX();
        double y = pt.getY() - this.getY();
        double z = pt.getZ() - this.getZ();
        
        return new Point3D(x, y, z);

    }


    /*********** SETTERS & GETTERS  **************/

    /**
     * Returns x value of a {@code Point3D}.
     * @return
     */
    public double getX() {
        return this.x;
    }
    
    
    /** 
     * Returns y value of a {@code Point3D}.
     * 
     * @return double
     */
    public double getY() {
        return this.y;
    }

    
    /** 
     * Returns z value of a {@code Point3D}.
     * 
     * 
     * @return double
     */
    public double getZ() {
        return this.z;
    }
    
    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return getX() + ", " + getY() + ", " + getZ();
    }
    
}