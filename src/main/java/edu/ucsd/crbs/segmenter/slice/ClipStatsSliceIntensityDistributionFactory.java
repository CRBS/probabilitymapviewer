/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.util.RunCommandLineProcess;
import edu.ucsd.crbs.segmenter.util.RunCommandLineProcessImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author churas
 */
public class ClipStatsSliceIntensityDistributionFactory implements 
        SliceIntensityDistributionFactory {

    private static final Logger _log
            = Logger.getLogger(ClipStatsSliceIntensityDistributionFactory
                    .class.getName());

    private RunCommandLineProcess _runCommandLineProcess;
    private String _clipCmd;
    
    public static void main(String[] args){
        System.out.println("Hello world");
        RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
        ClipStatsSliceIntensityDistributionFactory c 
                = new ClipStatsSliceIntensityDistributionFactory("clip",rclp);
        c.getSliceIntensityDistribution(args[0]);
    }
    
    public ClipStatsSliceIntensityDistributionFactory(final String clipCmd,
            RunCommandLineProcess rclp){
        _runCommandLineProcess = rclp;
        _clipCmd = clipCmd;
    }
    
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
             _log.log(Level.WARNING,"Caughte exception running {0} : {1}",
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
        
        for (int i = 0; i < splitLine.length; i++){
            System.out.println("\t " + Integer.toString(i)+ " :"+splitLine[i]+":");
        }
        System.out.println("Min="+splitLine[2]);
        System.out.println("Max="+splitLine[4]);
        System.out.println("Mean="+splitLine[6]);
        System.out.println("stddev="+splitLine[7]);
        
        SliceIntensityDistribution sid = new SliceIntensityDistribution();
        sid.setMinIntensity(Double.parseDouble(splitLine[2]));
        sid.setMaxIntensity(Double.parseDouble(splitLine[4]));
        sid.setMeanIntensity(Double.parseDouble(splitLine[6]));
        if (splitLine.length != 8){
            _log.log(Level.WARNING,"Expected 7 elements got {0}",
                    splitLine.length);
            return null;
        }
        return null;
    }
    
}
