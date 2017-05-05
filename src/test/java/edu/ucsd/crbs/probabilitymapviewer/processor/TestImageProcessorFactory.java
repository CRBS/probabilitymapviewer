/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.processor;


import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.layer.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import org.junit.Assert;

import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;
import edu.ucsd.crbs.probabilitymapviewer.processor.ImageProcessorFactory;
import edu.ucsd.crbs.probabilitymapviewer.processor.chm.ExternalImageProcessor;
import java.util.Properties;

/**
 *
 * @author churas
 */
@RunWith(JUnit4.class)
public class TestImageProcessorFactory {
    
    public TestImageProcessorFactory() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGetImageProcessorSuccess() throws Exception {
        Properties props = new Properties();
        props.put(App.LAYER_HANDLER_BASE_DIR, "basedir");
        props.put(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        props.put(App.CONVERT_ARG, "convert");
        props.put(App.TILE_SIZE_ARG, "128");
        props.put(App.DISABLE_ANALYZING_TILE_ARG, "true");
        props.put(App.DIR_ARG, "dir");
        
        CustomLayer cl = new CustomLayer("foo", "red", "script", null);
        ImageProcessorFactory imf = new ImageProcessorFactory(props);
        ImageProcessor ip = imf.getImageProcessor(cl);
        assertTrue(ip != null);
        assertTrue(ip instanceof ExternalImageProcessor);
        
        // try again with disable analyzing tile arg false
        props.put(App.DISABLE_ANALYZING_TILE_ARG, "false");
        imf = new ImageProcessorFactory(props);
        ip = imf.getImageProcessor(cl);
        assertTrue(ip != null);
        assertTrue(ip instanceof ExternalImageProcessor);
        
    }

}
