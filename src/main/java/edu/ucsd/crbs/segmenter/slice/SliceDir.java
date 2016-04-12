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

    public static String SLICE_PREFIX = "slice";
    
    private String _path;
    private int _sliceNumber;
    
    /**
     * Constructor
     * @param slicePath full path to Slice
     */
    public SliceDir(final String slicePath){
        _path = slicePath;
        extractSliceNumber();
    }

    public int getSliceNumber(){
        return _sliceNumber;
    }
    
    /**
     * Parses slice number from path by looking for last '_' in path
     * and assuming any characters to right are a number. 
     * @return slice number or -1 if path is null, -2 if no _ found in path
     * or -3 if the slice number could not be converted to an int
     */
    private void extractSliceNumber() {
        if (_path == null){
            _sliceNumber = -1;
            return;
        }
        int prefixPos = _path.lastIndexOf("_");
        
        if (prefixPos < 0){
            _log.log(Level.WARNING,"No _ found in path "
                    + _path);
            _sliceNumber = -2;
            return;
        }
        try {
            _sliceNumber = Integer.parseInt(_path.substring(prefixPos + 1));
        }
        catch(NumberFormatException nfe){
            _log.log(Level.WARNING,"Unable to extract slice number from path "
                    + _path);
            _sliceNumber = -3;
            return;
        }        
    }
    
    /**
     * Returns full path to slice
     * @return Path to slice directory
     */
    public String getFullPath(){
        return _path;
    }
    
    public String getSliceName(){
        if (_path == null){
            return null;
        }
        
        String notrailslash = _path.replaceAll(File.separator + "+$","");
        int slashpos = notrailslash.lastIndexOf(File.separator);
        if (slashpos < 0){
            return notrailslash;
        }
        return notrailslash.substring(slashpos + 1);
    }
}
