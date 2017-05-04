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

package edu.ucsd.crbs.probabilitymapviewer.processor;

import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;
import edu.ucsd.crbs.probabilitymapviewer.processor.chm.ExternalImageProcessor;
import edu.ucsd.crbs.probabilitymapviewer.processor.chm.SGECHMImageProcessor;
import edu.ucsd.crbs.probabilitymapviewer.processor.chm.SGEIlastikImageProcessor;
import edu.ucsd.crbs.probabilitymapviewer.processor.chm.SimpleCHMImageProcessor;
import edu.ucsd.crbs.probabilitymapviewer.processor.chm.SimpleIlastikImageProcessor;
import java.io.File;
import java.util.Properties;

/**
 * Creates ImageProcessor objects
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ImageProcessorFactory {

    public static final String CHM_TEST_SH = "CHM_test.sh";
    public static final String RUN_ILASTIK_SH = "run_ilastik.sh";
    
    private Properties _props;
    
    public ImageProcessorFactory(Properties props){
        _props = props;
    }
    
    public ImageProcessor getImageProcessor(CustomLayer layer){
        String workingDir =  _props.getProperty(App.LAYER_HANDLER_BASE_DIR)
                +File.separator+layer.getVarName();
        return getExternalImageProcessor(workingDir, layer);
    }
    
    private ImageProcessor getExternalImageProcessor(final String workingDir,
            CustomLayer layer){
        return new ExternalImageProcessor(
                _props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG),
                   workingDir,layer.getScript(),layer.getConvertColor(),
                _props.getProperty(App.CONVERT_ARG),
                   _props.getProperty(App.TILE_SIZE_ARG),
                getAnalyzingTile(layer));
    }
    
    private ImageProcessor getSGECHMImageProcessor(final String workingDir,
            CustomLayer layer){
        
        return new SGECHMImageProcessor(_props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG),
                    workingDir,
                    layer.getScript(),
                    _props.getProperty(App.CHM_BIN_ARG)+File.separator+CHM_TEST_SH,
                    _props.getProperty(App.MATLAB_ARG),layer.getConvertColor(),
                    _props.getProperty(App.TILE_SIZE_ARG),
                    _props.getProperty(App.SGE_CHM_QUEUE_ARG),
                    _props.getProperty(App.CONVERT_ARG),
                    getAnalyzingTile(layer));
    }
    
    private ImageProcessor getSimpleCHMImageProcessor(final String workingDir,
            CustomLayer layer){
        return new SimpleCHMImageProcessor(_props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG),
                    workingDir,
                    layer.getScript(),
                    _props.getProperty(App.CHM_BIN_ARG)+File.separator+CHM_TEST_SH,
                    _props.getProperty(App.MATLAB_ARG),layer.getConvertColor(),
                    _props.getProperty(App.TILE_SIZE_ARG),
                    getAnalyzingTile(layer));
    }
    
    private ImageProcessor getSimpleIlastikImageProcessor(final String workingDir,
            CustomLayer layer){
        /** @TODO Add support for analyzing tile */
        return new SimpleIlastikImageProcessor(_props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG),
                    workingDir,
                    layer.getScript(),
                    _props.getProperty(App.ILASTIK_ARG)+File.separator+RUN_ILASTIK_SH,
                    _props.getProperty(App.MATLAB_ARG),layer.getConvertColor(),
                    _props.getProperty(App.TILE_SIZE_ARG));
        
        
    }
    
    private ImageProcessor getSGEIlastikImageProcessor(final String workingDir,
            CustomLayer layer){
        /** @TODO Add support for analyzing tile */
        return new SGEIlastikImageProcessor(_props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG),
                    workingDir,
                    layer.getScript(),
                    _props.getProperty(App.ILASTIK_ARG)+File.separator+RUN_ILASTIK_SH,
                    layer.getConvertColor(),
                    _props.getProperty(App.SGE_ILASTIK_QUEUE_ARG),
                    _props.getProperty(App.CONVERT_ARG));
    }
    
    private String getAnalyzingTile(CustomLayer layer){
        String analyzingTile = null;
        if (_props.getProperty(App.DISABLE_ANALYZING_TILE_ARG,"true").equals("false")){
            analyzingTile = _props.getProperty(App.DIR_ARG)+File.separator
                            +layer.getAnalyzingTile();
        }
        return analyzingTile;
    }
}
