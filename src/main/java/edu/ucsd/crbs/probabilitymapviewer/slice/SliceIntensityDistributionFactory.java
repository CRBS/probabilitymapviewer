/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.slice;

/**
 * Defines method for classes that support generation of
 * {@link SliceIntensityDistribution} objects from a file path.
 * 
 * @author churas
 */
public interface SliceIntensityDistributionFactory {
   
    
    public SliceIntensityDistribution 
        getSliceIntensityDistribution(final String path);
}
