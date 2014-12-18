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

package edu.ucsd.crbs.realtimeseg.util;

import edu.ucsd.crbs.realtimeseg.job.CHMCommandLineJob;
import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SimpleCHMImageProcessor implements ImageProcessor{

    private ExecutorService _executorService;
    private String _inputImageDir;
    private String _workingDir;
    private String _trainedModel;
    private String _binary;
    private String _matlabDir;
    
    public SimpleCHMImageProcessor(ExecutorService ex,final String inputImageDir,
            final String workingDir,final String trainedModel,
            final String binary,final String matlabDir){
        _executorService = ex;
        _inputImageDir = inputImageDir;
        _workingDir = workingDir;
        _trainedModel = trainedModel;
        _binary = binary;
        _matlabDir = matlabDir;
    }

    public void process(String image) {
        File checkForFile = new File(_inputImageDir+File.separator+image);
        if (checkForFile.exists() == false){
            return;
        }
        
        CHMCommandLineJob job = new CHMCommandLineJob(_inputImageDir+File.separator+image,
                _trainedModel,_binary,_matlabDir,_workingDir,"128x128");
        System.out.println("Processing job: "+image);
        _executorService.submit(job);
    }
}
