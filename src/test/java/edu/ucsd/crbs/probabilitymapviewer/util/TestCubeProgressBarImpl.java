package edu.ucsd.crbs.probabilitymapviewer.util;

import edu.ucsd.crbs.probabilitymapviewer.util.CubeProgressBarImpl;
import edu.ucsd.crbs.probabilitymapviewer.App;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestCubeProgressBarImpl {

    public TestCubeProgressBarImpl() {
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
    public void testNullPropsAndVariousSliceCounts() {
        CubeProgressBarImpl progBar = new CubeProgressBarImpl(null);
        assertTrue(progBar.getCubeImage(-1), progBar.getCubeImage(-1).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        assertTrue(progBar.getCubeImage(0), progBar.getCubeImage(0).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        String cubeName;
        for (int i = 0; i <= 1000;i+=100){
            for (int j = 1; j <= 99; j++) {
                if (i < 100){
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube10.png";
                }
                else {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube"+Integer.toString(i/10)+".png";
                }
                assertTrue(progBar.getCubeImage(i+j)+" for value "+(i+j)+
                        " and my convert is "+Integer.toString(i/10), 
                        progBar.getCubeImage(i+j).equals(cubeName));
            }
        }
    }
    
    @Test
    public void TestSetExpectedSlices(){
        CubeProgressBarImpl progBar = new CubeProgressBarImpl(new Properties());
        
        progBar.setExpectedSlices(20);
        String cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube100.png";
        assertTrue(progBar.getCubeImage(20),
                   progBar.getCubeImage(20).equals(cubeName));
        
        // try 0 expected slices, weird case
        progBar.setExpectedSlices(0);
        cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png";
        assertTrue(progBar.getCubeImage(20),
                   progBar.getCubeImage(20).equals(cubeName));
        
    }
    
    @Test
    public void testPropsNotSetExpectedSliceAndVariousSliceCounts() {
        CubeProgressBarImpl progBar = new CubeProgressBarImpl(new Properties());
        assertTrue(progBar.getCubeImage(-1), progBar.getCubeImage(-1).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        assertTrue(progBar.getCubeImage(0), progBar.getCubeImage(0).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        String cubeName;
        for (int i = 0; i <= 1000;i+=100){
            for (int j = 1; j <= 99; j++) {
                if (i < 100){
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube10.png";
                }
                else {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube"+Integer.toString(i/10)+".png";
                }
                assertTrue(progBar.getCubeImage(i+j)+" for value "+(i+j)+
                        " and my convert is "+Integer.toString(i/10), 
                        progBar.getCubeImage(i+j).equals(cubeName));
            }
        }
    }
    
    @Test
    public void testExpectedSliceOfOneThousandAndVariousSliceCounts() {
        Properties props = new Properties();
        props.setProperty(App.EXPECTED_SLICES_ARG, "1000");
        CubeProgressBarImpl progBar = new CubeProgressBarImpl(props);
        assertTrue(progBar.getCubeImage(-1), progBar.getCubeImage(-1).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        assertTrue(progBar.getCubeImage(0), progBar.getCubeImage(0).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        String cubeName;
        for (int i = 0; i <= 1000;i+=100){
            for (int j = 1; j <= 99; j++) {
                if (i < 100){
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube10.png";
                }
                else {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube"+Integer.toString(i/10)+".png";
                }
                assertTrue(progBar.getCubeImage(i+j)+" for value "+(i+j)+
                        " and my convert is "+Integer.toString(i/10), 
                        progBar.getCubeImage(i+j).equals(cubeName));
            }
        }
    }
    
    @Test
    public void testExpectedSliceOfOneHundredAndVariousSliceCounts() {
        Properties props = new Properties();
        props.setProperty(App.EXPECTED_SLICES_ARG, "100");
        CubeProgressBarImpl progBar = new CubeProgressBarImpl(props);
        assertTrue(progBar.getCubeImage(-1), progBar.getCubeImage(-1).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        assertTrue(progBar.getCubeImage(0), progBar.getCubeImage(0).equals(
                CubeProgressBarImpl.CUBES_PREFIX_PATH + "cube.png"));
        String cubeName;
            for (int j = 1; j <= 150; j++) {
                if (j < 20){
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube10.png";
                }
                else if (j < 30) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube20.png";
                }
                                else if (j < 40) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube30.png";
                }

                                else if (j < 50) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube40.png";
                }
                else if (j < 60) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube50.png";
                }
                else if (j < 70) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube60.png";
                }
                else if (j < 80) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube70.png";
                }
                else if (j < 90) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube80.png";
                }
                else if (j < 100) {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube90.png";
                }
                else {
                    cubeName = CubeProgressBarImpl.CUBES_PREFIX_PATH + 
                            "cube100.png";
                }

                assertTrue(progBar.getCubeImage(j)+" for value "+(j)+
                        " and my convert is "+Math.round(j), 
                        progBar.getCubeImage(j).equals(cubeName));
            }
    }
}
