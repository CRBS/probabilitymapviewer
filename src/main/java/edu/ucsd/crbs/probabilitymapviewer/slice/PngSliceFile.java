package edu.ucsd.crbs.probabilitymapviewer.slice;

import java.io.File;

/**
 * Represents a png slice which has expected naming format of:
 * BaseName_3viewBS_slice_####.png
 *
 * At the current moment it is not known if BaseName_3viewBS will change or if
 * that is always fixed. The #### should be the slice number.
 * @author churas
 */
public class PngSliceFile {
    
    public static final String PNG_EXTENSION = ".png";
    
    private String _path;
    private int _sliceNumber;

    public PngSliceFile(final String slicePath) {
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
     * Strips off extension .png from file name
     * @return 
     */
    public String getSliceName() {
        if (_path == null) {
            return null;
        }
        
        return new File(_path).getName()
                .replaceAll("\\" + PNG_EXTENSION + "$", "");
    }
    
}
