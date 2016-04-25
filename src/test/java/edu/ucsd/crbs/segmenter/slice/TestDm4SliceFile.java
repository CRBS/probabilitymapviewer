/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

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
public class TestDm4SliceFile {
    
    public TestDm4SliceFile() {
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
    public void testGetFullPath(){
        Dm4SliceFile slice = new Dm4SliceFile(null);
        assertTrue(slice.getFullPath() == null);
        slice = new Dm4SliceFile("/foo");
        assertTrue(slice.getFullPath().equals("/foo"));
    }
    
    @Test
    public void testGetSliceName(){
        Dm4SliceFile slice = new Dm4SliceFile(null);
        assertTrue(slice.getSliceName() == null);
        
        slice = new Dm4SliceFile("");
        assertTrue(slice.getSliceName().equals(""));
        
        slice = new Dm4SliceFile("/foo");
        assertTrue(slice.getSliceName().equals("foo"));
        
        slice = new Dm4SliceFile("/foo/blah" + Dm4SliceFile.DM4_EXTENSION);
        assertTrue(slice.getSliceName().equals("blah"));
        
        slice = new Dm4SliceFile("/foo/blah" + Dm4SliceFile.DM4_EXTENSION +
                Dm4SliceFile.DM4_EXTENSION);
        
        assertTrue(slice.getSliceName().equals("blah" 
                + Dm4SliceFile.DM4_EXTENSION));
    }
}
