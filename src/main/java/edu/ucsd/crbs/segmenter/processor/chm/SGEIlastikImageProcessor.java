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
import edu.ucsd.crbs.segmenter.job.IlastikCommandLineJobViaSGE;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SGEIlastikImageProcessor implements ImageProcessor,StringReplacer {
    
    public static final String OUTPUT_DIR_TOKEN = "@@OUTPUT_DIR@@";
    public static final String ILASTIK_BINARY_TOKEN = "@@ILASTIK_BINARY@@";
    public static final String PROJECT_TOKEN = "@@PROJECT@@";
    public static final String CONVERT_BINARY_TOKEN = "@@CONVERT_BINARY@@";
    public static final String COLORS_TO_ZERO_OUT_TOKEN = "@@COLORS_TO_ZERO_OUT@@";
    
    
    private static final Logger _log = Logger.getLogger(SGEIlastikImageProcessor.class.getName());
    
    private String _inputImageDir;
    private String _workingDir;
    private String _project;
    private String _binary;
    private String _colorsToZeroOut;
    private String _script;
    private String _queue;
    private String _convert;
    
     public SGEIlastikImageProcessor(final String inputImageDir,
            final String workingDir,final String project,
            final String binary,final String colorsToZeroOut,
            final String queue,
            final String convert){
        _inputImageDir = inputImageDir;
        _workingDir = workingDir;
        _project = project;
        _binary = binary;
        _colorsToZeroOut = colorsToZeroOut;
        _queue = queue;
        _convert = convert;
        _log.log(Level.INFO,"Image Processor colors to zero out: {0}",_colorsToZeroOut);
        createCommandLineScript();
    }

    @Override
    public String replace(String line) {
        if (line == null){
            return null;
        }
        return line.replaceAll(OUTPUT_DIR_TOKEN, _workingDir)
                   .replaceAll(ILASTIK_BINARY_TOKEN, _binary)
                   .replaceAll(PROJECT_TOKEN, _project)
                   .replaceAll(CONVERT_BINARY_TOKEN, _convert)
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
            _script = _workingDir + File.separator + "ilastikviasge.sh";
            scriptWriter.writeResourceToScript("/ilastikviasge.sh.template", _script, 
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
        File checkForFile = new File(_inputImageDir+File.separator+image);
        if (checkForFile.exists() == false){
            return;
        }
        IlastikCommandLineJobViaSGE job = new IlastikCommandLineJobViaSGE(_script,image,
                checkForFile.getAbsolutePath(),_workingDir,_queue);
        
        _log.log(Level.INFO,"Submitting image {0} for processing and writing output to {1}",
                new Object[]{image,_workingDir});
        
        App.tilesToProcess.add(job);
        
    }


    
    
    
}
