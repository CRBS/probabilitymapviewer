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

package edu.ucsd.crbs.segmenter.job;

import edu.ucsd.crbs.segmenter.util.RunCommandLineProcess;
import edu.ucsd.crbs.segmenter.util.RunCommandLineProcessImpl;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CHMCommandLineJobViaSGE implements Callable {

     private static final Logger _log = Logger.getLogger(CHMCommandLineJobViaSGE.class.getName());
    private String _inputImageName;
    private String _inputImage;
    private String _script;
    private String _outDir;
    private String _queue;
    private RunCommandLineProcess _runCommandLineProcess;
    
    public CHMCommandLineJobViaSGE(final String script,final String inputImageName,
            final String inputImage,final String outDir,final String queue){
        _inputImageName = inputImageName;
        _inputImage = inputImage;
        _script = script;
        _outDir = outDir;
        _queue = queue;
        _runCommandLineProcess = new RunCommandLineProcessImpl();
    }
    
    @Override
    public Object call() throws Exception {
        
        
        //invoke job with flags
        //sync should be a flag as to whether this call waits or not although if there
        //isnt a wait then we dont know how long it takes to run
        //qsub -sync y -b y -N (image name) -j y -o `pwd`/Mitochondria/0-r1_c1.png.out `pwd`/mito.sh /home/churas/src/leaflet/images6/0-r1_c1.png 0-r1_c1.png
        
        //delete the (image name).out file if its size is 0
        
        _runCommandLineProcess.setWorkingDirectory(_outDir);
        String result = null;
        _log.log(Level.INFO, "Running chm on {0}",_inputImageName);
        JobResult jobResult = new JobResult();
        
        try {
            String fixedName = "chm_"+_inputImageName.replaceAll("-", "");
            String outFilePath = _outDir+File.separator+_inputImageName+".out";
            long startTime = System.currentTimeMillis();
            result = _runCommandLineProcess.runCommandLineProcess("qsub","-V","-q",
                    _queue,
                    "-sync","y","-b","y","-N",fixedName,"-j","y","-o",outFilePath,
                    _script,
                    _inputImage,_inputImageName);
             long chmDuration = System.currentTimeMillis() - startTime;
            _log.log(Level.FINE,"chm output: {0}", result);
            jobResult.setRunTimeInMilliseconds(chmDuration);
             _log.log(Level.INFO,"{0} CHM & Convert Took: {1} seconds",
                     new Object[]{_inputImage,chmDuration/1000});
             
             File outFile = new File(outFilePath);
             if (outFile.exists() && outFile.length() == 0){
                 outFile.delete();
             }
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught exception trying to run chm {0}",
                    ex.getMessage());
        }
        return jobResult;
    }
}
