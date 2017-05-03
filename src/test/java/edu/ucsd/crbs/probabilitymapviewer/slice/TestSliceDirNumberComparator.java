/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.slice;

import edu.ucsd.crbs.probabilitymapviewer.slice.SliceDir;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceDirNumberComparator;
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
public class TestSliceDirNumberComparator {
    
    public TestSliceDirNumberComparator() {
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
    public void testCommonScenarios(){
        SliceDirNumberComparator snc = new SliceDirNumberComparator();
        //both null
        assertTrue(snc.compare(null, null) == 0);
    
        //test o1 null o2 not null
        assertTrue(snc.compare(null, new SliceDir("slice_1")) == -1);
    
    
        //test o1 not null o2 null
        assertTrue(snc.compare(new SliceDir("slice_1"), null) == 1);
    
        // test o1 less then o2
        assertTrue(snc.compare(new SliceDir("slice_1"), new SliceDir("slice_2")) == -1);
    
        // test o1 equal o2
        assertTrue(snc.compare(new SliceDir("slice_0002"),
                new SliceDir("slice_0002")) == 0);
    
        // test o1 greater then o2
        assertTrue(snc.compare(new SliceDir("slice_0003"),
                new SliceDir("slice_0002")) == 1);
    }
}
