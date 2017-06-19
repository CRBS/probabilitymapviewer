package edu.ucsd.crbs.probabilitymapviewer.slice;

import java.util.List;
import java.util.Properties;

/**
 * Implementing classes will provide a list of slice sub paths with the
 * newest slices first
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface SliceMonitor {
    
    /**
     * Gets list of slices sub paths with the newest first
     * @return List of slice sub paths or null if there was an error.  If 
     * only a single image is found then the list will have 1 element and it
     * will be an empty string
     */
    public List<SliceDir> getSlices() throws Exception;

    
    public Properties getCollectionInformation() throws Exception;
}
