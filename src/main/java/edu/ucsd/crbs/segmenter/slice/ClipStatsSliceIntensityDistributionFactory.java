package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.util.RunCommandLineProcess;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs clip command to extract min, max, mean, and standard deviation of
 * pixel intensities from an mrc file.
 * @author churas
 */
public class ClipStatsSliceIntensityDistributionFactory implements 
        SliceIntensityDistributionFactory {

    private static final Logger _log
            = Logger.getLogger(ClipStatsSliceIntensityDistributionFactory
                    .class.getName());

    private RunCommandLineProcess _runCommandLineProcess;
    private String _clipCmd;
        
    public ClipStatsSliceIntensityDistributionFactory(final String clipCmd,
            RunCommandLineProcess rclp){
        _runCommandLineProcess = rclp;
        _clipCmd = clipCmd;
    }
    
    /**
     * Runs clip command to get stats on image set by <b>path</b> parameter.
     * Method assumes output from clip is in this format:
     * <code>
     *   slice|   min   |(   x,   y)|    max  |(      x,      y)|   mean    |  std dev.
     *   -----|---------|-----------|---------|-----------------|-----------|----------
     *   0    65.0000 (2019,7270) 30021.0000 (7822.01,8327.14) 29123.6884   202.0377
     * </code>
     * In the above example 65 is the min, 30021 is max, 29123 is mean, and
     * 202 is standard deviation.
     * @param path Path to image to extract image stats from
     * @return SliceItensityDistribution object upon success or null upon error
     */
    @Override
    public SliceIntensityDistribution 
        getSliceIntensityDistribution(final String path) {
            
         long startTime = System.currentTimeMillis();
         String result = "";
         try {
            result = _runCommandLineProcess.runCommandLineProcess(_clipCmd,
                            "stats", path);
         }
         catch(Exception ex){
             _log.log(Level.WARNING,"Caught exception running {0} : {1}",
                     new Object[]{_clipCmd,ex.getMessage()});
             return null;
         }
        
        long duration = System.currentTimeMillis() - startTime;

        _log.log(Level.FINE, "clip stats output: {0}", result);
        _log.log(Level.INFO, "running clip stats took {0} seconds", 
                duration / 1000);
        
        //need to parse result string for min,max, and standard deviation
        /*
        slice|   min   |(   x,   y)|    max  |(      x,      y)|   mean    |  std dev.
        -----|---------|-----------|---------|-----------------|-----------|----------
        0    65.0000 (2019,7270) 30021.0000 (7822.01,8327.14) 29123.6884   202.0377
        all    65.0000 (@ z=    0) 30021.0000 (@ z=    0      ) 29123.6884   202.0377
        */
        String[] lines = result.split("\n");
        if (lines.length < 3){
            _log.log(Level.WARNING,"Expected multiple lines from result {0}",
                    result);
            return null;
        }
        String splitLine[] = lines[2].split(" +");
        
        
        if (splitLine.length != 8){
            _log.log(Level.WARNING,"Expected 7 elements got {0} from line {1}",
                    new Object[]{splitLine.length,lines[2]});
            return null;
        }
        SliceIntensityDistribution sid = new SliceIntensityDistribution();
        sid.setMinIntensity(Double.parseDouble(splitLine[2]));
        sid.setMaxIntensity(Double.parseDouble(splitLine[4]));
        sid.setMeanIntensity(Double.parseDouble(splitLine[6]));
        sid.setStandardDeviation(Double.parseDouble(splitLine[7]));
        
        return sid;
    }
    
}
