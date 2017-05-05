/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this probabilitymapviewer for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this probabilitymapviewer into commercial products
 * or use for commercial purposes should contact the Technology Transfer Office, 
 * University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, 
 * La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, 
 * E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS segmenter, EVEN IF THE UNIVERSITY 
 * OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE probabilitymapviewer PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE probabilitymapviewer WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */
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
