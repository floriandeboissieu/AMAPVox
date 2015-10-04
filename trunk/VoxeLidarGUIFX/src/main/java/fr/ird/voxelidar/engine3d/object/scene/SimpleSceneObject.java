/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.engine3d.object.scene;

import com.jogamp.opengl.GL3;
import fr.ird.voxelidar.engine3d.mesh.GLMesh;
import fr.ird.voxelidar.engine3d.loading.shader.Shader;
import fr.ird.voxelidar.engine3d.mesh.TexturedGLMesh;
import static fr.ird.voxelidar.engine3d.object.scene.VoxelSpace.FLOAT_SIZE;
import java.nio.IntBuffer;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public class SimpleSceneObject extends SceneObject{
    
    public SimpleSceneObject(GLMesh mesh, int shaderId, boolean isAlphaRequired){
        
        super(mesh, shaderId, isAlphaRequired);
    }
    
    @Override
    public void initBuffers(GL3 gl){
        
        mesh.initBuffers(gl, GLMesh.DEFAULT_SIZE);
    }
    
    @Override
    public void initVao(GL3 gl, Shader shader){
        
        //generate vao
        IntBuffer tmp = IntBuffer.allocate(1);
        gl.glGenVertexArrays(1, tmp);
        vaoId = tmp.get(0);
        
        gl.glBindVertexArray(vaoId);
        
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, mesh.getVboId());
                
                if(textureId != 0){
                    //gl.glActiveTexture(GL3.GL_TEXTURE0);
                    gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
                }
            
                gl.glEnableVertexAttribArray(shader.attributeMap.get("position"));
                gl.glVertexAttribPointer(shader.attributeMap.get("position"), 3, GL3.GL_FLOAT, false, 0, 0);
                
                if(mesh.colorBuffer != null){
                    gl.glEnableVertexAttribArray(shader.attributeMap.get("color"));
                    gl.glVertexAttribPointer(shader.attributeMap.get("color"), 3, GL3.GL_FLOAT, false, 0, mesh.vertexBuffer.capacity()*FLOAT_SIZE);
                    
                    gl.glEnableVertexAttribArray(shader.attributeMap.get("normal"));
                    gl.glVertexAttribPointer(shader.attributeMap.get("normal"), 3, GL3.GL_FLOAT, false, 0, mesh.vertexBuffer.capacity()*FLOAT_SIZE+mesh.normalBuffer.capacity()*FLOAT_SIZE);
                }else if(mesh instanceof TexturedGLMesh){
                    gl.glEnableVertexAttribArray(shader.attributeMap.get("textureCoordinates"));
                    gl.glVertexAttribPointer(shader.attributeMap.get("textureCoordinates"), 2, GL3.GL_FLOAT, false, 0, mesh.vertexBuffer.capacity()*FLOAT_SIZE);
                }
                
                if(textureId != -1){
                    gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
                }
                 
            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, mesh.getIboId());
            
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
            
        gl.glBindVertexArray(0);
    }
    
    @Override
    public void draw(GL3 gl){
        
        gl.glBindVertexArray(vaoId);
            if(texture != null){
                gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
            }
            
            mesh.draw(gl);

            if(texture != null){
                gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
            }
        gl.glBindVertexArray(0);
    }
}
