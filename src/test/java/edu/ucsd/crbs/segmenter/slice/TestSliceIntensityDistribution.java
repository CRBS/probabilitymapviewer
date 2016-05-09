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
public class TestSliceIntensityDistribution {
    
    public TestSliceIntensityDistribution() {
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
    public void testGettersAndSetters(){
        SliceIntensityDistribution sid = new SliceIntensityDistribution();
        assertTrue(sid.getMinIntensity() == 0.0);
        assertTrue(sid.getMaxIntensity() == 0.0);
        assertTrue(sid.getMeanIntensity() == 0.0);
        assertTrue(sid.getStandardDeviation() == 0.0);
        sid.setMinIntensity(1.0);
        sid.setMaxIntensity(2.0);
        sid.setMeanIntensity(3.0);
        sid.setStandardDeviation(4.0);
        
        assertTrue(sid.getMinIntensity() == 1.0);
        assertTrue(sid.getMaxIntensity() == 2.0);
        assertTrue(sid.getMeanIntensity() == 3.0);
        assertTrue(sid.getStandardDeviation() == 4.0);
        
    }
    
    @Test 
    public void testGetScalingLimitAsString(){
        
        SliceIntensityDistribution sid = new SliceIntensityDistribution();
        assertTrue(sid.getScalingLimitsAsString().equals("0,0"));
        
        // test where mean is negative
        sid.setMeanIntensity(-4.0);
        sid.setStandardDeviation(2.0);
        assertTrue(sid.getScalingLimitsAsString(),
                sid.getScalingLimitsAsString().equals("-8,0"));
        
        // test where mean is positive
        sid.setMeanIntensity(10.0);
        assertTrue(sid.getScalingLimitsAsString(),
                sid.getScalingLimitsAsString().equals("6,14"));
    }
}
