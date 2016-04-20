/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.App;
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
    
}
