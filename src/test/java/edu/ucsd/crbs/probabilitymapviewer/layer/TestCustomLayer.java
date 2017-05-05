/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.layer;


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

/**
 *
 * @author churas
 */
@RunWith(JUnit4.class)
public class TestCustomLayer {
    
    public TestCustomLayer() {
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
    public void testConstructor() throws Exception {
        CustomLayer cl = new CustomLayer("name", "color","script", null);
        assertTrue(cl.getName().equals("name"));
        assertTrue(cl.getColor().equals("color"));
        assertTrue(cl.getScript().equals("script"));
        assertTrue(cl.getOptArgs().equals(""));
        assertTrue(cl.getVarName().equals("name"));
        assertTrue(cl.getImageName().equals("{z}-r{y}_c{x}.png"));
        assertTrue(cl.getImagesPath().equals("name/" + cl.getImageName()));
        
        cl = new CustomLayer(null, "color","script", null);
        assertTrue(cl.getName() == null);
        assertTrue(cl.getColor().equals("color"));
        assertTrue(cl.getScript().equals("script"));
        assertTrue(cl.getOptArgs().equals(""));
        assertTrue(cl.getVarName().equals("uhoh"));
        assertTrue(cl.getImagesPath().equals("uhoh/" + cl.getImageName()));
        
        cl = new CustomLayer(null, null,"script", "some arg");
        assertTrue(cl.getOptArgs().equals("some arg"));
        assertTrue(cl.getColor() == null);
    }
    
    @Test
    public void testGetBackgroundColorCSS(){
        CustomLayer cl = new CustomLayer(null, null, null, null);
        assertTrue(cl.getBackgroundColorCSS().equals(""));
        
        cl = new CustomLayer(null, "yellow", null, null);
        assertTrue(cl.getBackgroundColorCSS().equals("background-color: darkgray;"));
        
        cl = new CustomLayer(null, "YELLOW", null, null);
        assertTrue(cl.getBackgroundColorCSS().equals("background-color: darkgray;"));
    }
    
    public static String getTileNameForColor(final String color){
        return "analyzing_" + color + "_50opac.png";
    }
    
    @Test
    public void testGetAnalyzingTile(){
         CustomLayer cl = new CustomLayer(null, null, null, null);
         assertTrue(cl.getAnalyzingTile().equals("analyzing.png"));

        String[] colorlist = { "green", "blue", "yellow", "magenta", 
                               "red", "cyan" };
        for (String s : colorlist){
            cl = new CustomLayer(null, s, null, null);
            assertTrue("Checking " + s + " color", 
                       cl.getAnalyzingTile().equals(TestCustomLayer.getTileNameForColor(s)));
        
            cl = new CustomLayer(null, s.toUpperCase(), null, null);
            assertTrue("Checking " + s.toUpperCase() + " color",
                       cl.getAnalyzingTile().equals(TestCustomLayer.getTileNameForColor(s)));
        }
        
        cl = new CustomLayer(null, "other", null, null);
        assertTrue("Checking other color", 
                   cl.getAnalyzingTile().equals(TestCustomLayer.getTileNameForColor("red")));
    }

    @Test
    public void testGetConvertColor(){
        CustomLayer cl = new CustomLayer(null, null, null, null);
        assertTrue(cl.getConvertColor().equals("Red,Blue"));
        
        cl = new CustomLayer(null, "green", null, null);
        assertTrue(cl.getConvertColor().equals("Red,Blue"));
        
        cl = new CustomLayer(null, "blue", null, null);
        assertTrue(cl.getConvertColor().equals("Red,Green"));
        
        cl = new CustomLayer(null, "yellow", null, null);
        assertTrue(cl.getConvertColor().equals("Blue"));
        
        cl = new CustomLayer(null, "magenta", null, null);
        assertTrue(cl.getConvertColor().equals("Green"));
        
        cl = new CustomLayer(null, "red", null, null);
        assertTrue(cl.getConvertColor().equals("Blue,Green"));
        
        cl = new CustomLayer(null, "cyan", null, null);
        assertTrue(cl.getConvertColor().equals("Red"));
        
        cl = new CustomLayer(null, "other", null, null);
        assertTrue(cl.getConvertColor().equals("Red"));
    }

}
