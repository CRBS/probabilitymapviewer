/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.util.RunCommandLineProcess;
import edu.ucsd.crbs.segmenter.util.RunCommandLineProcessImpl;
import java.io.File;
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
    private String _inputImage;
    private String _dm2mrc_cmd;
    private String _mrc2tif_cmd;
    private String _convert_cmd;
    private int _downsample_factor;
    private RunCommandLineProcess _runCommandLineProcess;
    private String _tileSizeArgForConvert;
    private String _renameArgForConvert;

    /**
     * Constructor
     */
    public Dm4ToSliceConverter(Properties props) {
        _props = props;
        if (_props == null) {
            throw new NullPointerException("Properties passed in constructor "
                    + "is null");
        }
        _inputImage = _props.getProperty(App.INPUT_IMAGE_ARG);
        if (_inputImage == null) {
            throw new NullPointerException(App.INPUT_IMAGE_ARG
                    + " property is null");
        }

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
        _renameArgForConvert = "\"r%[fx:page.y/" + tileSize + "]_c%[fx:page.x/"
                + tileSize + "]\"";

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

        _runCommandLineProcess
                .setWorkingDirectory(destTmpDir.getAbsolutePath());

        String mrcfile = run_dm2mrc(sourcePath,
                destTmpDir.getAbsolutePath());

        String pngfile = run_mrc2tif(mrcfile, destTmpDir.getAbsolutePath());

        //run convert with rescale to create tiles
        run_convert(pngfile, destTmpDir.getAbsolutePath());

        //rename temp directory to destPath directory
        if (destTmpDir.renameTo(new File(destPath)) == false) {
            throw new Exception("Unable to rename "
                    + destTmpDir.getAbsolutePath() + " to " + destPath);
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

        long startTime = System.currentTimeMillis();

        String destPng = destTmpDir + File.separator + "out.png";

        String result
                = _runCommandLineProcess.runCommandLineProcess(_mrc2tif_cmd,
                        "-p", sourcePath, destPng);

        long duration = System.currentTimeMillis() - startTime;

        _log.log(Level.FINE, "mrc2tif output: {0}", result);

        _log.log(Level.INFO, "Generating {0} via mrc2tif took {1} seconds", 
                new Object[]{destPng,duration / 1000});

        return destPng;
    }

    private void run_convert(final String sourcePath,
            final String destTmpDir) throws Exception {

        long startTime = System.currentTimeMillis();

        String destPng = destTmpDir + File.separator + "out.png";
        
        double dfactor = (1.0/(double)_downsample_factor)*100.0;
        
        String resizePercent = Long.toString(Math.round(dfactor)) + "%";

        _log.log(Level.FINE, "Resize set to : " + resizePercent);
        
        String result
                = _runCommandLineProcess.runCommandLineProcess(_convert_cmd,
                        sourcePath,
                        "-resize", resizePercent, "-crop",
                        _tileSizeArgForConvert, "-set", "filename:tile",
                        _renameArgForConvert, "+repage", "+adjoin",
                        destTmpDir + File.separator
                        + ZOOM + "-%[filename:tile].png");

        long duration = System.currentTimeMillis() - startTime;

        _log.log(Level.FINE, "convert output: {0}", result);

        _log.log(Level.INFO, "convert took {0} seconds", duration / 1000);
    }
}
