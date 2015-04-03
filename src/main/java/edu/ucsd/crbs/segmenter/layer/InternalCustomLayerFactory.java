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

package edu.ucsd.crbs.segmenter.layer;

import edu.ucsd.crbs.segmenter.App;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class InternalCustomLayerFactory {

    public List<CustomLayer> getLayers(Properties props) throws Exception {
        ArrayList<CustomLayer> layers = new ArrayList<CustomLayer>();
        CustomLayer layer = new CustomLayer(getMitoTrainedModelDir(props.getProperty(App.LAYER_MODEL_BASE_DIR)),
        "Mitochondria","green","chm");
        layers.add(layer);
        
        layer = new CustomLayer(getLysoTrainedModelDir(props.getProperty(App.LAYER_MODEL_BASE_DIR)),
        "Lysosome","blue","chm");
        layers.add(layer);
        
        return layers;
    }
    
    private String getMitoTrainedModelDir(final String workingDir) throws Exception {
        
        File mitoModelDir = new File(workingDir+File.separator+"mitomodel");
        mitoModelDir.mkdirs();
        String mito = "mito";
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/MODEL_level0_stage1.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"MODEL_level0_stage1.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/MODEL_level0_stage2.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"MODEL_level0_stage2.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/MODEL_level1_stage1.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"MODEL_level1_stage1.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+mito+"/param.mat"),
                new File(mitoModelDir.getAbsolutePath()+File.separator+"param.mat"));
        
        return mitoModelDir.getAbsolutePath();
    }
    
    private String getLysoTrainedModelDir(final String workingDir) throws Exception {
        
        File lysoModelDir = new File(workingDir+File.separator+"lysomodel");
        lysoModelDir.mkdirs();
        String lyso = "lyso";
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+lyso+"/MODEL_level0_stage1.mat"),
                new File(lysoModelDir.getAbsolutePath()+File.separator+"MODEL_level0_stage1.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+lyso+"/MODEL_level0_stage2.mat"),
                new File(lysoModelDir.getAbsolutePath()+File.separator+"MODEL_level0_stage2.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+lyso+"/MODEL_level1_stage1.mat"),
                new File(lysoModelDir.getAbsolutePath()+File.separator+"MODEL_level1_stage1.mat"));
        
        FileUtils.copyInputStreamToFile(Class.class.getResourceAsStream("/"+lyso+"/param.mat"),
                new File(lysoModelDir.getAbsolutePath()+File.separator+"param.mat"));
        
        return lysoModelDir.getAbsolutePath();
    }
    
}
