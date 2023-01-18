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


public class PointCloud {
    
    private List<Point3D> pointList;
    private Map<Point3D, Integer> indexMap;

    
    //point clound from file
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

    //empty point cloud
    PointCloud(){
        this.pointList = new ArrayList<>();
        this.indexMap = new HashMap<>();
    }


    public void addPoint(Point3D pt){
        this.indexMap.put(pt, pointList.size());
        this.pointList.add(pt);
    }

    public void remove(Point3D pt){
        int index = indexMap.remove(pt);
        int last = pointList.size() - 1;
        Point3D elem = pointList.remove(last);
        if(index != last){
            indexMap.put(elem, index);
            pointList.set(index, elem);
        }
    }

    public Point3D getPoint(){
        Point3D elem = pointList.get((int)(Math.random()* this.size()));
        remove(elem);
        return elem;
    }

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

    public Iterator<Point3D> iterator(){
        return this.pointList.iterator();
    }

    public boolean contains(Point3D pt){
        return this.indexMap.containsKey(pt);
    }

    public int size(){
        return this.pointList.size();
    }

    public List<Point3D> getPointList(){
        return this.pointList;
    }

    public void clear(){
        this.indexMap.clear();
        this.pointList.clear();
    }
}
