/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this segmenter for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this segmenter into commercial products
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
 * THE segmenter PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE segmenter WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */

package edu.ucsd.crbs.probabilitymapviewer.slice;

import edu.ucsd.crbs.probabilitymapviewer.slice.SliceDir;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceMonitorImpl;
import edu.ucsd.crbs.probabilitymapviewer.slice.SimulatedSliceMonitor;
import edu.ucsd.crbs.probabilitymapviewer.App;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestSimulatedSliceMonitor {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public TestSimulatedSliceMonitor() {
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
    public void testNullProps() throws Exception{
        try {
            SimulatedSliceMonitor ssm = new SimulatedSliceMonitor(null);
            fail("Expected NullPointerException");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("Properties passed in constructor is null"));
        }
    }
    
    @Test
    public void testNullInputImageArg() throws Exception{
        try {
            SimulatedSliceMonitor ssm = new SimulatedSliceMonitor(new Properties());
            fail("Expected NullPointerException");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("INPUT_IMAGE_ARG property is null"));
        }
    }
    
    @Test
    public void testNoSliceDirectories() throws Exception {
        File tmpDir = testFolder.newFolder();
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        SimulatedSliceMonitor ssm = new SimulatedSliceMonitor(props);
        assertTrue(ssm.getSlices().size() == 0);
        assertTrue(ssm.getCollectionInformation() == null);
    }
    
    @Test
    public void testNoSliceDirectoriesWithReadMe() throws Exception {
        File tmpDir = testFolder.newFolder();
        
        FileWriter fw = new FileWriter(tmpDir+File.separator+SliceMonitorImpl.COLLECTION_PROPS_README);
        fw.write("name=hello");
        fw.flush();
        fw.close();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        SimulatedSliceMonitor ssm = new SimulatedSliceMonitor(props);
        assertTrue(ssm.getSlices().size() == 0);
        
        Properties resProp = ssm.getCollectionInformation();
        assertTrue(resProp != null);
        assertTrue(resProp.getProperty("name").equals("hello"));
    }
    
    @Test
    public void testOneSliceDirectoriesWithReadMe() throws Exception {
        File tmpDir = testFolder.newFolder();
        
        File sliceOne = new File(tmpDir.getAbsolutePath()+File.separator+
                "slice_46");
        assertTrue(sliceOne.mkdirs());
        
        FileWriter fw = new FileWriter(tmpDir+File.separator+SliceMonitorImpl.COLLECTION_PROPS_README);
        fw.write("name=");
        fw.flush();
        fw.close();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        SimulatedSliceMonitor ssm = new SimulatedSliceMonitor(props);
        
        assertTrue(ssm.getSlices().size() == 1);
        assertTrue(ssm.getSlices().get(0).getSliceName().equals("slice_46"));
        
        assertTrue(ssm.getSlices().size() == 1);
        
        
        Properties resProp = ssm.getCollectionInformation();
        assertTrue(resProp != null);
        assertTrue(resProp.getProperty("name").equals(""));
    }
    
    @Test
    public void testTwoSliceDirectories() throws Exception {
        File tmpDir = testFolder.newFolder();
        
        File sliceOne = new File(tmpDir.getAbsolutePath()+File.separator+
                "slice_46");
        assertTrue(sliceOne.mkdirs());
        File sliceTwo = new File(tmpDir.getAbsolutePath()+File.separator+
                "slice_0");
        assertTrue(sliceTwo.mkdirs());
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        SimulatedSliceMonitor ssm = new SimulatedSliceMonitor(props);
        List<SliceDir> slices = ssm.getSlices();

        assertTrue(slices.size() == 1);
        assertTrue(slices.get(0).getSliceName().equals("slice_0"));
        
        slices = ssm.getSlices();
        assertTrue(slices.size() == 2);
        assertTrue(slices.get(0).getSliceName().equals("slice_0"));
        assertTrue(slices.get(1).getSliceName().equals("slice_46"));
        
        slices = ssm.getSlices();
        assertTrue(slices.size() == 2);
        assertTrue(slices.get(0).getSliceName().equals("slice_0"));
        assertTrue(slices.get(1).getSliceName().equals("slice_46"));

        slices = ssm.getSlices();
        assertTrue(slices.size() == 2);
        assertTrue(slices.get(0).getSliceName().equals("slice_0"));
        assertTrue(slices.get(1).getSliceName().equals("slice_46"));
        
        assertNull(ssm.getCollectionInformation());
    }
    
    @Test
    public void testFiftySliceDirectories() throws Exception {
        File tmpDir = testFolder.newFolder();
        
        for (int i = 0; i < 50; i++){
            File sliceOne = new File(tmpDir.getAbsolutePath()+File.separator+
                "slice_"+i);
            assertTrue(sliceOne.mkdirs());
        }
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        SimulatedSliceMonitor ssm = new SimulatedSliceMonitor(props);
        List<SliceDir> slices = ssm.getSlices();

        assertTrue(slices.size() == 1);
        assertTrue(slices.get(0).getSliceName().equals("slice_0"));
        
        slices = ssm.getSlices();
        assertTrue(slices.size() == 2);
        assertTrue(slices.get(0).getSliceName().equals("slice_0"));
        assertTrue(slices.get(1).getSliceName().equals("slice_1"));
        
        for (int i = 2; i < 50; i++){
            slices = ssm.getSlices();
            assertTrue(slices.size() == i+1);
            assertTrue(slices.get(i).getSliceName().equals("slice_"+i));
        }
        assertNull(ssm.getCollectionInformation());
    }
}