/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this segmenter for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this segmenter into commercial products
 * or use for commercial purposes should contact the Technology Transfer Office, 
 * University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, 
 * La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, 
 * E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS segmenter, EVEN IF THE UNIVERSITY 
 * OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE segmenter PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE segmenter WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */

package edu.ucsd.crbs.segmenter.processor.chm;

import edu.ucsd.crbs.segmenter.processor.ImageProcessor;
import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.io.ResourceToFile;
import edu.ucsd.crbs.segmenter.io.ResourceToFileImpl;
import edu.ucsd.crbs.segmenter.io.StringReplacer;
import edu.ucsd.crbs.segmenter.job.CHMCommandLineJobViaSGE;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SGECHMImageProcessor implements ImageProcessor,StringReplacer {
    
    public static final String CHM_BINARY_TOKEN = "@@CHM_BINARY@@";
    public static final String TILE_SIZE_TOKEN = "@@TILE_SIZE@@";
    public static final String OVERLAP_TOKEN = "@@OVERLAP@@";
    public static final String TRAINED_MODEL_TOKEN = "@@TRAINED_MODEL@@";
    public static final String MATLAB_DIR_TOKEN = "@@MATLAB_DIR@@";
    public static final String CONVERT_BINARY_TOKEN = "@@CONVERT_BINARY@@";
    public static final String COLORS_TO_ZERO_OUT_TOKEN = "@@COLORS_TO_ZERO_OUT@@";
    public static final String ANALYZING_TILE_TOKEN = "@@ANALYZING_TILE@@";
    
    
    private static final Logger _log = Logger.getLogger(SGECHMImageProcessor.class.getName());
    
    private String _inputImageDir;
    private String _workingDir;
    private String _trainedModel;
    private String _binary;
    private String _matlabDir;
    private String _colorsToZeroOut;
    private String _tileSize;
    private String _script;
    private String _queue;
    private String _convert;
    private String _analyzingTile;
    
     public SGECHMImageProcessor(final String inputImageDir,
            final String workingDir,final String trainedModel,
            final String binary,final String matlabDir,final String colorsToZeroOut,
            final String tileSize,
            final String queue,
            final String convert,
            final String analyzingTile){
        _inputImageDir = inputImageDir;
        _workingDir = workingDir;
        _trainedModel = trainedModel;
        _binary = binary;
        _matlabDir = matlabDir;
        _colorsToZeroOut = colorsToZeroOut;
        _tileSize = tileSize+"x"+tileSize;
        _queue = queue;
        _convert = convert;
        _analyzingTile = analyzingTile;
        _log.log(Level.INFO,"Image Processor colors to zero out: {0}",_colorsToZeroOut);
        createCommandLineScript();
    }

    @Override
    public String replace(String line) {
        if (line == null){
            return null;
        }
        return line.replaceAll(CHM_BINARY_TOKEN, _binary)
                   .replaceAll(TILE_SIZE_TOKEN, _tileSize)
                   .replaceAll(OVERLAP_TOKEN, "0x0")
                   .replaceAll(TRAINED_MODEL_TOKEN, _trainedModel)
                   .replaceAll(MATLAB_DIR_TOKEN, _matlabDir)
                   .replaceAll(CONVERT_BINARY_TOKEN, _convert)
                   .replaceAll(ANALYZING_TILE_TOKEN,_analyzingTile)
                   .replaceAll(COLORS_TO_ZERO_OUT_TOKEN, _colorsToZeroOut);
    }
    
    private void createCommandLineScript()  {
        try {
            File workingDirFile = new File(_workingDir);
            if (!workingDirFile.exists()){
                if (workingDirFile.mkdirs() != true){
                    throw new Exception("Unable to create directory: "
                            +workingDirFile.getAbsolutePath());
                }
            }
            ResourceToFile scriptWriter = new ResourceToFileImpl();
            _script = _workingDir + File.separator + "chmviasge.sh";
            scriptWriter.writeResourceToScript("/chmviasge.sh.template", _script, 
                    this);
            File scriptFile = new File(_script);
            scriptFile.setExecutable(true);
        }
        catch(Exception ex){
            _log.log(Level.WARNING,
                    "Caught exception trying to create {0} file : {1}",
                    new Object[]{_script,ex.getMessage()});
        }
    }
    
     
    @Override
    public void process(String image) {
        
        /** @TODO This path adjustment is redundant in SimpleCHM so it should be
         * moved into its own class
         */
        String fileCheckPath = _inputImageDir+File.separator+image;
        String workingDirPath = _workingDir;

        
        if (App.latestSlice != null && App.latestSlice != ""){
            workingDirPath = _workingDir+File.separator+App.latestSlice;
        }
        
        File checkForFile = new File(fileCheckPath);
        if (checkForFile.exists() == false){
            return;
        }
        CHMCommandLineJobViaSGE job = new CHMCommandLineJobViaSGE(_script,image,
                checkForFile.getAbsolutePath(),workingDirPath,_queue);
        
        _log.log(Level.INFO,"Submitting image {0} for processing and writing output to {1}",
                new Object[]{image,workingDirPath});
        
        App.tilesToProcess.add(job);
        
    }


    
    
    
}
