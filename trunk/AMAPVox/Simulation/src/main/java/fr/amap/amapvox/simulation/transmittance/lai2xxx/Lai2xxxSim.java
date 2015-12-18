/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.amap.amapvox.simulation.transmittance.lai2xxx;

import fr.amap.amapvox.commons.util.Statistic;
import fr.amap.amapvox.jeeb.raytracing.geometry.LineElement;
import fr.amap.amapvox.jeeb.raytracing.geometry.LineSegment;
import fr.amap.amapvox.jeeb.raytracing.util.BoundingBox3d;
import fr.amap.amapvox.jeeb.raytracing.voxel.DirectionalTransmittance;
import fr.amap.amapvox.jeeb.raytracing.voxel.Scene;
import fr.amap.amapvox.jeeb.raytracing.voxel.VoxelManager;
import fr.amap.amapvox.jeeb.raytracing.voxel.VoxelManagerSettings;
import fr.amap.amapvox.jeeb.raytracing.voxel.VoxelSpace;
import fr.amap.amapvox.jeeb.workspace.sunrapp.light.Turtle;
import fr.amap.amapvox.simulation.transmittance.TransmittanceCfg;
import fr.amap.amapvox.simulation.transmittance.TransmittanceParameters;
import static fr.amap.amapvox.simulation.transmittance.lai2xxx.LAI2xxx.ViewCap.CAP_360;
import fr.amap.amapvox.voxcommons.Voxel;
import fr.amap.amapvox.voxcommons.VoxelSpaceInfos;
import fr.amap.amapvox.voxreader.VoxelFileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.apache.log4j.Logger;

/**
 *
 * @author Julien
 */
public class Lai2xxxSim {
    
    private final static Logger logger = Logger.getLogger(Lai2xxxSim.class);
    
    private final LAI2xxx lai2xxx;
    private final TransmittanceParameters parameters;
    
    private DirectionalTransmittance direcTransmittance;
    private List<Point3d> positions;
    
    private final Vector3f[] directions;
    private VoxelSpace voxSpace;

    public Lai2xxxSim(TransmittanceCfg cfg) {
        
        parameters = cfg.getParameters();
        
        if(parameters.getMode() == TransmittanceParameters.Mode.LAI2000){
            lai2xxx = new LAI2000(parameters.getDirectionsNumber(), CAP_360, parameters.getMasks());
        }else{
            lai2xxx = new LAI2200(parameters.getDirectionsNumber(), CAP_360, parameters.getMasks());
        }

        logger.info("Computing directions...");
        lai2xxx.computeDirections();
        
        directions = lai2xxx.getDirections();
    }
    
    public void process() throws Exception{
        
        //*******début du test
        //lecture du fichier voxel
        VoxelFileReader voxReader = new VoxelFileReader(parameters.getInputFile());
        VoxelSpaceInfos infos = voxReader.getVoxelSpaceInfos();

        Iterator<Voxel> iterator = voxReader.iterator();
        Voxel voxels[][][] = new Voxel[infos.getSplit().x][infos.getSplit().y][infos.getSplit().z];

        //conversion de la liste de voxels en tableau 3d
        while (iterator.hasNext()) {
            Voxel voxel = iterator.next();
            voxels[voxel.$i][voxel.$j][voxel.$k] = voxel;
        }
        //initialisation de la scène
        Scene scene = new Scene();
        scene.setBoundingBox(new BoundingBox3d(infos.getMinCorner(), infos.getMaxCorner()));
        

        //création d'un nouveau VoxelManager avec les paramètres du fichier voxel
        VoxelManager vm = new VoxelManager(scene, new VoxelManagerSettings(infos.getSplit(), 0));
        
        //*******fin du test
        
        logger.info("===== " + parameters.getInputFile().getAbsolutePath() + " =====");

        direcTransmittance = new DirectionalTransmittance(parameters.getInputFile());
        voxSpace = direcTransmittance.getVoxSpace();
        
        getSensorPositions();

        // TRANSMITTANCE
        logger.info("Computation of transmittance");
        
        
        lai2xxx.initPositions(positions.size());
        
        int positionID = 0;
        double transmitted;
        
        Statistic NaNCounter = new Statistic();
        
        for (Point3d position : positions) {
            
            for (int t = 0; t < directions.length; t++) {
                
                Vector3d dir = new Vector3d(directions[t]);
                dir.normalize();

                transmitted = direcTransmittance.directionalTransmittance(position, dir);
                    
                int ring = lai2xxx.getRingIDFromDirectionID(t);
                
                
                //test
                LineElement lineElement = new LineSegment(position, new Vector3d(dir), 99999999);
                //distance cumulée
                double distance = 0;


                //dernière distance valide (sortie de canopée)
                double lastValidDistance = 0;

                //get the first voxel cross by the line
                VoxelManager.VoxelCrossingContext context = vm.getFirstVoxel(lineElement);

                double distanceToHit = lineElement.getLength();
                boolean gotOneNaN = false;

                while ((context != null) && (context.indices != null)) {

                    //current voxel
                    Point3i indices = context.indices;
                    Voxel voxel = voxels[indices.x][indices.y][indices.z];
                    
                    if(voxel.ground_distance < 0.0f){
                        break;
                    }
                    
                    if(Float.isNaN(voxel.PadBVTotal)){
                        gotOneNaN = true;
                        break;
                    }

                    if(voxel.PadBVTotal > 0){
                        lastValidDistance = distance;
                    }

                    //distance from the last origin to the point in which the ray enter the voxel
                    double d1 = context.length;

                    context = vm.CrossVoxel(lineElement, indices);

                    //distance from the last origin to the point in which the ray exit the voxel
                    double d2 = context.length;

                    if (d2 < distanceToHit) {

                        distance += (d2 - d1);

                    }else if (d1 >= distanceToHit) {

                    }else {
                        distance += (d2 - d1);
                    }
                }

                double pathLength = lastValidDistance;
                
                //test
                if(!gotOneNaN && pathLength != 0){
                    
                    NaNCounter.addValue(transmitted);
                    lai2xxx.addTransmittance(ring, positionID, (float) (Math.pow(transmitted, 1/pathLength)));
                    //lai2xxx.addTransmittance(ring, positionID, (float) transmitted);
                }else{
                    NaNCounter.addValue(Double.NaN);
                }
                
                //lai2xxx.addTransmittance(ring, positionID, (float) transmitted);
                
            }

            positionID++;

            if (positionID % 1000 == 0) {
                logger.info(positionID + "/" + positions.size());
            }
        }
        
        //test
        System.out.println("Nb values : "+NaNCounter.getNbValues());
        System.out.println("Nb NaN values : "+NaNCounter.getNbNaNValues());
        
        if(parameters.isGenerateTextFile()){
            writeTransmittance();
            logger.info("File "+parameters.getTextFile().getAbsolutePath()+" was written.");
        }

        logger.info("Simulation is finished.");        
    }
    
