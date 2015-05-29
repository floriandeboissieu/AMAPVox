/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.engine3d.object.scene;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import fr.ird.voxelidar.engine3d.buffer.MeshBuffer;
import fr.ird.voxelidar.util.image.ScaleGradient;
import fr.ird.voxelidar.engine3d.object.mesh.Attribut;
import fr.ird.voxelidar.engine3d.loading.mesh.MeshFactory;
import fr.ird.voxelidar.engine3d.loading.shader.Shader;
import fr.ird.voxelidar.io.file.FileManager;
import fr.ird.voxelidar.engine3d.math.vector.Vec3F;
import fr.ird.voxelidar.engine3d.object.mesh.InstancedMesh;
import fr.ird.voxelidar.util.ColorGradient;
import fr.ird.voxelidar.util.CombinedFilter;
import fr.ird.voxelidar.util.CombinedFilters;
import fr.ird.voxelidar.util.Filter;
import fr.ird.voxelidar.util.Settings;
import fr.ird.voxelidar.util.StandardDeviation;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.event.EventListenerList;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import org.apache.log4j.Logger;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public class VoxelSpace extends SceneObject{
    
    public final Logger logger = Logger.getLogger(VoxelSpace.class);
    
    public static final int FLOAT_SIZE = Buffers.SIZEOF_FLOAT;
    public static final int INT_SIZE = Buffers.SIZEOF_INT;
    public static final int SHORT_SIZE = Buffers.SIZEOF_SHORT;
    
    public static final int VOXELSPACE_FORMAT1 = 1;
    public static final int VOXELSPACE_FORMAT2 = 2;

    
    
    public enum Format{
        VOXELSPACE_FORMAT2(2);
        
        private final int format;
        Format(int format){
            this.format = format;
        }
    }
    
    private float cubeSize;
    private String currentAttribut;
    public float widthX, widthY, widthZ;
    private boolean fileLoaded;
    public float attributValueMax;
    public float attributValueMin;
    public float attributValueMaxClipped;
    public float attributValueMinClipped;
    private boolean useClippedRangeValue;
    public float min;
    public float max;
    
    private File voxelsFile;
    
    public boolean arrayLoaded;
    private Settings settings;
    private Map<String,Attribut> mapAttributs;
    private Set<String> variables;
    
    public float centerX;
    public float centerY;
    public float centerZ;
    
    private Color[] gradient = ColorGradient.GRADIENT_HEAT;
    private ColorGradient colorGradient;
    
    private boolean gradientUpdated;
    private boolean cubeSizeUpdated;
    private boolean instancesUpdated;
    
    
    private boolean stretched;
    
    public VoxelSpaceData data;
    
    //private Set<Filter> filteredValues;
    private CombinedFilters combinedFilters;
    private boolean displayValues;
    
    private final EventListenerList listeners;
    float sdValue;
    float average;
    
    public VoxelSpace(){
        
        data = new VoxelSpaceData();
        //filteredValues = new TreeSet<>();
        //filteredValues.add(new Filter("x", Float.NaN, Filter.EQUAL));
        //filteredValues.add(new Filter("x", 0.0f, Filter.EQUAL));
        combinedFilters = new CombinedFilters();
        combinedFilters.addFilter(new CombinedFilter(new Filter("x", Float.NaN, Filter.EQUAL), null, CombinedFilter.AND));
        combinedFilters.addFilter(new CombinedFilter(new Filter("x", 0.0f, Filter.EQUAL), null, CombinedFilter.AND));
        mapAttributs = new LinkedHashMap<>();
        variables = new TreeSet<>();
        listeners = new EventListenerList();
        fileLoaded = false;
    }
    
    public VoxelSpace(File voxelSpace){
        
        data = new VoxelSpaceData();
        //filteredValues = new TreeSet<>();
        //filteredValues.add(new Filter("x", Float.NaN, Filter.EQUAL));
        //filteredValues.add(new Filter("x", 0.0f, Filter.EQUAL));
        combinedFilters = new CombinedFilters();
        combinedFilters.addFilter(new CombinedFilter(new Filter("x", Float.NaN, Filter.EQUAL), null, CombinedFilter.AND));
        combinedFilters.addFilter(new CombinedFilter(new Filter("x", 0.0f, Filter.EQUAL), null, CombinedFilter.AND));
        mapAttributs = new LinkedHashMap<>();
        variables = new TreeSet<>();
        listeners = new EventListenerList();
        fileLoaded = false;
        
        this.voxelsFile = voxelSpace;
    }

    public void setMapAttributs(Map<String, Attribut> mapAttributs) {
        this.mapAttributs = mapAttributs;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }

    public float getCubeSize() {
        return cubeSize;
    }
    
    public void addExtendedMapAttributs(Map<String,Attribut> extendedMapAttributs){
        for(Entry entry : extendedMapAttributs.entrySet()){
            
            Attribut a = (Attribut) entry.getValue();
            this.variables.add(a.getName());
            addAttribut(a.getName(), a.getExpressionString());
        }
    }

    public void setCurrentAttribut(String attributToVisualize) {
        this.currentAttribut = attributToVisualize;
    }
    
    public void load(){
        loadFromFile(voxelsFile);
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
        
    }

    public Map<String, Attribut> getMapAttributs() {
        return mapAttributs;
    }    
    
    public final void addAttribut(String name, String expression){
        
        if(!mapAttributs.containsKey(name)){            
            this.variables.add(name);
            mapAttributs.put(name, new Attribut(name, expression, variables));
        }
    }
    
    public void setReadFileProgress(int progress) {
        fireReadFileProgress(progress);
    }
    
    public void fireReadFileProgress(int progress){
        
        for(VoxelSpaceListener listener :listeners.getListeners(VoxelSpaceListener.class)){
            
            listener.voxelSpaceCreationProgress(progress);
        }
    }

    public void setFileLoaded(boolean fileLoaded) {
        this.fileLoaded = fileLoaded;
        
        if(fileLoaded){
            firefileLoaded();
        }
    }
    
    public void setStretched(boolean stretched){
        this.stretched = stretched;
    }

    public boolean isFileLoaded() {
        return fileLoaded;
    }

    public Color[] getGradient() {
        return gradient;
    }
    
    public void firefileLoaded(){
        
        for(VoxelSpaceListener listener :listeners.getListeners(VoxelSpaceListener.class)){
            
            listener.voxelSpaceCreationFinished();
        }
    }
    
    public void addVoxelSpaceListener(VoxelSpaceListener listener){
        listeners.add(VoxelSpaceListener.class, listener);
    }
    
    @Override
    public int getShaderId() {
        return shaderId;
    }
    
    public File file;

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getCenterZ() {
        return centerZ;
    }

    public boolean isGradientUpdated() {
        return gradientUpdated;
    }

    public void changeCurrentAttribut(String attributToVisualize) {
        
        this.currentAttribut = attributToVisualize;
        updateValue();
    }
    
    public void setFilterValues(Set<CombinedFilter> values, boolean display){
        combinedFilters = new CombinedFilters();
        combinedFilters.setFilters(values);
        this.displayValues = display;
    }
    
    private void setWidth(){
        
        if(data.voxels.size() > 0){
            
            widthX = ((VoxelObject)data.getLastVoxel()).position.x - ((VoxelObject)data.getFirstVoxel()).position.x;
            widthY = ((VoxelObject)data.getLastVoxel()).position.y - ((VoxelObject)data.getFirstVoxel()).position.y;
            widthZ = ((VoxelObject)data.getLastVoxel()).position.z - ((VoxelObject)data.getFirstVoxel()).position.z;
        }
    }
    
    private void setCenter(){
        
        if(data.voxels.size() > 0){
            
            VoxelObject firstVoxel = (VoxelObject) data.getFirstVoxel();
            VoxelObject lastVoxel = (VoxelObject) data.getLastVoxel();
            
            centerX = (firstVoxel.position.x + lastVoxel.position.x)/2.0f;
            centerY = (firstVoxel.position.y + lastVoxel.position.y)/2.0f;
            centerZ = (firstVoxel.position.z + lastVoxel.position.z)/2.0f;
        }
        
    }
    
    private void readVoxelFormat(File f){
        
        String header = FileManager.readHeader(f.getAbsolutePath());
        
        if(header.equals("VOXEL SPACE")){
            
            readVoxelFormat1(f);
            
        }else if(header.split(" ").length == 10){
            
            //readVoxelFormat2(f);
        }
    }
    
    public void initAttributs(List<String> columnsNames){
        
        for(String name : columnsNames){
            variables.add(name);
        }
        
        for(String name : columnsNames){
            mapAttributs.put(name, new Attribut(name, name, variables));
        }
    }
    
    
    
    
    private void readVoxelFormat1(File f){
        
        String header = FileManager.readHeader(f.getAbsolutePath());
        
        
        if(header.equals("VOXEL SPACE")){
            
            data = new VoxelSpaceData();
            data.header = VoxelSpaceHeader.readVoxelFileHeader(f);
            initAttributs(data.header.attributsNames);
            
            int count = FileManager.getLineNumber(file.getAbsolutePath());
            
            /******read file*****/

            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                
                //Map<String, Point2F> minMax = new HashMap<>();
                
                //header
                FileManager.skipLines(reader, 6);
                
                int lineNumber = 0;
                String line;                
                
                //start reading voxels
                while ((line = reader.readLine())!= null) {

                    String[] voxelLine = line.split(" ");
                    
                    
                    Point3i indice = new Point3i(Integer.valueOf(voxelLine[0]), 
                            Integer.valueOf(voxelLine[1]),
                            Integer.valueOf(voxelLine[2]));

                    float[] mapAttrs = new float[data.header.attributsNames.size()];

                    for (int i=0;i<voxelLine.length;i++) {
                        
                        float value = Float.valueOf(voxelLine[i]);
                        
                        mapAttrs[i] = value;
                        
                        //Point2F minMaxPoint;
                        /*
                        if((minMaxPoint = minMax.get(data.header.attributsNames.get(i)))!=null){
                            
                            float min = minMaxPoint.x;
                            float max = minMaxPoint.y;
                            
                            if(value < min){
                                min = value;
                            }
                            
                            if(value > max){
                                max = value;
                            }
                            
                            minMaxPoint = new Point2F(min, max);
                            minMax.put(data.header.attributsNames.get(i), minMaxPoint);
                            
                        }else{
                            minMax.put(data.header.attributsNames.get(i), new Point2F(value, value));
                        }*/
                    }
                    
                    Point3f position = new Point3f((float) (data.header.bottomCorner.x+(indice.x*(data.header.resolution.x))),
                                                    (float) (data.header.bottomCorner.y+(indice.y*(data.header.resolution.y))),
                                                    (float) (data.header.bottomCorner.z+(indice.z*(data.header.resolution.z))));
                    
                    if(lineNumber == 0){
                        data.minY = position.y;
                        data.maxY = position.y;
                    }else{
                        if(data.minY > position.y){
                            data.minY = position.y;
                        }

                        if(data.maxY < position.y){
                            data.maxY = position.y;
                        }
                    }                    
                    
                    data.voxels.add(new VoxelObject(indice, position, mapAttrs, 1.0f));

                    lineNumber++;

                    setReadFileProgress((lineNumber * 100) / count);
                }
                
                //data.calculateAttributsLimits();
                
                reader.close();

            } catch (FileNotFoundException ex) {
                logger.error(null, ex);
            } catch (IOException ex) {
                logger.error(null, ex);
            }
            
        }
    }
    
