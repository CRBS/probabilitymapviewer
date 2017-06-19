package edu.ucsd.crbs.probabilitymapviewer.job;

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
public class TestJobResult {
    
    public TestJobResult() {
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
    public void testGetterAndSetter(){
        JobResult jr = new JobResult();
        assertTrue(jr.getRunTimeInMilliseconds() == 0);
        jr.setRunTimeInMilliseconds(1L);
        assertTrue(jr.getRunTimeInMilliseconds() == 1L);
        
    }

    
}
