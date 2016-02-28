/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.amap.lidar.amapvox.voxviewer.loading.shader;

import com.jogamp.opengl.GL3;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author calcul
 */
public abstract class Uniform {
    
    protected final String name;
    protected final List<Shader> owners;
    protected boolean dirty; //if true it means no shaders has updated the uniform
    
    public Uniform(String name){
        
        owners = new ArrayList<>();
        
        this.name = name;
    }
    
    public Uniform(String name, Shader shader){
        
        owners = new ArrayList<>();
        
        this.name = name;
        
        owners.add(shader);
        
    }
    
    public void addOwner(Shader shader){
        owners.add(shader);
    }
    
    public void notifyOwners(){
        
        if(!owners.isEmpty()){
            
            dirty = false;
            
            for (Shader owner : owners) {
                owner.notifyDirty(this);
            }
            
        }else{
            dirty = true;
        }
        
    }

    public String getName() {
        return name;
    }

    public boolean isDirty() {
        return dirty;
    }
    
    public abstract void update(GL3 gl, int location); 
}