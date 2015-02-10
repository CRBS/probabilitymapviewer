/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ZipUtil {
    
    private static final Logger _log = Logger.getLogger(ZipUtil.class.getName());
    
    public void decompress(File sourceFile,File destDir) throws Exception {
        
        if (!sourceFile.exists()){
            throw new Exception(sourceFile+" file does not exist");
        }
        if (!sourceFile.isFile()){
            throw new Exception(sourceFile+" is not a file");
        }
        
        if (sourceFile.length() <= 0){
            throw new Exception(sourceFile+" size is 0");
        }
        ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile));

        ZipEntry ze = zis.getNextEntry();
        File curDir = null;
        while (ze != null) {
            if (!ze.isDirectory()){
                String curFileName = removeEverythingLeftofSlash(ze.getName());
                if (curFileName.startsWith("MODEL_") ||
                        curFileName.equals("param.mat")){
                    _log.log(Level.INFO, "Found file with name: {0}", ze.getName());
                    IOUtils.copy(zis, new FileOutputStream(new File(destDir.getAbsolutePath() + File.separator + curFileName)));
                }
            }
            zis.closeEntry();
            ze = zis.getNextEntry();
        }
        zis.close();
       
    }
    
    private String removeEverythingLeftofSlash(final String path){
        return path.replaceAll("^.*\\/","");
    }
    
    private String removeEverythingLeftOfFirstSlash(final String path){
        int slashPos = path.indexOf(File.separator);
        if (slashPos == -1){
            return path;
        }
        if (slashPos == path.length()){
            return path;
        }
        
        return path.substring(slashPos+1);
    }
}
