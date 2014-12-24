/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this realtime-segmentation for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this realtime-segmentation into commercial products
 * or use for commercial purposes should contact the Technology Transfer Office, 
 * University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, 
 * La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, 
 * E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS realtime-segmentation, EVEN IF THE UNIVERSITY 
 * OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE realtime-segmentation PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE realtime-segmentation WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */

package edu.ucsd.crbs.realtimeseg.job;

import edu.ucsd.crbs.realtimeseg.util.RunCommandLineProcess;
import edu.ucsd.crbs.realtimeseg.util.RunCommandLineProcessImpl;
import java.io.File;
import org.apache.commons.io.FileUtils;

/**
 * Runs CHM on image to generate a probability map
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CHMCommandLineJob implements Runnable {

    private String _inputImage;
    private String _trainedModel;
    private String _binary;
    private String _overlap = "0x0";
    private String _tileSize;
    private String _outDir;
    private String _matlabDir;
    private String _colorsToZeroOut;
    
    public CHMCommandLineJob(final String inputImage, final String trainedModel,
            final String binary,final String matlabDir, final String outDir,final String tileSize,
            final String colorsToZeroOut){
        _inputImage = inputImage;
        _trainedModel = trainedModel;
        _binary = binary;
        _outDir = outDir;
        _tileSize = tileSize;
        _matlabDir = matlabDir;
        _colorsToZeroOut = colorsToZeroOut;
    }
    
    @Override
    public void run() {
        RunCommandLineProcess rclp = new RunCommandLineProcessImpl();
        rclp.setWorkingDirectory(_outDir);
        String result = null;
        System.out.println("Running chm on "+_inputImage);
        try {
            int slashPos = _inputImage.lastIndexOf('/');
            String fileName = _inputImage.substring(slashPos+1);
            
            File tempDir = new File(_outDir+File.separator+fileName+"dir");
            tempDir.mkdirs();
            
            long startTime = System.currentTimeMillis();
        
            result = rclp.runCommandLineProcess(_binary,_inputImage,tempDir.getAbsolutePath(),
                    "-b",_tileSize,"-o",_overlap,"-t","1,1","-m",_trainedModel,"-M",
                    _matlabDir);
            long chmDuration = System.currentTimeMillis() - startTime;
            System.out.println(result);
        
        
            startTime = System.currentTimeMillis();
            result = rclp.runCommandLineProcess("convert",
                    tempDir.getAbsolutePath()+File.separator+fileName,
                    "-threshold","30%","-transparent","black","-alpha","set",
                    "-channel","A",
                    "-channel",_colorsToZeroOut,"-evaluate","set","0",
                    _outDir+File.separator+fileName);
             System.out.println(result);
             long convertDuration = System.currentTimeMillis() - startTime;
             System.out.println(_inputImage+"  CHM Took: "+chmDuration/1000+" seconds and convert took "+convertDuration/1000+" seconds");
             FileUtils.deleteDirectory(tempDir);
        }
        catch(Exception ex){
            System.err.println("Caught exception trying to run chm: "+ex.getMessage());
        }
    }

}
