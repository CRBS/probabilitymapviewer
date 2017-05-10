package edu.ucsd.crbs.probabilitymapviewer.job;

import edu.ucsd.crbs.probabilitymapviewer.util.RunCommandLineProcess;
import edu.ucsd.crbs.probabilitymapviewer.util.RunCommandLineProcessImpl;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Runs external command line job to perform
 * segmentation
 * @author churas
 */
public class ExternalCommandLineJob implements Callable{
 
    private static final Logger _log = 
            Logger.getLogger(ExternalCommandLineJob.class.getName());
    private String _inputImage;
    private String _binary;
    private String _tileSize;
    private String _outDir;
    private String _colorsToZeroOut;
    private String _analyzingTile;
    private String _optArgs;
    private RunCommandLineProcess _runCommandLineProcess;
    
    public ExternalCommandLineJob(final String inputImage,
            final String binary,final String outDir, final String tileSize,
            final String colorsToZeroOut, final String analyzingTile,
            final String optArgs) {
        _inputImage = inputImage;
        try {
            _binary = new File(binary).getCanonicalPath();
        }
        catch(IOException ex){
            _log.log(Level.SEVERE, "Caught IOException trying to get path to "
                    + "binary script : " + ex.getMessage());
            _binary = binary;
        }
        
        _outDir = outDir;
        _tileSize = tileSize;
        _colorsToZeroOut = colorsToZeroOut;
        _analyzingTile = analyzingTile;
        _optArgs = optArgs;
        _runCommandLineProcess = new RunCommandLineProcessImpl();
    }
    
    public String getInputImage(){
        return _inputImage;
    }
    
    public String getBinary(){
        return _binary;
    }
    
    public String getTileSize(){
        return _tileSize;
    }
    
    public String getColorsToZeroOut(){
        return _colorsToZeroOut;
    }
    
    public String getAnalyzingTile(){
        return _analyzingTile;
    }
    
    public String getOptArgs(){
        return _optArgs;
    }
    
    public String getOutDir(){
        return _outDir;
    }
    
    public void setRunCommandLineProcess(RunCommandLineProcess rclp){
        _runCommandLineProcess = rclp;
    }    
    
    @Override
    public JobResult call() {
        _runCommandLineProcess.setWorkingDirectory(_outDir);
        String result = null;
        _log.log(Level.INFO, "Running {0} on {1} with optargs: {2}",
                new Object[]{_binary,_inputImage,_optArgs});
        JobResult jobResult = new JobResult();
        try {
            int slashPos = _inputImage.lastIndexOf('/');
            String fileName = _inputImage.substring(slashPos+1);

            if (_analyzingTile != null){
                File aTile = new File(_analyzingTile);
                if (aTile.exists()){
                    FileUtils.copyFile(aTile,
                            new File(_outDir+File.separator+fileName));
                }
            }
            
            File tempDir = new File(_outDir+File.separator+fileName+"dir");
            tempDir.mkdirs();
            
            long startTime = System.currentTimeMillis();
        
            result = _runCommandLineProcess.runCommandLineProcess(_binary,
                    _inputImage,
                    tempDir.getAbsolutePath() + File.separator + fileName,
                    _optArgs);
            
            _log.log(Level.INFO,"opt args:{0}:", new Object[]{_optArgs});
            
            long extDuration = System.currentTimeMillis() - startTime;
            _log.log(Level.FINE,"{0} output: {1}",
                    new Object[]{_binary, result});
        
            startTime = System.currentTimeMillis();
            result = _runCommandLineProcess.runCommandLineProcess("convert",
                    tempDir.getAbsolutePath()+File.separator+fileName,
                    "-transparent","black","-alpha","set",
                    "-channel","A",
                    "-channel",_colorsToZeroOut,"-evaluate","set","0",
                    _outDir+File.separator+fileName);
            _log.log(Level.FINE,"convert output: {0}", result);
            
             long convertDuration = System.currentTimeMillis() - startTime;
             
             jobResult.setRunTimeInMilliseconds(extDuration+convertDuration);
             _log.log(Level.INFO,"{0} {1} Took: "
                     + "{2} seconds and convert took {3} seconds",
                     new Object[]{_inputImage,_binary,extDuration/1000,
                         convertDuration/1000});
             FileUtils.deleteDirectory(tempDir);
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught exception trying to run {0} {1}",
                    new Object[]{_binary,ex.getMessage()});
        }
        return jobResult;
    }
}