//    private void readVoxelFormat2(File f){
//        
//        String header = FileManager.readHeader(f.getAbsolutePath());
//        
//        if(header.split(" ").length == 10){
//            
//            data = new VoxelSpaceData();
//            
//            int count = FileManager.getLineNumber(file.getAbsolutePath());
//
//            /******read file*****/
//
//            BufferedReader reader;
//            try {
//                reader = new BufferedReader(new FileReader(file));
//                
//                Map<String, Point2F> minMax = new HashMap<>();
//                
//                String[] columnsNames = reader.readLine().split(" ");
//                
//                String[] infos = reader.readLine().split(" ");
//                data.split = new Point3i(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2]));
//                //data.resolution = Float.valueOf(infos[3]);
//                
//                int lineNumber = 0;
//                String line;                
//                
//                //start reading voxels
//                while ((line = reader.readLine())!= null) {
//
//                    String[] voxel = line.split(" ");
//                    
//                    int indiceX = Integer.valueOf(voxel[0]);
//                    int indiceZ = Integer.valueOf(voxel[1]);
//                    int indiceY = Integer.valueOf(voxel[2]);
//
//                    Map<String,Float> mapAttributs = new HashMap<>();
//
//                    for (int i=0;i<voxel.length;i++) {
//                        
//                        float value = Float.valueOf(voxel[i]);
//                        
//                        mapAttributs.put(columnsNames[i], value);
//                        
//                        Point2F minMaxPoint;
//                        
//                        if((minMaxPoint = minMax.get(columnsNames[i]))!=null){
//                            
//                            float min = minMaxPoint.x;
//                            float max = minMaxPoint.y;
//                            
//                            if(value < min){
//                                min = value;
//                            }
//                            
//                            if(value > max){
//                                max = value;
//                            }
//                            
//                            minMaxPoint = new Point2F(min, max);
//                            minMax.put(columnsNames[i], minMaxPoint);
//                            
//                        }else{
//                            minMax.put(columnsNames[i], new Point2F(value, value));
//                        }
//                    }
//                    /*
//                    float offsetX = -12;
//                    float offsetY = -2;
//                    float offsetZ = 8;
//                    */
//                    float offsetX = 0;
//                    float offsetY = 0;
//                    float offsetZ = 0;
//                    
//                    float posX = offsetX+(indiceX)*(resolution.x);
//                    float posY = offsetZ+(indiceY)*(resolution.y);
//                    float posZ = offsetY+(indiceZ)*(resolution.z);
//
//                    data.voxels.add(new Voxel(indiceX, indiceY, indiceZ, posX, posY, posZ, mapAttributs, 1.0f));
//
//                    lineNumber++;
//
//                    setReadFileProgress((lineNumber * 100) / count);
//                }
//                
//                data.setMinMax(minMax);
//                
//                reader.close();
//
//            } catch (FileNotFoundException ex) {
//                logger.error(null, ex);
//            } catch (IOException ex) {
//                logger.error(null, ex);
//            }
//            
//        }
//    }
    
    public final void loadFromFile(File f){
        
        setFileLoaded(false);
        
        //attribut is a custom equation defined by user
        //this.mapAttributs = mapAttributs;
        
        this.file =f;
        
        readVoxelFormat(file);


        //updateValue();

        setCenter();
        setWidth();

        setFileLoaded(true);
    }
    
    public void setAttributValueRange(float minClipped, float maxClipped){
        
        useClippedRangeValue = true;
        attributValueMinClipped = minClipped;
        attributValueMaxClipped = maxClipped;
    }
    
    public void resetAttributValueRange(){
        useClippedRangeValue = false;
    }

    public boolean isUseClippedRangeValue() {
        return useClippedRangeValue;
    }
    
    /*
    public final void loadFromFile(File f){
        
            
        setFileLoaded(false);

        this.file =f;

        SwingWorker sw = new SwingWorker() {


            @Override
            protected Object doInBackground() {

                readVoxelFormat(file);

                setFileLoaded(true);

                setCenter();
                setWidth();
                

                return null;

            }
        };

        sw.execute();   
    }
    */
    
    public void updateValue(){
        
        if(currentAttribut == null){
            currentAttribut = mapAttributs.entrySet().iterator().next().getKey();
        }
        
        Attribut attribut = mapAttributs.get(currentAttribut);
        
        //float[] values = new float[data.voxels.size()];
        
        int count = 0;
        boolean minMaxInit = false;
        
        StandardDeviation sd = new StandardDeviation();
        
        for(VoxelObject voxel:data.voxels){
            
            
            float attributValue;
            
            float[] attributs = voxel.getAttributs();
            
            for(int i=0; i< attributs.length;i++){
                
                String name = data.header.attributsNames.get(i);
                double value = attributs[i];
                attribut.getExpression().setVariable(name, value);
            }

             try{
                attributValue = (float) attribut.getExpression().evaluate();
            }catch(Exception e){
                attributValue = 0;
            }

            voxel.attributValue = attributValue;
            //voxel.color = getColorFromValue(attributValue);

            if (!Float.isNaN(attributValue)){
                
                if(!minMaxInit){

                    attributValueMax = attributValue;
                    attributValueMin = attributValue;

                    minMaxInit = true;
                }else{

                    //set maximum attribut value
                    if(attributValue>attributValueMax){

                        attributValueMax = attributValue;
                    }

                    //set minimum attribut value
                    if(attributValue < attributValueMin){

                        attributValueMin = attributValue;
                    }
                }
            }
            

            
            voxel.setAlpha(255);
            
            //values[count] = voxel.attributValue;
            
            if(stretched){
                if(useClippedRangeValue){
                    if(voxel.attributValue < attributValueMinClipped){
                        sd.addValue(attributValueMinClipped);
                    }else if(voxel.attributValue > attributValueMaxClipped){
                        sd.addValue(attributValueMaxClipped);
                    }else{
                        sd.addValue(voxel.attributValue); 
                   }
                }else{
                    sd.addValue(voxel.attributValue);
                }
            }
            
            count++;
            
        }
        
        //calculate standard deviation
        
        if(stretched){
            sdValue = sd.getStandardDeviation();
            average = sd.getAverage();
            
            min = average-(2*sdValue);
            max = average+(2*sdValue);
            
            if(useClippedRangeValue){
                if(min < attributValueMinClipped){
                    min = attributValueMinClipped;
                }

                if(max > attributValueMaxClipped){
                    max = attributValueMaxClipped;
                }
            }else{
                if(min < attributValueMin){
                    min = attributValueMin;
                }

                if(max > attributValueMax){
                    max = attributValueMax;
                }
            }

            setGradientColor(gradient, min, max);
            
        }else{
            if(useClippedRangeValue){
                setGradientColor(gradient, attributValueMinClipped, attributValueMaxClipped);
            }else{
                setGradientColor(gradient, attributValueMin, attributValueMax);
            }
        }
        
    }

    public boolean isStretched() {
        return stretched;
    }
    
    
    public void updateColorValue(Color[] gradient){
        if(stretched){
            setGradientColor(gradient, min, max);
        }else{
            if(useClippedRangeValue){
                setGradientColor(gradient, attributValueMinClipped, attributValueMaxClipped);
            }else{
                setGradientColor(gradient, attributValueMin, attributValueMax);
            }
            
        }
        
    }
    
    public Vec3F getColorFromValue(float value){
        
        Color c = colorGradient.getColor(value);
        
        return new Vec3F(c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f);
    }
    
    
    public void setGradientColor(Color[] gradientColor, float valMin, float valMax){
        
        this.gradient = gradientColor;
        
        ColorGradient color = new ColorGradient(valMin, valMax);
        color.setGradientColor(gradientColor);
        //ArrayList<Float> values = new ArrayList<>();
        for (VoxelObject voxel : data.voxels) {
            
            //float ratio = voxel.attributValue/(attributValueMax-attributValueMin);
            //float value = valMin+ratio*(valMax-valMin);
            //Color colorGenerated = color.getColor(value);
            Color colorGenerated = color.getColor(voxel.attributValue);
            
            voxel.setColor(colorGenerated.getRed(), colorGenerated.getGreen(), colorGenerated.getBlue());
            //values.add(voxel.attributValue);
            
            boolean isFiltered = combinedFilters.doFilter(voxel.attributValue);
            
            if(isFiltered && displayValues){
                voxel.setAlpha(1);
            }else if(isFiltered && !displayValues){
                voxel.setAlpha(0);
            }else if(!isFiltered && displayValues){
                voxel.setAlpha(0);
            }else{
                voxel.setAlpha(1);
            }
        }
        //System.out.println("test");
        //voxelList = ImageEqualisation.scaleHistogramm(voxelList);
        //voxelList = ImageEqualisation.voxelSpaceFormatEqualisation(voxelList);
        
        
    }
    
    
    public void updateInstanceColorBuffer(){
        
        gradientUpdated = false;
        
    }
    /*
    public BufferedImage createScaleImage(int width, int height){
        
        if(stretched){
            return ScaleGradient.generateScale(gradient, min, max, width, height, ScaleGradient.HORIZONTAL);
        }else{
            if(useClippedRangeValue){
                return ScaleGradient.generateScale(gradient, attributValueMinClipped, attributValueMaxClipped, width, height, ScaleGradient.HORIZONTAL);
            }else{
                return ScaleGradient.generateScale(gradient, attributValueMin, attributValueMax, width, height, ScaleGradient.HORIZONTAL);
            }
        }
        
    }*/
    
    public void updateCubeSize(GL3 gl, float size){
        
        cubeSize = size;
        cubeSizeUpdated = false;
    }
    
    public void switchLightOn(){
        
    }
    
    public void switchLightOff(){
        
    }

    public boolean isInstancesUpdated() {
        return instancesUpdated;
    }

    public void setInstancesUpdated(boolean instancesUpdated) {
        this.instancesUpdated = instancesUpdated;
    }
    
    @Override
    public void initVao(GL3 gl, Shader shader){
        
        //generate vao
        int[] tmp2 = new int[1];
        gl.glGenVertexArrays(1, tmp2, 0);
        vaoId = tmp2[0];
        
        gl.glBindVertexArray(vaoId);
        
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, buffer.getVboId());

                gl.glEnableVertexAttribArray(shader.attributeMap.get("position"));
                gl.glVertexAttribPointer(shader.attributeMap.get("position"), 3, GL3.GL_FLOAT, false, 0, 0);
                
                gl.glEnableVertexAttribArray(shader.attributeMap.get("instance_position"));
                gl.glVertexAttribPointer(shader.attributeMap.get("instance_position"), 3, GL3.GL_FLOAT, false, 0, mesh.vertexBuffer.capacity()*FLOAT_SIZE);
                gl.glVertexAttribDivisor(shader.attributeMap.get("instance_position"), 1);
                
                gl.glEnableVertexAttribArray(shader.attributeMap.get("instance_color"));
                gl.glVertexAttribPointer(shader.attributeMap.get("instance_color"), 4, GL3.GL_FLOAT, false, 0, (mesh.vertexBuffer.capacity()+((InstancedMesh)mesh).instancePositionsBuffer.capacity())*FLOAT_SIZE);
                gl.glVertexAttribDivisor(shader.attributeMap.get("instance_color"), 1);
                
                //gl.glEnableVertexAttribArray(shader.attributeMap.get("ambient_occlusion"));
                //gl.glVertexAttribPointer(shader.attributeMap.get("ambient_occlusion"), 4, GL3.GL_FLOAT, false, 0, 0);
                 
            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, buffer.getIboId());
            
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
            
        gl.glBindVertexArray(0);
        
        gradientUpdated = true;
    }
    
    public void updateVao(){
        
        List<Float> instancePositionsList = new ArrayList<>();
        List<Float> instanceColorsList = new ArrayList<>();

        for (int i=0;i<data.voxels.size();i++) {
            
            VoxelObject voxel = data.voxels.get(i);

            if(voxel.getAlpha() != 0){
                
                instancePositionsList.add(voxel.position.x);
                instancePositionsList.add(voxel.position.y);
                instancePositionsList.add(voxel.position.z);
                
                instanceColorsList.add(voxel.getRed());
                instanceColorsList.add(voxel.getGreen());
                instanceColorsList.add(voxel.getBlue());
                instanceColorsList.add(voxel.getAlpha());
            }
            
        }
        
        float[] instancePositions = new float[instancePositionsList.size()];
        float[] instanceColors = new float[instanceColorsList.size()];
        
        for(int i=0;i<instancePositionsList.size();i++){
            instancePositions[i] = instancePositionsList.get(i);
        }
        
        for(int i=0;i<instanceColorsList.size();i++){
            instanceColors[i] = instanceColorsList.get(i);
        }
        
        ((InstancedMesh)mesh).instancePositionsBuffer = Buffers.newDirectFloatBuffer(instancePositions);
        ((InstancedMesh)mesh).instanceColorsBuffer = Buffers.newDirectFloatBuffer(instanceColors);
        
        ((InstancedMesh)mesh).instanceNumber = instancePositions.length/3;
        
        instancesUpdated = false;
    }
    
    @Override
    public void initBuffers(GL3 gl){
        
        cubeSize = (float) (data.header.resolution.x/2.0f);
        
        int instanceNumber = data.voxels.size();        
        mesh = new InstancedMesh(MeshFactory.createCube(cubeSize), instanceNumber);
        buffer = new MeshBuffer(gl);
        
        int maxSize = (mesh.vertexBuffer.capacity()*MeshBuffer.FLOAT_SIZE)+(data.voxels.size()*3*MeshBuffer.FLOAT_SIZE)+(data.voxels.size()*4*MeshBuffer.FLOAT_SIZE);
        buffer.initBuffersV2(gl, maxSize, mesh.indexBuffer, mesh.vertexBuffer);
        
        updateVao();
    }
    
    @Override
    public void draw(GL3 gl, int drawType) {
        
        gl.glBindVertexArray(vaoId);
            if(texture != null){
                gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
            }
            
            if(mesh instanceof InstancedMesh){
                gl.glDrawElementsInstanced(drawType, mesh.vertexCount, GL3.GL_UNSIGNED_SHORT, 0, ((InstancedMesh)mesh).instanceNumber);
            }else{
                gl.glDrawElements(drawType, mesh.vertexCount, GL3.GL_UNSIGNED_SHORT, 0);
            }

            if(texture != null){
                gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
            }
        gl.glBindVertexArray(0);
    }
    
    public void render(GL3 gl, Shader shader){
        
        if(!instancesUpdated){
            
            buffer.updateBuffer(gl, 1, ((InstancedMesh)mesh).instancePositionsBuffer);
            buffer.updateBuffer(gl, 2, ((InstancedMesh)mesh).instanceColorsBuffer);
            
            gl.glBindVertexArray(vaoId);
        
                gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, buffer.getVboId());

                    gl.glEnableVertexAttribArray(shader.attributeMap.get("position"));
                    gl.glVertexAttribPointer(shader.attributeMap.get("position"), 3, GL3.GL_FLOAT, false, 0, 0);

                    gl.glEnableVertexAttribArray(shader.attributeMap.get("instance_position"));
                    gl.glVertexAttribPointer(shader.attributeMap.get("instance_position"), 3, GL3.GL_FLOAT, false, 0, mesh.vertexBuffer.capacity()*FLOAT_SIZE);
                    gl.glVertexAttribDivisor(shader.attributeMap.get("instance_position"), 1);

                    gl.glEnableVertexAttribArray(shader.attributeMap.get("instance_color"));
                    gl.glVertexAttribPointer(shader.attributeMap.get("instance_color"), 4, GL3.GL_FLOAT, false, 0, (mesh.vertexBuffer.capacity()+((InstancedMesh)mesh).instancePositionsBuffer.capacity())*FLOAT_SIZE);
                    gl.glVertexAttribDivisor(shader.attributeMap.get("instance_color"), 1);

                gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, buffer.getIboId());

                gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

            gl.glBindVertexArray(0);
            
            instancesUpdated = true;
        }
        
        if(!gradientUpdated){
            
            float instanceColors[] = new float[data.voxels.size()*4];
            
            int count0 = 0;
            
            for (int i=0, j=0;i<data.voxels.size();i++, j+=4) {
                
                VoxelObject voxel = (VoxelObject) data.voxels.get(i);

                if(voxel.getAlpha() != 0){
                
                    instanceColors[count0] = voxel.getRed();
                    instanceColors[count0+1] = voxel.getGreen();
                    instanceColors[count0+2] = voxel.getBlue();
                    instanceColors[count0+3] = voxel.getAlpha();
                    count0 += 4;
                }
            }

            ((InstancedMesh)mesh).instanceColorsBuffer = Buffers.newDirectFloatBuffer(instanceColors, 0, count0);
            
            buffer.updateBuffer(gl, 2, ((InstancedMesh)mesh).instanceColorsBuffer);
            
            gradientUpdated = true;
        }
        
        if(!cubeSizeUpdated){
            
            int instanceNumber = ((InstancedMesh)mesh).instanceNumber;
            mesh = new InstancedMesh(MeshFactory.createCube(cubeSize), instanceNumber);
            
            buffer.updateBuffer(gl, 0, mesh.vertexBuffer);
            
            cubeSizeUpdated = true;
        }
        //gl.glDisable(GL3.GL_DEPTH_TEST);
        draw(gl, GL3.GL_TRIANGLES);
        //gl.glEnable(GL3.GL_DEPTH_TEST);
    }
}
