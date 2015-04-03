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

package edu.ucsd.crbs.segmenter.handler.ccdb.model;

import edu.ucsd.crbs.segmenter.util.ZipUtil;
import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;

/**
 * Downloads model from CCDB
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ModelDownloaderImpl implements ModelDownloader {

    private String _url;
    private String _modelBaseDir;
    
    public ModelDownloaderImpl(final String url,final String modelBaseDir){
        _url = url;
        _modelBaseDir = modelBaseDir;
    }
    
    /**
     * Downloads model corresponding to <b>id</b> passed in to the directory
     * defined by <b>destinationDir</b> 
     * @param id Identifier for model to download
     * @param destinationDir Full path to download model to
     * @throws Exception if there was a problem downloading the model
     */
    @Override
    public File downloadModel(final String id,final String destinationDir) throws Exception {
        URL url = new URL(_url+"/CCDBSlashChmService/ChmModelDownloadServlet?id="+id);
        File destDir = new File(_modelBaseDir+File.separator+destinationDir);
        destDir.mkdirs();
        File destFile = new File(destDir.getAbsolutePath()+File.separator+"downloadedFile.zip");
        
        FileUtils.copyURLToFile(url,destFile, 10000, 10000);
        
        if (destFile.length() < 1){
            throw new Exception("Downloaded file is 0 bytes");
        }
        
        //unzip the file
        ZipUtil zUtil = new ZipUtil();
        zUtil.decompress(destFile,destDir);
        return destDir;
    }
}