    //doublon (même méthode dans TransmittanceSim, à nettoyer)
    private void getSensorPositions() {
        
        positions = parameters.getPositions();
        
        if(positions == null){ //to remove in the future, keep compatibility with deprecated functions
            
            positions = new ArrayList<>();
        
            if(parameters.isUseScanPositionsFile()){

                File pointPositionsFile = parameters.getPointsPositionsFile();

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(pointPositionsFile));

                    String line;
                    boolean firstLineParsed = false;

                    while((line = reader.readLine()) != null){

                        line = line.replaceAll(" ", ",");
                        line = line.replaceAll("\t", ",");

                        String[] split = line.split(",");

                        if(split != null && split.length >= 3){

                            if(split.length > 3 && !firstLineParsed){
                                logger.info("Sensor position file contains more than three columns, parsing the three first");
                            }

                            Point3d position = new Point3d(Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]));

                            int i = (int) ((position.x - voxSpace.getBoundingBox().min.x) / voxSpace.getVoxelSize().x);
                            int j = (int) ((position.y - voxSpace.getBoundingBox().min.y) / voxSpace.getVoxelSize().y);

                            if(i < voxSpace.getSplitting().x && i >= 0 && j < voxSpace.getSplitting().y && j >= 0){
                                positions.add(position);
                            }else{
                                logger.warn("Position "+position.toString() +" ignored because out of voxel space!");
                            }
                        }

                        if(!firstLineParsed){
                            firstLineParsed = true;
                        }
                    }

                } catch (FileNotFoundException ex) {
                    logger.error("File "+ parameters.getPointsPositionsFile()+" not found", ex);
                } catch (IOException ex) {
                    logger.error("An error occured when reading file", ex);
                }

            }else{
                // Smaller plot at center
                int size = (int)parameters.getWidth();

                int middleX = (int)parameters.getCenterPoint().x;
                int middleY = (int)parameters.getCenterPoint().y;

                int xMin = middleX - size;
                int yMin = middleY - size;

                int xMax = middleX + size;
                int yMax = middleY + size;

                xMin = Integer.max(xMin, 0);
                yMin = Integer.max(yMin, 0);

                xMax = Integer.min(xMax, voxSpace.getSplitting().x -1);
                yMax = Integer.min(yMax, voxSpace.getSplitting().y -1);

                for (int i = xMin; i < xMax; i++) {

                    double tx = (0.5f + (double) i) * voxSpace.getVoxelSize().x;

                    for (int j = yMin; j < yMax; j++) {

                        double ty = (0.5f + (double) j) * voxSpace.getVoxelSize().y;
                        Point3d pos = new Point3d(voxSpace.getBoundingBox().min);
                        pos.add(new Point3d(tx, ty, direcTransmittance.getMnt()[i][j] + parameters.getCenterPoint().z));
                        positions.add(pos);
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(parameters.getTextFile().getParentFile()+File.separator+"positions.txt")))) {

                    for(Point3d position : positions){
                        writer.write(position.x + " " + position.y + " " + position.z + "\n");
                    }

                    writer.close();
                }catch (IOException ex) {
                logger.error("Cannot write positions.txt file in output directory", ex);
                }
            }
        }
        
        
        logger.info("nb positions= " + positions.size());
    }
    
    public void writeTransmittance() throws IOException{
        
        if(parameters.isGenerateLAI2xxxTypeFormat()){
            lai2xxx.writeOutput(parameters.getTextFile());
        }else{

            lai2xxx.computeValues();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(parameters.getTextFile()))) {

                bw.write("posX\tposY\tposZ\tLAI\tGAP[1]\tGAP[2]\tGAP[3]\tGAP[4]\tGAP[5]\n");

                for(int i =0 ; i<positions.size() ; i++){

                    Point3d position = positions.get(i);

                    String line = position.x + "\t" + position.y + "\t" + position.z+"\t" + lai2xxx.getByPosition_LAI()[i];

                    for(int r=0;r<lai2xxx.getRingNumber();r++){
                        line += "\t"+lai2xxx.getGapsByRingAndPosition()[r][i];
                    }

                    bw.write(line+"\n");

                }
            }catch(IOException ex){
                throw ex;
            }

        }
    }
}
