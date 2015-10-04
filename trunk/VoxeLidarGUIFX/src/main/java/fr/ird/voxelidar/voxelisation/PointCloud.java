
/*
This software is distributed WITHOUT ANY WARRANTY and without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

This program is open-source LGPL 3 (see copying.txt).
Authors:
    Gregoire Vincent    gregoire.vincent@ird.fr
    Julien Heurtebize   julienhtbe@gmail.com
    Jean Dauzat         jean.dauzat@cirad.fr
    Rémi Cresson        cresson.r@gmail.com

For further information, please contact Gregoire Vincent.
 */

package fr.ird.voxelidar.voxelisation;

import fr.ird.voxelidar.engine3d.math.matrix.Mat4D;
import fr.ird.voxelidar.engine3d.math.point.Point3F;
import fr.ird.voxelidar.engine3d.math.vector.Vec4D;
import fr.ird.voxelidar.util.BoundingBox3F;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Julien
 */


public class PointCloud {
    
    private final static Logger logger = Logger.getLogger(PointCloud.class);
    
    public List<Point3F> points;
    private Point3F min;
    private Point3F max;
    
     public PointCloud(){
        points = new ArrayList<>();
    }
    
    public boolean isPointInsidePointCloud(Point3F point, float maxDistance){
        
        int index = nearestPoint(point, maxDistance);
        if(index < 0){
            return false;
        }else{
            return true;
        }
        
    }
    
    public int nearestPoint(Point3F point, float maxDistance){
        int low = 0;
        int high = points.size()-1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Comparable<Point3F> midVal = points.get(mid);
            int cmp = midVal.compareTo(point);

            if (cmp < 0){
                low = mid + 1;
            }else if (cmp > 0){
                high = mid - 1;
            }else{
                return mid; // key found
            }
        }
        
        if((low < points.size()) && (point.distanceTo(points.get(low)) < maxDistance)){
            return low;
        }else if(high >= 0 && (point.distanceTo(points.get(high)) < maxDistance)){
            return high;
        }
        
        return -(low + 1);  // key not found
        //return Collections.binarySearch(points, point);
    }
    
    public void readFromFile(File file, Mat4D transfMatrix){
        
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                points = new ArrayList<>();
                String line;
                while((line = reader.readLine()) != null){
                    
                    String[] split = line.split(",");
                    
                    Vec4D transformedPoint = Mat4D.multiply(transfMatrix, new Vec4D(Float.valueOf(split[0]), Float.valueOf(split[1]), Float.valueOf(split[2]), 1));
                
                    points.add(new Point3F((float) transformedPoint.x, (float) transformedPoint.y, (float) transformedPoint.z));
                }
            
            long startTime = System.currentTimeMillis();
            Collections.sort(points);
            long endTime = System.currentTimeMillis();
            
            if(points != null && points.size()>0){
                min = points.get(0);
                max = points.get(points.size()-1);
            }
            
            System.out.println("temps de tri: "+((endTime-startTime)*Math.pow(10, -3)));
            
        } catch (FileNotFoundException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    
    public BoundingBox3F getBoundingBox(){
        
        float xMin = 0, yMin = 0, zMin = 0;
        float xMax = 0, yMax = 0, zMax = 0;
        
        int count = 0;
        
        for(Point3F point : points){
            
            if(count == 0){
                
                xMin = point.x;
                xMax = point.x;
                yMin = point.y;
                yMax = point.y;
                zMin = point.z;
                zMax = point.z;
                
            }else{
                
                if(point.x < xMin){
                    xMin = point.x;
                }else if(point.x > xMax){
                    xMax = point.x;
                }
                
                if(point.y < yMin){
                    yMin = point.y;
                }else if(point.y > yMax){
                    yMax = point.y;
                }
                
                if(point.z < zMin){
                    zMin = point.z;
                }else if(point.z > zMax){
                    zMax = point.z;
                }
            }
            
            count++;
        }
        
        return new BoundingBox3F(new Point3F(xMin, yMin, zMin), new Point3F(xMax, yMax, zMax));
    }
}
