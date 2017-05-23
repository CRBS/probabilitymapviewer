/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * Converts dm4 image to slice aka bunch of tiled images
 *
 * @author churas
 */
public class Dm4ToSliceConverter implements SliceConverter {

    
    public static final String ZOOM = "0";
    public static final String TMP_SUFFIX = ".tmp";

    private static final Logger _log
            = Logger.getLogger(Dm4ToSliceConverter.class.getName());

    private Properties _props;
    private String _dm2mrc_cmd;
    private String _mrc2tif_cmd;
    private String _convert_cmd;
    private String _clip_cmd;
    private String _createchmimage_cmd = "createchmimage.py";
    private String _downsample_factor;
    private RunCommandLineProcess _runCommandLineProcess;
    private String _tileSizeArgForConvert;
    private String _renameArgForConvert;
    private boolean _addEqualize = false;
    private SliceIntensityDistributionFactory _sliceItensityFactory;

    /**
     * Constructor
     */
    public Dm4ToSliceConverter(Properties props,SliceIntensityDistributionFactory
            sliceItensityFactory) {
        _props = props;
        if (_props == null) {
            throw new NullPointerException("Properties passed in constructor "
                    + "is null");
        }
        
        _sliceItensityFactory = sliceItensityFactory;
                
        //need binary paths for convert, dm2mrc, mrc2tif
        _mrc2tif_cmd = _props.getProperty(App.MRC2TIF_ARG);
        if (_mrc2tif_cmd == null) {
            throw new NullPointerException(App.MRC2TIF_ARG
                    + " property is null");
        }

        _dm2mrc_cmd = _props.getProperty(App.DM2MRC_ARG);
        if (_dm2mrc_cmd == null) {
            throw new NullPointerException(App.DM2MRC_ARG
                    + " property is null");
        }

        _convert_cmd = _props.getProperty(App.CONVERT_ARG);
        if (_convert_cmd == null) {
            throw new NullPointerException(App.CONVERT_ARG
                    + " property is null");
        }
        
        _clip_cmd = _props.getProperty(App.CLIP_ARG);
        if (_clip_cmd == null) {
            throw new NullPointerException(App.CLIP_ARG
                    + " property is null");
        }
        
        _addEqualize = Boolean.parseBoolean(
                _props.getProperty(App.CONVERT_EQUALIZE_ARG,
                "false"));

        // need downsampling value
        try {
            int dsample = Integer.parseInt(_props.getProperty(App.DOWNSAMPLEFACTOR_ARG,
                                           "0"));
            if (dsample < 0){
                _log.log(Level.WARNING, "Negative downsample detected, using 0");
                dsample = 0;
            }
            
            _downsample_factor = Integer.toString(dsample);
        } catch (Exception ex) {
            //we'll just default to 0 after logging the failure here
            _downsample_factor = "0";
            _log.log(Level.WARNING, "Invalid downsample value of {0} received"
                    + " using value of {1}",
                    new Object []{_props.getProperty(App.DOWNSAMPLEFACTOR_ARG,
                                                     "0"),
                                  _downsample_factor});
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
    public String getDownsampleFactor() {
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

        String mrcfile = run_dm2mrc(sourcePath,
                destTmpDir.getAbsolutePath());

        String pngfile = run_mrc2tif(mrcfile, destTmpDir.getAbsolutePath());

        String tpngfile = pngfile + ".tmp.png";
        
        run_createchmimage(pngfile,tpngfile);
        
        //run convert with rescale to create tiles
        run_convert(tpngfile, destTmpDir.getAbsolutePath());

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
                    + sourcePath.replaceAll("^.*/","").replaceAll(".dm4$","") +"\n");
            fw.flush();
            fw.close();
        }catch(Exception ex){
            _log.log(Level.WARNING," Caught Exception: " + ex.getMessage());
        }
    }

    /**
     * Runs dm2mrc to convert the dm4 image to mrc
     *
     * @param sourcePath Full path to dm4 image
     * @param destTmpDir Full path output directory
     * @return Full path to created mrc file
     * @throws Exception If there is an error running the command.
     */
    private String run_dm2mrc(final String sourcePath, final String destTmpDir)
            throws Exception {

        long startTime = System.currentTimeMillis();

        String destMrc = destTmpDir + File.separator + "out.mrc";

        String result
                = _runCommandLineProcess.runCommandLineProcess(_dm2mrc_cmd,
                        sourcePath,
                        destMrc);
        
        long duration = System.currentTimeMillis() - startTime;
        _log.log(Level.FINE, "dm2mrc output: {0}", result);

        _log.log(Level.INFO, "Generating {0} via dm2mrc took {1} seconds", 
                new Object[]{destMrc,duration / 1000});

        return destMrc;
    }

    /**
     * Converts mrc to png file using mrc2tif
     *
     * @param sourcePath Path to mrc file
     * @param destTmpDir Destination directory
     * @return Path to resulting png file (named out.png)
     * @throws Exception If there is an error.
     */
    private String run_mrc2tif(final String sourcePath, final String destTmpDir)
            throws Exception {
        
        SliceIntensityDistribution sid = 
                _sliceItensityFactory.getSliceIntensityDistribution(sourcePath);
        
        if (sid == null){
            throw new Exception("Unable to get slice intensity distribution");
        }
        
        long startTime = System.currentTimeMillis();

        String destPng = destTmpDir + File.separator + "out.png";

        String result
                = _runCommandLineProcess.runCommandLineProcess(_mrc2tif_cmd,
                        "-p","-S",sid.getScalingLimitsAsString(),sourcePath, destPng);

        long duration = System.currentTimeMillis() - startTime;

        _log.log(Level.FINE, "mrc2tif output: {0}", result);

        _log.log(Level.INFO, "Generating {0} via mrc2tif took {1} seconds", 
                new Object[]{destPng,duration / 1000});

        _log.log(Level.FINE, "Deleting {0}",sourcePath);
        //delete the input png file
        File srcMrc = new File(sourcePath);
        srcMrc.delete();
        
        return destPng;
    }
    
    /**
     * Passes png file through createchmimage.py and performing 
     * downsample as well as --autocontrast
     *
     * @param sourcePath Path to png file
     * @param destPath Path to destination png file
     * @throws Exception If there is an error.
     */
    private void run_createchmimage(final String sourcePath,
            final String destPath)
            throws Exception {

        long startTime = System.currentTimeMillis();

        String result
                = _runCommandLineProcess.runCommandLineProcess(_createchmimage_cmd,
                        sourcePath, destPath,"--autocontrast",
                        "--gaussianblur", "--downsample",
                        _downsample_factor);

        long duration = System.currentTimeMillis() - startTime;

        _log.log(Level.FINE, "{0} output: {1}",
                new Object[]{_createchmimage_cmd,result});

        _log.log(Level.INFO, "Generating {0} via createchmimage.py took {1} seconds", 
                new Object[]{destPath,duration / 1000});

        _log.log(Level.FINE, "Deleting {0}",sourcePath);
        //delete the input png file
        File srcPng = new File(sourcePath);
        srcPng.delete();        
    }

    private void run_convert(final String sourcePath,
            final String destTmpDir) throws Exception {

        long startTime = System.currentTimeMillis();
                        
        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(_convert_cmd);
        cmd.add(sourcePath);
        if (_addEqualize == true){
            cmd.add("-set");
            cmd.add("colorspace");
            cmd.add("Gray");
            cmd.add("-separate");
            cmd.add("-average");
            cmd.add("-auto-level");
            cmd.add("-equalize");
            cmd.add("-gaussian-blur");
            cmd.add("5x2");
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
