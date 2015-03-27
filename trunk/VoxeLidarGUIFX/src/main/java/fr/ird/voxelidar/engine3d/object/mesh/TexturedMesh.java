/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.engine3d.object.mesh;

import java.nio.FloatBuffer;

/**
 *
 * @author Julien
 */
public class TexturedMesh extends Mesh{
    
    public FloatBuffer textureCoordinatesBuffer;
    
    
    public TexturedMesh(){
    }
    
    public TexturedMesh(Mesh mesh){
        this.colorBuffer = mesh.colorBuffer;
        this.indexBuffer = mesh.indexBuffer;
        this.vertexBuffer = mesh.vertexBuffer;
        this.vertexCount = mesh.vertexCount;
    }
}