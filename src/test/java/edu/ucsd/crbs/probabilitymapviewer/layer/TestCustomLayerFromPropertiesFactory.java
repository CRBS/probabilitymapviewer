/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.layer;

import edu.ucsd.crbs.probabilitymapviewer.App;
import java.util.Properties;

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

import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayerFromPropertiesFactory;
import java.util.List;
import org.junit.Assert;

/**
 *
 * @author churas
 */
@RunWith(JUnit4.class)
public class TestCustomLayerFromPropertiesFactory {
    
    public TestCustomLayerFromPropertiesFactory() {
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
    public void testGetCustomLayersNullProperties() throws Exception {
        CustomLayerFromPropertiesFactory clf = new CustomLayerFromPropertiesFactory();
        try {
            clf.getCustomLayers(null);
            Assert.fail("Expected IllegalArgumentException");
        }
        catch(IllegalArgumentException iae)  {
             assertTrue("Message is wrong",
                        iae.getMessage().equals("props is null"));
        }
    }
    
    @Test
    public void testGetCustomLayersSingleWithThreeArgs() throws Exception {
        Properties props = new Properties();
        props.put(App.CUSTOM_ARG,"name,color,script");
        CustomLayerFromPropertiesFactory clf = new CustomLayerFromPropertiesFactory();
        List<CustomLayer> clist = clf.getCustomLayers(props);
        assertTrue(clist.size() == 1);
        CustomLayer cl = clist.get(0);
        assertTrue(cl.getName().equals("name"));
        assertTrue(cl.getColor().equals("color"));
        assertTrue(cl.getScript().equals("script"));
        assertTrue(cl.getOptArgs().equals(""));
    }
    
    @Test
    public void testGetCustomLayersSingleWithFourArgs() throws Exception {
        Properties props = new Properties();
        props.put(App.CUSTOM_ARG,"name,color,script,optargs");
        CustomLayerFromPropertiesFactory clf = new CustomLayerFromPropertiesFactory();
        List<CustomLayer> clist = clf.getCustomLayers(props);
        assertTrue(clist.size() == 1);
        CustomLayer cl = clist.get(0);
        assertTrue(cl.getName().equals("name"));
        assertTrue(cl.getColor().equals("color"));
        assertTrue(cl.getScript().equals("script"));
        assertTrue(cl.getOptArgs().equals("optargs"));
    }
    
    @Test
    public void testGetCustomLayersSingleWithFourArgsAndQuotes() throws Exception {
        Properties props = new Properties();
        props.put(App.CUSTOM_ARG,"name,color,script,\"1,2,3\"");
        CustomLayerFromPropertiesFactory clf = new CustomLayerFromPropertiesFactory();
        List<CustomLayer> clist = clf.getCustomLayers(props);
        assertTrue(clist.size() == 1);
        CustomLayer cl = clist.get(0);
        assertTrue(cl.getName().equals("name"));
        assertTrue(cl.getColor().equals("color"));
        assertTrue(cl.getScript().equals("script"));
        assertTrue(cl.getOptArgs().equals("1,2,3"));
    }
    

}
