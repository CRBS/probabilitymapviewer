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
 * Represents a dm4 slice which has expected naming format of:
 * BaseName_3viewBS_slice_####.dm4
 *
 * At the current moment it is not known if BaseName_3viewBS will change or if
 * that is always fixed. The #### should be the slice number.
 *
 * @author churas
 */
public class Dm4SliceFile {

    private static final Logger _log
            = Logger.getLogger(Dm4SliceFile.class.getName());

    public static final String DM4_EXTENSION = ".dm4";
    
    private String _path;
    private int _sliceNumber;

    public Dm4SliceFile(final String slicePath) {
        _path = slicePath;
    }

    /**
     * Returns full path to slice
     *
     * @return Path to slice directory
     */
    public String getFullPath() {
        return _path;
    }

    /**
     * Strips off extension .dm4 from file name
     * @return 
     */
    public String getSliceName() {
        if (_path == null) {
            return null;
        }
        
        return new File(_path).getName()
                .replaceAll("\\" + DM4_EXTENSION + "$", "");
    }

}
