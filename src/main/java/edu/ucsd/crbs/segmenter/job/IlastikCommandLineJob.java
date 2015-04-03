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

package edu.ucsd.crbs.segmenter.job;

import edu.ucsd.crbs.segmenter.util.RunCommandLineProcess;
import edu.ucsd.crbs.segmenter.util.RunCommandLineProcessImpl;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Runs CHM on image to generate a probability map
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class IlastikCommandLineJob implements Callable {

    
    private static final Logger _log = Logger.getLogger(IlastikCommandLineJob.class.getName());
    private String _inputImage;
    private String _project;
    private String _binary;
    private String _overlap = "0x0";
    private String _tileSize;
    private String _outDir;
    private String _matlabDir;
    private String _colorsToZeroOut;
    private RunCommandLineProcess _runCommandLineProcess;
    
    public IlastikCommandLineJob(final String inputImage, final String project,
            final String binary,final String matlabDir, final String outDir,final String tileSize,
            final String colorsToZeroOut){
        _inputImage = inputImage;
        _project = project;
        _binary = binary;
        _outDir = outDir;
        _tileSize = tileSize;
        _matlabDir = matlabDir;
        _colorsToZeroOut = colorsToZeroOut;
        _runCommandLineProcess = new RunCommandLineProcessImpl();
    }
    
    public void setRunCommandLineProcess(RunCommandLineProcess rclp){
        _runCommandLineProcess = rclp;
    }
    
    @Override
    public JobResult call() {
        _runCommandLineProcess.setWorkingDirectory(_outDir);
        String result = null;
        _log.log(Level.INFO, "Running Ilastik on {0}",_inputImage);
        JobResult jobResult = new JobResult();
        try {
            int slashPos = _inputImage.lastIndexOf('/');
            String fileName = _inputImage.substring(slashPos+1);
            
            File tempDir = new File(_outDir+File.separator+fileName+"dir");
            tempDir.mkdirs();
            
            long startTime = System.currentTimeMillis();
        
            _log.log(Level.INFO,"{0} {1} {2} {3} {4} {5}",new Object[]{_binary,
                    "--headless",
                    "--project="+_project,
                    "--output_format=png",
                    "--output_filename_format="+tempDir.getAbsolutePath()+File.separator+fileName,
                    _inputImage});
            
            result = _runCommandLineProcess.runCommandLineProcess(_binary,"--headless",
                    "--project="+_project,"--output_format=png",
                    "--output_filename_format="+tempDir.getAbsolutePath()
                            +File.separator+fileName,
                    _inputImage);
            
            long ilastikDuration = System.currentTimeMillis() - startTime;
            _log.log(Level.FINE,"Ilastik output: {0}", result);
        
            startTime = System.currentTimeMillis();
            result = _runCommandLineProcess.runCommandLineProcess("convert",
                    tempDir.getAbsolutePath()+File.separator+fileName,
                    "-negate","-channel",_colorsToZeroOut,"-threshold","100%",
                    _outDir+File.separator+fileName);
            _log.log(Level.FINE,"convert output: {0}", result);
            
             long convertDuration = System.currentTimeMillis() - startTime;
             
             jobResult.setRunTimeInMilliseconds(ilastikDuration+convertDuration);
             _log.log(Level.INFO,"{0} Ilastik Took: {1} seconds and convert took {2} seconds",
                     new Object[]{_inputImage,ilastikDuration/1000,convertDuration/1000});
             FileUtils.deleteDirectory(tempDir);
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught exception trying to run Ilastik {0}",
                    ex.getMessage());
        }
        return jobResult;
    }

}
