package edu.ucsd.crbs.probabilitymapviewer.slice;

import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.util.RunCommandLineProcess;
import edu.ucsd.crbs.probabilitymapviewer.util.RunCommandLineProcessImpl;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author churas
 */
public class PngToSliceConverter implements SliceConverter {
    
        
    public static final String ZOOM = "0";
    public static final String TMP_SUFFIX = ".tmp";

    private static final Logger _log
            = Logger.getLogger(Dm4ToSliceConverter.class.getName());

    private Properties _props;
    private String _convert_cmd;
    private int _downsample_factor;
    private RunCommandLineProcess _runCommandLineProcess;
    private String _tileSizeArgForConvert;
    private String _renameArgForConvert;
    private boolean _addEqualize = false;

    /**
     * Constructor
     */
    public PngToSliceConverter(Properties props) {
        _props = props;
        if (_props == null) {
            throw new NullPointerException("Properties passed in constructor "
                    + "is null");
        }

        _convert_cmd = _props.getProperty(App.CONVERT_ARG);
        if (_convert_cmd == null) {
            throw new NullPointerException(App.CONVERT_ARG
                    + " property is null");
        }
        
        _addEqualize = Boolean.parseBoolean(
                _props.getProperty(App.CONVERT_EQUALIZE_ARG,
                "false"));

        // need downsampling values
        _downsample_factor = 1;
        try {
            _downsample_factor = Integer.parseInt(
                    _props.getProperty(App.DOWNSAMPLEFACTOR_ARG, "1"));
            if (_downsample_factor < 1) {
                _downsample_factor = 1;
            }
        } catch (Exception ex) {
            //we'll just default to 1 after logging the failure here
            _log.log(Level.WARNING, "Invalid downsample value of {0} received"
                    + " using value of 1",
                    _props.getProperty(App.DOWNSAMPLEFACTOR_ARG, "1"));
        }
        String tileSize = _props.getProperty(App.TILE_SIZE_ARG, "128");
        _tileSizeArgForConvert = tileSize + "x" + tileSize;
        _renameArgForConvert = "r%[fx:page.y/" + tileSize + "]_c%[fx:page.x/"
                + tileSize + "]";

        _runCommandLineProcess = new RunCommandLineProcessImpl();
    }

    public void setRunCommandLineProcess(RunCommandLineProcess rclp) {
        _runCommandLineProcess = rclp;
    }

    /**
     * Gets the downsample factor set via the constructor
     *
     * @return
     */
    public int getDownsampleFactor() {
        return _downsample_factor;
    }

    @Override
    public void convert(String sourcePath, String destPath) throws Exception {

        //verify src and dest are valid
        File srcFile = new File(sourcePath);
        if (srcFile.isFile() == false) {
            throw new Exception("Source file " + srcFile.getAbsolutePath()
                    + " is not a file");
        }

        //create a temp directory        
        File destTmpDir = new File(destPath + TMP_SUFFIX);
        if (destTmpDir.mkdirs() == false) {
            throw new Exception("Unable to create "
                    + destTmpDir.getAbsolutePath() + " tmp directory");
        }
        Thread.sleep(1000); //no directory yet very weird....
        
        //write out a readme.props file to directory
        writeReadmeFile(sourcePath,destTmpDir.getAbsolutePath());
        
        _runCommandLineProcess
                .setWorkingDirectory(destTmpDir.getAbsolutePath());

        //run convert with rescale to create tiles
        run_convert(sourcePath, destTmpDir.getAbsolutePath());

        //rename temp directory to destPath directory
        if (destTmpDir.renameTo(new File(destPath)) == false) {
            throw new Exception("Unable to rename "
                    + destTmpDir.getAbsolutePath() + " to " + destPath);
        }
    }
    
    public void writeReadmeFile(final String sourcePath,final String destPath){
        try {
            FileWriter fw = new FileWriter(destPath + File.separator 
                    + "readme.props");
            fw.write("inputfile=" + sourcePath + "\n");
            fw.write("name=" 
                    + sourcePath.replace("^.*/","")
                            .replace(PngSliceFile.PNG_EXTENSION +"$","") 
                    +"\n");
            fw.flush();
            fw.close();
        }catch(Exception ex){
            _log.log(Level.WARNING," Caught Exception: " + ex.getMessage());
        }
    }

    private void run_convert(final String sourcePath,
            final String destTmpDir) throws Exception {

        long startTime = System.currentTimeMillis();
        
        double dfactor = (1.0/(double)_downsample_factor)*100.0;
        
        String resizePercent = Long.toString(Math.round(dfactor)) + "%";

        _log.log(Level.FINE, "Resize set to : " + resizePercent);
        
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(_convert_cmd);
        cmd.add(sourcePath);
        cmd.add("-resize");
        cmd.add(resizePercent);
        if (_addEqualize == true){
            cmd.add("-equalize");
        }
        cmd.add("-crop");
        cmd.add(_tileSizeArgForConvert);
        cmd.add("-set");
        cmd.add("filename:tile");
        cmd.add(_renameArgForConvert);
        cmd.add("+repage");
        cmd.add("+adjoin");
        cmd.add(destTmpDir + File.separator + ZOOM + "-%[filename:tile].png");
        
        
        String result
                = _runCommandLineProcess.runCommandLineProcess(
                        cmd.toArray(new String[cmd.size()]));

        long duration = System.currentTimeMillis() - startTime;

        _log.log(Level.FINE, "convert output: {0}", result);

        _log.log(Level.INFO, "convert took {0} seconds", duration / 1000);

        _log.log(Level.FINE, "Deleting {0}",sourcePath);
        //delete the input png file
        File srcPng = new File(sourcePath);
        srcPng.delete();
    }
    
}
