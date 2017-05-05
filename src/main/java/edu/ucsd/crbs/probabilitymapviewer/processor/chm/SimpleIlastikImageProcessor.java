/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.probabilitymapviewer.processor.chm;

import edu.ucsd.crbs.probabilitymapviewer.processor.ImageProcessor;
import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.job.IlastikCommandLineJob;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SimpleIlastikImageProcessor implements ImageProcessor{

     private static final Logger _log = Logger.getLogger(SimpleIlastikImageProcessor.class.getName());
    
    
    private String _inputImageDir;
    private String _workingDir;
    private String _project;
    private String _binary;
    private String _matlabDir;
    private String _colorsToZeroOut;
    private String _tileSize;
    
    public SimpleIlastikImageProcessor(final String inputImageDir,
            final String workingDir,final String project,
            final String binary,final String matlabDir,final String colorsToZeroOut,
            final String tileSize){
        _inputImageDir = inputImageDir;
        _workingDir = workingDir;
        _project = project;
        _binary = binary;
        _matlabDir = matlabDir;
        _colorsToZeroOut = colorsToZeroOut;
        _tileSize = tileSize+"x"+tileSize;
        _log.log(Level.INFO,"Image Processor colors to zero out: {0}",_colorsToZeroOut);
    }

     @Override
    public void process(String image) {
        File checkForFile = new File(_inputImageDir+File.separator+image);
        if (checkForFile.exists() == false){
            return;
        }
        
        IlastikCommandLineJob job = new IlastikCommandLineJob(_inputImageDir+File.separator+image,
                _project,_binary,_matlabDir,_workingDir,_tileSize,_colorsToZeroOut);
        _log.log(Level.INFO,"Submitting image {0} for processing and writing output to {1}",
                new Object[]{image,_workingDir});
        
        App.tilesToProcess.add(job);
    }
}