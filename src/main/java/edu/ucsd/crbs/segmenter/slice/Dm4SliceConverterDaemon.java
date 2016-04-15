/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.App;
import java.io.File;
import java.io.FileFilter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.filefilter.FileFileFilter;

/**
 *
 * @author churas
 */
public class Dm4SliceConverterDaemon implements SliceConverterDaemon {

    
    
    private static final Logger _log = 
            Logger.getLogger(Dm4SliceConverterDaemon.class.getName());

    
    private Properties _props;
    private File _dirToWatch;
    private SliceConverter _sliceConverter;
    private boolean _shutdown = false;
    private File _secondYoungestFile = null;
    private String _destDir = null;
    
    public Dm4SliceConverterDaemon(Properties props, SliceConverter converter) {
        _props = props;
        
        //need to set directory to watch,
        _dirToWatch = new File(_props.getProperty(App.INPUT_IMAGE_ARG));
        if (_dirToWatch == null){
            throw new NullPointerException(App.INPUT_IMAGE_ARG 
                    + " property is null");
        }
        _destDir = props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG);
        if (_destDir == null){
            throw new NullPointerException(App.ADJUSTED_INPUT_IMAGE_ARG 
                    + " property"
                    + "is null");
        }
        _sliceConverter = converter;
    }

    @Override
    public String getDestinationDirectory() {
        return _destDir;
    }
    
    /**
     * Tells this object to shutdown
     */
    @Override
    public void shutdown() {
        _log.log(Level.INFO, "Received shutdown notification");
        _shutdown = true;
    }
    
    @Override
    public void run() {        
        _log.log(Level.INFO," Dm4SliceConverterDaemon, entering run loop...");
        while(_shutdown == false){
            File dm4File = getSecondNewestDm4File();
            if (dm4File != null){
                if (_secondYoungestFile !=null){
                    if (dm4File.getName().equals(_secondYoungestFile.getName())){
                        threadSleep();
                        continue;
                    }
                    if (dm4File.lastModified() 
                        < _secondYoungestFile.lastModified()){
                        threadSleep();
                        continue;
                    }
                }

                _secondYoungestFile = dm4File;

                _log.log(Level.INFO, "Found file to convert: "
                        + dm4File.getAbsolutePath());

                String dest = genDestinationPath(dm4File);
                try {
                    _sliceConverter.convert(dm4File.getAbsolutePath(), dest);
                } catch (Exception ex) {
                    _log.log(Level.WARNING, "Caught exception attempting to "
                            + "convert file: " + dm4File.getAbsolutePath()
                            + " to " + dest);
                }
            }
            threadSleep();
        }
        _log.log(Level.INFO, "Shutting down...");
    }
    
    private void threadSleep() {
        _log.log(Level.INFO, "Sleeping 10 seconds.");
        Thread.yield();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
            // do nothing
            _log.log(Level.FINEST, "Received InterruptedException");

        }

    }
    
    private String genDestinationPath(File dm4File){
        Dm4SliceFile sliceFile = new Dm4SliceFile(dm4File.getAbsolutePath());
        return _destDir + File.separator + SliceDir.SLICE_PREFIX 
                + sliceFile.getSliceName();
    }
    
    private File getSecondNewestDm4File(){
        
        long youngestAge = -1;
        File youngestFile = null;
        File secondYoungest = null;
        
        File[] files = _dirToWatch.listFiles((FileFilter)FileFileFilter.FILE);
        for (int i = 0; i < files.length; i++){
            if (!files[i].getName().endsWith(Dm4SliceFile.DM4_EXTENSION)){
                continue;
            }
            _log.log(Level.FINEST, "Found dm4 file: " + files[i].getName());
            if (youngestAge == -1){
                youngestAge = files[i].lastModified();
                youngestFile = files[i];
                continue;
            }
            if (files[i].lastModified() > youngestAge){
                _log.log(Level.INFO, "Current Second youngest: " 
                        + files[i].getName());

                secondYoungest = youngestFile;
                
                youngestAge = files[i].lastModified();
                youngestFile = files[i];
            }
        }
        return secondYoungest;
    }
    
}
