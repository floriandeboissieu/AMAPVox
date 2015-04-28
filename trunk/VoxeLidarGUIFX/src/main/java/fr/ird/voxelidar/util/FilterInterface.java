/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public interface FilterInterface {
    
    public static Set<Filter> filters = new HashSet<>();
    public void addFilter(Filter filter);
    public boolean doFilter(ArrayList<String> attributsNames, float[] attributs);
    public boolean doFilter(ArrayList<String> attributsNames, float attribut);
}
