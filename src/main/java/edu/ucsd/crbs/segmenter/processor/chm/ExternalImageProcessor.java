/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.processor.chm;

import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.job.ExternalCommandLineJob;
import edu.ucsd.crbs.segmenter.processor.ImageProcessor;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author churas
 */
public class ExternalImageProcessor implements ImageProcessor {
 
    private static final Logger _log = Logger.getLogger(ExternalImageProcessor.class.getName());

    private String _inputImageDir;
    private String _workingDir;
    private String _binary;
    private String _colorsToZeroOut;
    private String _tileSize;
    private String _analyzingTile;
    private String _convert;
    
    public ExternalImageProcessor(final String inputImageDir,
            final String workingDir,
            final String binary,final String colorsToZeroOut,
            final String convert,
            final String tileSize,
            final String analyzingTile){
        _inputImageDir = inputImageDir;
        _workingDir = workingDir;
        _binary = binary;
        _colorsToZeroOut = colorsToZeroOut;
        _tileSize = tileSize+"x"+tileSize;
        _analyzingTile = analyzingTile;
        _log.log(Level.INFO,"Image Processor colors to zero out: {0}",_colorsToZeroOut);
    }
    
    @Override
    public void process(String image) {
        String fileCheckPath = _inputImageDir+File.separator+image;
        String workingDirPath = _workingDir;

        
        if (App.latestSlice != null && App.latestSlice != ""){
            workingDirPath = _workingDir+File.separator+App.latestSlice;
        }
        
        File checkForFile = new File(fileCheckPath);
        if (checkForFile.exists() == false){
            return;
        }
        
        ExternalCommandLineJob job = new ExternalCommandLineJob(fileCheckPath,
                _binary,workingDirPath,_tileSize,_colorsToZeroOut,
        _analyzingTile);
        _log.log(Level.INFO,"Submitting image {0} for processing and writing "
                + "output to {1}",
                new Object[]{image,workingDirPath});
        
        App.tilesToProcess.add(job);
    }
}
