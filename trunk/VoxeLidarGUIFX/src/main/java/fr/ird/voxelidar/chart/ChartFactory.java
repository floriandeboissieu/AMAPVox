/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.chart;

import fr.ird.voxelidar.engine3d.object.scene.VoxelObject;
import fr.ird.voxelidar.util.VoxelFilter;
import fr.ird.voxelidar.engine3d.object.scene.VoxelSpace;
import fr.ird.voxelidar.util.Processing;
import fr.ird.voxelidar.voxelisation.raytracing.voxel.Voxel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.vecmath.Point2f;
import org.apache.log4j.Logger;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public class ChartFactory extends Processing{
    
    private final Logger logger = Logger.getLogger(ChartFactory.class);
    
    public XYSeries generateChartWithFilters(VoxelSpace voxelSpace, String horizontalAxis, String verticalAxis, VoxelFilter filter){
        
        ArrayList<VoxelObject> voxels = voxelSpace.data.voxels;
        
        XYSeries data = new XYSeries("ALS");
        List<String> attributsNames = voxelSpace.data.header.attributsNames;
        
        int iterationsNumber = voxels.size();
        int step = iterationsNumber/10;
        
        int iterationIndex = 0;
        for (VoxelObject voxel : voxels) {
            
            if(iterationIndex % step == 0){
                fireProgress("", (int)(((iterationIndex/(float)iterationsNumber)*100)));
            }
            
            float[] attributs =  voxel.attributs;
            
            if(filter.doFilter(attributsNames, attributs)){
                
                float horizontal = attributs[attributsNames.indexOf(horizontalAxis)];
                float vertical = attributs[attributsNames.indexOf(verticalAxis)];
                
                data.add(vertical, horizontal);
            }
            
            iterationIndex++;
        }
        
        fireFinished();
        
        return data;
        
    }
    
    public XYSeries generateVegetationProfile(VoxelFilter filter, VoxelSpace voxelSpace){
        
        ArrayList<VoxelObject> voxels = voxelSpace.data.voxels;
        
        Map<String, Point2f> minMax = voxelSpace.data.getMinMax();
        Point2f get = minMax.get("dist");
        
        int distIndex = voxelSpace.data.header.attributsNames.indexOf("dist");
        int nbSamplingIndex = voxelSpace.data.header.attributsNames.indexOf("BFEntering");
        
        float minDist = 0, maxDist=0;
        boolean isRangesInit = false;
        
        for (VoxelObject voxel : voxelSpace.data.voxels) {
            
            float[] attributs = voxel.attributs;

            float dist = attributs[distIndex];
            
            float nbSampling = attributs[nbSamplingIndex];

            if(nbSampling > 0){
                
                if(!isRangesInit){
                    minDist = dist;
                    maxDist = dist;
                    isRangesInit = true;
                }
                
                if(minDist>dist){
                    minDist = dist;
                }
                if(maxDist<dist){
                    maxDist = dist;
                }
            }
        }
        
        float minHeight = 0;
        float maxHeight;
        
        maxHeight = maxDist;
        
        XYSeries data = new XYSeries("ALS");
        
        
        //float minHeight = format.minY;
        //float maxHeight = format.maxY;
        
        float heightIntervall = (maxHeight - minHeight)/20.0f;
        
        //compute heights
        float[] heights = new float[20];
        
        float temp = minHeight;
        for(int i=0;i<heights.length;i++){
            
            if(i == 0){
                heights[i] = temp;
            }else{
                heights[i] = temp + heightIntervall;
            }
            
            temp = heights[i];
        }
        
        int iterationsNumber = heights.length;
        int step = iterationsNumber/10;
        
        float lai = 0;
        
        for(int i=0;i<heights.length-1;i++){
            
            if(i % step == 0){
                fireProgress("", (int)(((i/(float)iterationsNumber)*100)));
            }
            
            float averagePAD = 0;
            int count = 0;
            List<String> attributsNames = voxelSpace.data.header.attributsNames;
            
            for (VoxelObject voxel : voxels) {
                
                
                float[] attributs = voxel.attributs;
                
                float dist = 0;
                try{
                    dist = attributs[attributsNames.indexOf("dist")];
                }catch(Exception e){
                    //dist = voxel.position.y;
                }
                
                if(belongToRange(heights[i], heights[i+1], dist) && filter.doFilter(attributsNames, attributs)){
                    
                    float pad = attributs[attributsNames.indexOf("PAD")];
                    float nbsampling = attributs[attributsNames.indexOf("BFEntering")];
                    
                    if(nbsampling > 0 && !Float.isNaN(pad)){
                        averagePAD += pad;
                        count++;
                    }
                }
            }
            
            averagePAD/=count;
            
            if(!Float.isNaN(averagePAD)){
                data.add(heights[i], averagePAD);
                lai += averagePAD;
            }
        }
        
        logger.info("LAI = "+lai);
        
        fireFinished();
        
        return data;
        
    }
    
    private boolean belongToRange(float min, float max, float value){
        
        return value >= min && value < max; 
    }

    @Override
    public File process() {
        return null;
    }
}
