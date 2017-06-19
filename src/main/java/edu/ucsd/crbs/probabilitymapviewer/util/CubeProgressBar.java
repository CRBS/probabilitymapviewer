package edu.ucsd.crbs.probabilitymapviewer.util;

/**
 * Defines current methods to denote correct cube to display
 * based on progress of collection
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface CubeProgressBar {
    
    public void setExpectedSlices(int val);
    
    /**
     * Gets the cube based on current progress of collection
     * @return 
     */
    public String getCubeImage(int slicesCollected);

}
