/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a slice which is a path to a directory with
 * name in this structure slice_#### 
 * where #### is a number
 * @author churas
 */
public class SliceDir {
    
    private static final Logger _log = Logger.getLogger(SliceDir.class.getName());

    public static String SLICE_PREFIX = "slice_";
    
    private File _sliceDir;
    private int _sliceNumber;
    
    /**
     * Constructor
     * @param slicePath full path to Slice
     */
    public SliceDir(final String slicePath){
        if (slicePath == null){
            throw new NullPointerException("Slice Path cannot be null");
        }
        _sliceDir = new File(slicePath);
        extractSliceNumber();
    }

    public int getSliceNumber(){
        return _sliceNumber;
    }
    
    public long getLastModified(){
        return _sliceDir.lastModified();
    }
    
    /**
     * Parses slice number from path by looking for last '_' in path
     * and assuming any characters to right are a number. 
     * @return slice number or -1 if path is null, -2 if no _ found in path
     * or -3 if the slice number could not be converted to an int
     */
    private void extractSliceNumber() {
        if (_sliceDir == null){
            _sliceNumber = -1;
            return;
        }
        int prefixPos = _sliceDir.getName().lastIndexOf("_");
        
        if (prefixPos < 0){
            _log.log(Level.WARNING,"No _ found in path "
                    + _sliceDir.getName());
            _sliceNumber = -2;
            return;
        }
        try {
            _sliceNumber = Integer.parseInt(_sliceDir.getName()
                    .substring(prefixPos + 1));
        }
        catch(NumberFormatException nfe){
            _log.log(Level.WARNING,"Unable to extract slice number from path "
                    + _sliceDir.getName());
            _sliceNumber = -3;
            return;
        }        
    }
    
    /**
     * Returns full path to slice
     * @return Path to slice directory
     */
    public String getFullPath(){
        return _sliceDir.getAbsolutePath();
    }
    
    public String getSliceName(){
        if (_sliceDir == null){
            return null;
        }
        
        return _sliceDir.getName();
    }
}
