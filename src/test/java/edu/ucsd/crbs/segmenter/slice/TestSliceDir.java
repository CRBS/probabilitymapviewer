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
public class TestSliceDir {
    
    public TestSliceDir() {
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
    public void testSliceWithNullPath(){
        SliceDir s = new SliceDir(null);
        assertTrue(s.getSliceName() == null);
        assertTrue(s.getSliceNumber() == -1);
        assertTrue(s.getFullPath() == null);
    }
    
    @Test
    public void testSliceWithValidPath(){
        SliceDir s = new SliceDir("/home/foo/slice_25");
        assertTrue(s.getSliceName().equals("slice_25"));
        assertTrue(s.getSliceNumber() == 25);
        assertTrue(s.getFullPath().equals("/home/foo/slice_25"));
    }
    
    @Test
    public void testSliceWithJustValidDirName(){
        SliceDir s = new SliceDir("slice_0001");
        assertTrue(s.getSliceName().equals("slice_0001"));
        assertTrue(s.getSliceNumber() == 1);
        assertTrue(s.getFullPath().equals("slice_0001"));
    }
    
    @Test
    public void testSliceMissingUnderscoreInPath(){
        SliceDir s = new SliceDir("/h/slice0001");
        assertTrue(s.getSliceName().equals("slice0001"));
        assertTrue(s.getSliceNumber() == -2);
        assertTrue(s.getFullPath().equals("/h/slice0001"));
    }
    
    @Test
    public void testSliceNoNumberAfterUnderscore(){
        SliceDir s = new SliceDir("/h/slice_xx");
        assertTrue(s.getSliceName().equals("slice_xx"));
        assertTrue(s.getSliceNumber() == -3);
        assertTrue(s.getFullPath().equals("/h/slice_xx"));
    }
}
