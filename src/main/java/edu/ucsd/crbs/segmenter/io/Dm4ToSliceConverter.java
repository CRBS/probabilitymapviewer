/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.io;

import java.util.Properties;

/**
 * Converts dm4 image to slice aka bunch of tiled images
 * @author churas
 */
public class Dm4ToSliceConverter implements SliceConverter {

    /**
     * Constructor
     */
    public Dm4ToSliceConverter(Properties props) {
       //need binary paths for convert, dm2mrc, mrc2tif
       // need downsampling values
       // need any cropping values
       // need temp dir to use or if destPath.tmp will suffice
       // 

    }

    
    
    @Override
    public void convert(String sourcePath, String destPath) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); 
        //verify src and dest are valid
        
        //verify enough disk space (not sure if this is doable)
        
        //create a temp directory
        
        //run dm2mrc
        
        //run mrc2tif
        
        //run convert with rescale to create tiles
        
        //rename temp directory to destPath directory
    }
    
}
