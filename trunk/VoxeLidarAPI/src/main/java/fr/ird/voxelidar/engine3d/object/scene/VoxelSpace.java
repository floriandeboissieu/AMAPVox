/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.engine3d.object.scene;

import com.jogamp.common.nio.Buffers;
import fr.ird.voxelidar.engine3d.buffer.MeshBuffer;
import fr.ird.voxelidar.util.image.ScaleGradient;
import fr.ird.voxelidar.engine3d.object.mesh.Attribut;
import fr.ird.voxelidar.engine3d.loading.mesh.MeshFactory;
import fr.ird.voxelidar.engine3d.loading.shader.Shader;
import fr.ird.voxelidar.io.file.FileManager;
import fr.ird.voxelidar.engine3d.math.point.Point2F;
import fr.ird.voxelidar.engine3d.math.vector.Vec3F;
import fr.ird.voxelidar.engine3d.object.mesh.InstancedMesh;
import fr.ird.voxelidar.util.ColorGradient;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.media.opengl.GL3;
import javax.swing.SwingWorker;
import javax.swing.event.EventListenerList;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.log4j.Logger;

/**
 *
 * @author Julien
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
    public float min;
    public float max;
    
    private File voxelsFile;
    
    public boolean arrayLoaded = false;
    private Settings settings;
    private Map<String,Attribut> mapAttributs;
    private Set<String> variables;
    
    public float centerX;
    public float centerY;
    public float centerZ;
    
    private Color[] gradient = ColorGradient.GRADIENT_HEAT;
    private ColorGradient colorGradient;
    
    private boolean gradientUpdated = false;
    private boolean cubeSizeUpdated;
    
    public VoxelSpaceData data;
    
    private final EventListenerList listeners;
    
    public VoxelSpace(){
        
        data = new VoxelSpaceData();
        mapAttributs = new HashMap<>();
        variables = new TreeSet<>();
        listeners = new EventListenerList();
        fileLoaded = false;
    }
    
    public VoxelSpace(File voxelSpace){
        
        data = new VoxelSpaceData();
        mapAttributs = new HashMap<>();
        variables = new TreeSet<>();
        listeners = new EventListenerList();
        fileLoaded = false;
        
        this.voxelsFile = voxelSpace;
    }
    
    public VoxelSpace(File voxelSpace, String attributToVisualize){
        
        data = new VoxelSpaceData();
        mapAttributs = new HashMap<>();
        variables = new TreeSet<>();
        listeners = new EventListenerList();
        fileLoaded = false;
        
        this.currentAttribut = attributToVisualize;
        
        this.voxelsFile = voxelSpace;
        
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

    public void setGradientUpdated(boolean gradientUpdated) {
        this.gradientUpdated = gradientUpdated;
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
    
    
    private void setWidth(){
        
        if(data.voxels.size() > 0){
            
            widthX = (data.voxels.get(data.voxels.size()-1).position.x) - (data.voxels.get(0).position.x);
            widthY = (data.voxels.get(data.voxels.size()-1).position.y) - (data.voxels.get(0).position.y);
            widthZ = (data.voxels.get(data.voxels.size()-1).position.z) - (data.voxels.get(0).position.z);
        }
    }
    
    private void setCenter(){
        
        if(data.voxels.size() > 0){
            
            Voxel firstVoxel = data.voxels.get(0);
            Voxel lastVoxel = data.voxels.get(data.voxels.size()-1);
            
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
    
    private void initAttributs(String[] columnsNames){
        
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
            
            int count = FileManager.getLineNumber(file.getAbsolutePath());

            /******read file*****/

            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                
                Map<String, Point2F> minMax = new HashMap<>();
                
                //header
                reader.readLine();
                
                
                String[] minC = reader.readLine().split(" ");
                data.bottomCorner.x =  Double.valueOf(minC[1]);
                data.bottomCorner.y =  Double.valueOf(minC[2]);
                data.bottomCorner.z =  Double.valueOf(minC[3]);
                
                String[] maxC = reader.readLine().split(" ");
                data.topCorner.x =  Double.valueOf(maxC[1]);
                data.topCorner.y =  Double.valueOf(maxC[2]);
                data.topCorner.z =  Double.valueOf(maxC[3]);
                
                String[] split = reader.readLine().split(" ");
                
                data.split = new Point3i(Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3]));
                
                data.resolution.x = (data.topCorner.x - data.bottomCorner.x) / data.split.x;
                data.resolution.y = (data.topCorner.y - data.bottomCorner.y) / data.split.y;
                data.resolution.z = (data.topCorner.z - data.bottomCorner.z) / data.split.z;
                
                
                //offset
                String[] offsetString = reader.readLine().split(" ");
                Vec3F offset = new Vec3F(Float.valueOf(offsetString[1]), Float.valueOf(offsetString[2]),Float.valueOf(offsetString[3]));
                
                String[] columnsNames = reader.readLine().split(" ");
                initAttributs(columnsNames);
                
                data.attributsNames.addAll(Arrays.asList(columnsNames));
                
                int lineNumber = 0;
                String line;                
                
                //start reading voxels
                while ((line = reader.readLine())!= null) {

                    String[] voxelLine = line.split(" ");
                    
                    
                    Point3i indice = new Point3i(Integer.valueOf(voxelLine[0]), 
                            Integer.valueOf(voxelLine[2]),
                            Integer.valueOf(voxelLine[1]));

                    Float[] mapAttrs = new Float[data.attributsNames.size()];

                    for (int i=0;i<voxelLine.length;i++) {
                        
                        float value = Float.valueOf(voxelLine[i]);
                        
                        mapAttrs[i] = value;
                        
                        Point2F minMaxPoint;
                        
                        if((minMaxPoint = minMax.get(columnsNames[i]))!=null){
                            
                            float min = minMaxPoint.x;
                            float max = minMaxPoint.y;
                            
                            if(value < min){
                                min = value;
                            }
                            
                            if(value > max){
                                max = value;
                            }
                            
                            minMaxPoint = new Point2F(min, max);
                            minMax.put(columnsNames[i], minMaxPoint);
                            
                        }else{
                            minMax.put(columnsNames[i], new Point2F(value, value));
                        }
                    }
                    
                    Point3f position = new Point3f((float) (data.bottomCorner.x+(indice.x*(data.resolution.x))),
                                                    (float) (data.bottomCorner.z+(indice.y*(data.resolution.y))),
                                                    (float) (data.bottomCorner.y+(indice.z*(data.resolution.z))));
                    
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
                    
                    data.voxels.add(new Voxel(indice, position, mapAttrs, 1.0f));

                    lineNumber++;

                    setReadFileProgress((lineNumber * 100) / count);
                }
                
                data.calculateAttributsLimits();
                
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


        updateValue();

        setCenter();
        setWidth();

        setFileLoaded(true);
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
        
        float[] values = new float[data.voxels.size()];
        
        int count = 0;
        for(Voxel voxel:data.voxels){
                    
            float attributValue;
            
            Float[] attributs = voxel.getAttributs();
            
            for(int i=0; i< attributs.length;i++){
                
                String name = data.attributsNames.get(i);
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

            //initialize minimum and maximum attributs values
            if(voxel ==  data.voxels.get(0)){

                attributValueMax = attributValue;
                attributValueMin = attributValue;
            }

            //set maximum attribut value
            if(attributValue>attributValueMax){

                attributValueMax = attributValue;
            }

            //set minimum attribut value
            if(attributValue < attributValueMin){

                attributValueMin = attributValue;
            }
            //if(voxel.attributValue == -1){
                //voxel.attributValue = -1;
            //}
            boolean drawVoxel = !(Float.isNaN(voxel.attributValue) || voxel.attributValue < 0/*|| voxel.attributValue == -1.0f */|| (/*!settings.drawNullVoxel &&*/ voxel.attributValue == 0));
            
            if(!drawVoxel){
                voxel.alpha = 0;
            }else{
                voxel.alpha = 1;
            }
            
            values[count] = voxel.attributValue;
            count++;
            
            
            
        }
        
        //calculate standard deviation
        
        StandardDeviation sd = new StandardDeviation();
        float sdValue = sd.getFromFloatArray(values);
        float average = sd.getAverage();
        
        //min = 0;
        min = average - (2*sdValue);
        max = average + (2*sdValue);
        
        //colorGradient = new ColorGradient(min, max);
        setGradientColor(gradient, min, max);
        
        /*
        colorGradient = new ColorGradient(attributValueMin, attributValueMax);
        setGradientColor(gradient, attributValueMin, attributValueMax);
        */
        gradientUpdated = false;
        
        
    }
    
    public void updateColorValue(Color[] gradient){
        setGradientColor(gradient, min, max);
    }
    
    public Vec3F getColorFromValue(float value){
        
        Color c = colorGradient.getColor(value);
        
        return new Vec3F(c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f);
    }
    
    
    public void setGradientColor(Color[] gradientColor, float valMin, float valMax){
        
        this.gradient = gradientColor;
        
        ColorGradient color = new ColorGradient(valMin, valMax);
        color.setGradientColor(gradientColor);

        for (Voxel voxel : data.voxels) {
            
            Color colorGenerated = color.getColor(voxel.attributValue);
            if (voxel.alpha == 0) {
                voxel.color = new Vector3f(colorGenerated.getRed()/255.0f, colorGenerated.getGreen()/255.0f, colorGenerated.getBlue()/255.0f);
            } else {
                voxel.color = new Vector3f(colorGenerated.getRed()/255.0f, colorGenerated.getGreen()/255.0f, colorGenerated.getBlue()/255.0f);
            }
        }
        //voxelList = ImageEqualisation.scaleHistogramm(voxelList);
        //voxelList = ImageEqualisation.voxelSpaceFormatEqualisation(voxelList);
        
        
    }
    
    
    public void updateInstanceColorBuffer(){
        
        gradientUpdated = false;
        
    }
    
    public BufferedImage createScaleImage(int width, int height){
        
        return ScaleGradient.generateScale(gradient, attributValueMin, attributValueMax, width, height, ScaleGradient.HORIZONTAL);
    }
    
    public void updateCubeSize(GL3 gl, float size){
        
        cubeSize = size;
        cubeSizeUpdated = false;
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
    
    @Override
    public void initBuffers(GL3 gl){
        
        cubeSize = (float) (data.resolution.x/2.0f);
        
        int instanceNumber = data.voxels.size();
        
        mesh = new InstancedMesh(MeshFactory.createCube(cubeSize), instanceNumber);
        
        float instancePositions[] = new float[instanceNumber*3];
        float instanceColors[] = new float[instanceNumber*4];

        for (int i=0, j=0, k=0;i<data.voxels.size();i++, j+=3 ,k+=4) {

            instancePositions[j] = data.voxels.get(i).position.x;
            instancePositions[j+1] = data.voxels.get(i).position.y;
            instancePositions[j+2] = data.voxels.get(i).position.z;

            instanceColors[k] = data.voxels.get(i).color.x;
            instanceColors[k+1] = data.voxels.get(i).color.y;
            instanceColors[k+2] = data.voxels.get(i).color.z;
            instanceColors[k+3] = data.voxels.get(i).alpha;
        }
        
        ((InstancedMesh)mesh).instancePositionsBuffer = Buffers.newDirectFloatBuffer(instancePositions);
        ((InstancedMesh)mesh).instanceColorsBuffer = Buffers.newDirectFloatBuffer(instanceColors);
        
        buffer = new MeshBuffer(gl);
        
        if(mesh instanceof InstancedMesh){
            buffer.initBuffers(gl, mesh.indexBuffer, new FloatBuffer[]{mesh.vertexBuffer, 
                                    ((InstancedMesh)mesh).instancePositionsBuffer, 
                                    ((InstancedMesh)mesh).instanceColorsBuffer});
        }
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
    
    public void render(GL3 gl){
        
        if(!gradientUpdated){
            
            float instanceColors[] = new float[data.voxels.size()*4];

            for (int i=0, j=0;i<data.voxels.size();i++, j+=4) {

                instanceColors[j] = data.voxels.get(i).color.x;
                instanceColors[j+1] = data.voxels.get(i).color.y;
                instanceColors[j+2] = data.voxels.get(i).color.z;
                instanceColors[j+3] = data.voxels.get(i).alpha;
            }

            ((InstancedMesh)mesh).instanceColorsBuffer = Buffers.newDirectFloatBuffer(instanceColors);
            
            buffer.updateBuffer(gl, 2, ((InstancedMesh)mesh).instanceColorsBuffer);
        }
        
        if(!cubeSizeUpdated){
            
            int instanceNumber = data.voxels.size();
            mesh = new InstancedMesh(MeshFactory.createCube(cubeSize), instanceNumber);
            
            buffer.updateBuffer(gl, 0, mesh.vertexBuffer);
            
            cubeSizeUpdated = true;
        }
        
        draw(gl, GL3.GL_TRIANGLES);
    }
}
