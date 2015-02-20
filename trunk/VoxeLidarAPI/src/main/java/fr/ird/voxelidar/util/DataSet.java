/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * DataSet tools
 * @author Julien
 */
public class DataSet {
    
    /**
     * Merge two maps between them by additionate columns values
     * @param dataSets list of datasets to merge
     * @param toMerge Array of the same size as dataset keys number to describe which columns are merged
     * @return
     */
    
    public static Map<String, Float[]> mergeMultipleDataSet(ArrayList<Map<String, Float[]>> dataSets, boolean[] toMerge){
        
        
        Map<String, Float[]> mergedDataset;
        if(!dataSets.isEmpty() ){
            mergedDataset = dataSets.get(0);
        }else{
            return null;
        }
                
        for(int i=1;i<dataSets.size();i++){
            
            mergedDataset = mergeTwoDataSet(dataSets.get(i), mergedDataset, toMerge);
        }
        
        return mergedDataset;
    }
    
    public static Map<String, Float[]> mergeTwoDataSet(Map<String, Float[]> dataSet1, Map<String, Float[]> dataSet2, boolean[] toMerge){
        
        Map<String, Float[]> result = new LinkedHashMap<>();
        
        int count = 0;
        
        for(Entry entry:dataSet1.entrySet()){
            
            String key = entry.getKey().toString();
            
            Float[] values1 = (Float[]) entry.getValue();
            Float[] values2 = dataSet2.get(key);
            
            int maxSize = values1.length > values2.length ? values1.length : values2.length;
            
            Float[] resultValues = new Float[maxSize];
            
            if(toMerge[count]){
                
                for(int i=0;i<maxSize;i++){
                    
                    if(i < values1.length && i < values2.length){
                        resultValues[i] = values1[i] + values2[i];
                    }else if(i >= values1.length){
                        resultValues[i] = values2[i];
                    }else{
                        resultValues[i] = values1[i];
                    }
                }
                
                result.put(key, resultValues);
                
            }else{
                result.put(key, values1.length > values2.length ? values1 : values2);
            }
            
            
            count++;
        }
        
        return result;
    }
}
