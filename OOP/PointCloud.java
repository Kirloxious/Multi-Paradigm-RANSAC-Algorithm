/*
 * Nom: Alexandre Ringuette
 * Student number: 300251252
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class defines the point cloud structure. 
 * It uses an Arraylist to store the points & a hashmap to store the index of the points in the array
 * @author Alexandre Ringuette
 */
public class PointCloud {
    
    /******************** Instances variables ********************/

    private List<Point3D> pointList;
    private Map<Point3D, Integer> indexMap;

    
    /******************** Contructors ********************/

    /**
     * Creates a point clound from a file input.
     * @param filename
     */
    PointCloud(String filename){
        this.pointList = new ArrayList<>();
        this.indexMap = new HashMap<>();
        
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line = "";

            String splitBy = "\t";
            br.readLine(); // skip first x y z line
            while((line = br.readLine()) != null){
                String[] points = line.split(splitBy);
            

                Point3D point = new Point3D(Double.parseDouble(points[0]), 
                                            Double.parseDouble(points[1]), 
                                            Double.parseDouble(points[2]));

                this.addPoint(point);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("File not found.");
            e.printStackTrace();
        }
        catch(IOException e){
            System.out.println("I/O error");
            e.printStackTrace();
        }
    }
    
    /**
     * Creates an empty point cloud.
     */
    PointCloud(){
        this.pointList = new ArrayList<>();
        this.indexMap = new HashMap<>();
    }
    
    /******************** Class Methods ********************/
    
    /** 
     * Adds a point to the point cloud. The point is added to an Arraylist and its index is stored in a hashmap.
     * @param pt
     */
    public void addPoint(Point3D pt){
        this.indexMap.put(pt, pointList.size());
        this.pointList.add(pt);
    }
    
    /** 
     * Removes a point from the point cloud.
     * @param pt
     */
    public void remove(Point3D pt){
        if(this.contains(pt)){
            int index = indexMap.remove(pt);
            int last = pointList.size() - 1;
            Point3D elem = pointList.remove(last);
            if(index != last){
                indexMap.put(elem, index);
                pointList.set(index, elem);
            }
        }
    }
    
    /** 
     * Gets a random point from the point cloud.
     * @return Point3D
     */
    public Point3D getPoint(){
        Point3D elem = pointList.get((int)(Math.random()* this.size()));
        return elem;
    }
    
    /** 
     * Saves the point cloud to a new .xyz file.
     * @param filename
     */
    public void save(String filename){
        try (PrintWriter writer = new PrintWriter(filename)) {
            final String FILE_HEADER = "x	y	z";
            writer.append(FILE_HEADER);
            
            //write every point to the file
            for(Point3D p: this.pointList){
                writer.append("\n");
                writer.append(p.toString());
            
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found error.");
        }
    }
    
    /** 
     * Returns an iterator of the point cloud.
     * @return Iterator<Point3D>
     */
    public Iterator<Point3D> iterator(){
        return this.pointList.iterator();
    }
    
    /** 
     * Checks if point cloud contains the point.
     * @param pt
     * @return boolean
     */
    public boolean contains(Point3D pt){
        return this.indexMap.containsKey(pt);
    }
    
    /** 
     * Returns the size of the point cloud.
     * @return int
     */
    public int size(){
        return this.pointList.size();
    }
    
    /** 
     * Returns the point cloud.
     * @return List<Point3D>
     */
    public List<Point3D> getPointList(){
        return this.pointList;
    }

    /**
     * Clear the point cloud.
     */
    public void clear(){
        this.indexMap.clear();
        this.pointList.clear();
    }
}
