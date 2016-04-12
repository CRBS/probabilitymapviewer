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

    public static final String SLICE_PREFIX = "slice_";
    public static final String DM4_EXTENSION = ".dm4";
    
    private String _path;
    private int _sliceNumber;

    public Dm4SliceFile(final String slicePath) {
        _path = slicePath;
        extractSliceNumber();
    }

    public int getSliceNumber() {
        return _sliceNumber;
    }

    /**
     * Parses slice number from path by looking for last '_' in path and
     * assuming any characters to right are a number.
     *
     * @return slice number or -1 if path is null, -2 if no _ found in path or
     * -3 if the slice number could not be converted to an int
     */
    private void extractSliceNumber() {
        if (_path == null) {
            _sliceNumber = -1;
            return;
        }
        int prefixPos = _path.lastIndexOf("_");

        if (prefixPos < 0) {
            _log.log(Level.WARNING, "No _ found in path "
                    + _path);
            _sliceNumber = -2;
            return;
        }
        try {
            _sliceNumber = Integer.parseInt(_path.substring(prefixPos + 1));
        } catch (NumberFormatException nfe) {
            _log.log(Level.WARNING, "Unable to extract slice number from path "
                    + _path);
            _sliceNumber = -3;
            return;
        }
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
     * 
     * @return 
     */
    public String getSliceName() {
        if (_path == null) {
            return null;
        }

        String noextension = _path.replaceAll("\\" + DM4_EXTENSION + "$", "");
        
        int slicepos = noextension.lastIndexOf(Dm4SliceFile.SLICE_PREFIX);
        if (slicepos < 0) {
            return noextension;
        }
        return noextension.substring(slicepos);
    }

}
