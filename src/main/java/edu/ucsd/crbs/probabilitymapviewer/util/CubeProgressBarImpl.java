package edu.ucsd.crbs.probabilitymapviewer.util;

import edu.ucsd.crbs.probabilitymapviewer.App;
import java.util.Properties;

/**
 * Based on number of slices collected instances of this class
 * let caller know which cube to display in cube progress bar image
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CubeProgressBarImpl implements CubeProgressBar {

    private int _expectedSlices;
    
    public static final String CUBES_PREFIX_PATH="cubes/";
    
    public CubeProgressBarImpl(Properties props){
        if (props == null){
            _expectedSlices = 1000;
            return;
        }
        _expectedSlices = Integer.parseInt(props.getProperty(App.EXPECTED_SLICES_ARG,"1000"));
    }
    
    public void setExpectedSlices(int val){
        _expectedSlices = val;
    }
    
    /**
     * Returns appropriate cube to display based on progress of collection. 
     * @param slicesCollected
     * @return 
     */
    @Override
    public String getCubeImage(int slicesCollected) {
        if (slicesCollected <= 0 || _expectedSlices == 0){
            return CUBES_PREFIX_PATH+"cube.png";
        }
        
        double percentComplete = (double)slicesCollected/(double)_expectedSlices;
        percentComplete*=10;
        percentComplete = Math.floor(percentComplete);
        long percentAsLong = Math.round(percentComplete)*10;
        if (percentAsLong < 10){
            percentAsLong = 10L;
        } else if (percentAsLong > 100){
            percentAsLong = 100L;
        }
        
        
        return CUBES_PREFIX_PATH+"cube"+Long.toString(percentAsLong)+".png";
    }
}
