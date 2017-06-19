package edu.ucsd.crbs.probabilitymapviewer.slice;

/**
 * Defines methods to convert some data to a slice that is consumable by
 * segmenter
 * @author churas
 */
public interface SliceConverter {
    
    /**
     * Converts image defined by <b>sourcePath</b> to a set of tiled
     * images and storing them into <b>destPath</b> directory
     * @param sourcePath Path to source image
     * @param destPath Path to destination directory
     * @throws Exception If there was an error in conversion
     */
    public void convert(final String sourcePath,
            final String destPath) throws Exception;
}
