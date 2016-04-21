/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.App;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author churas
 */
public class TestDm4SliceConverterDaemon {
    
    public TestDm4SliceConverterDaemon() {
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
    public void testConstructorNullProps(){
        try {
            new Dm4SliceConverterDaemon(null, null);
            fail("Expected exception");
        } catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("props is null"));
        }
    }
    
    @Test
    public void testConstructorInputImageArgIsNull(){
        try {
            Properties props = new Properties();
            new Dm4SliceConverterDaemon(props, null);
            fail("Expected exception");
        } catch(NullPointerException npe){
            assertTrue(npe.getMessage(),
                    npe.getMessage().equals(App.INPUT_IMAGE_ARG 
                    + " property is null"));
        }
    }
    
    @Test
    public void testConstructorAdjustedInputImageArgIsNull(){
        try {
            Properties props = new Properties();
            props.setProperty(App.INPUT_IMAGE_ARG,"");
            
            new Dm4SliceConverterDaemon(props, null);
            fail("Expected exception");
        } catch(NullPointerException npe){
            assertTrue(npe.getMessage(),
                    npe.getMessage().equals(App.ADJUSTED_INPUT_IMAGE_ARG 
                    + " property is null"));
        }
    }
    
    @Test
    public void testGetDestinationDirectory(){
            Properties props = new Properties();
            props.setProperty(App.INPUT_IMAGE_ARG,"");
            props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG,"hello");
            Dm4SliceConverterDaemon daemon = 
                    new Dm4SliceConverterDaemon(props, null);
            assertTrue(daemon.getDestinationDirectory().equals("hello"));
    }
    
    // test getSecondYoungestDm4File No files
    
    // test getSecondYoungestDm4File one file
    
    //test getSecondYoungestDm4File two files
    
    //test getSecondYoungestDm4File three files
    
    //test getSecondYoungestDm4File four files
    
    
}
