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
