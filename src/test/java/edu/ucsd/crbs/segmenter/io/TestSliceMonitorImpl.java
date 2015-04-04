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

package edu.ucsd.crbs.segmenter.io;

import edu.ucsd.crbs.segmenter.App;
import java.io.File;
import java.util.List;
import java.util.Properties;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestSliceMonitorImpl {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public TestSliceMonitorImpl() {
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
    public void testGetSlicesWithNullPropertiesPassedIntoConstructor(){
        SliceMonitorImpl smi = new SliceMonitorImpl(null);
        try {
           smi.getSlices();
           fail("Expected Exception");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("Properties passed in constructor is null"));
        }
        catch(Exception ex){
            fail("Expected nullpointerexception");
        }
                   
    }
    
    @Test
    public void testGetSlicesWithNullInputImageArg(){
        SliceMonitorImpl smi = new SliceMonitorImpl(new Properties());
        try {
           smi.getSlices();
           fail("Expected Exception");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("INPUT_IMAGE_ARG property is null"));
        }
        catch(Exception ex){
            fail("Expected nullpointerexception");
        }
                   
    }

    @Test
    public void testGetSlicesOnNonExistantDirectory() throws Exception{
        File tempDir = testFolder.newFolder();
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath()+File.separator+"doesnotexist");
        SliceMonitorImpl smi = new SliceMonitorImpl(props);
        List<String> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 1);
        assertTrue(sliceList.get(0).equals(""));
        
    }

    @Test
    public void testGetSlicesOnEmpty() throws Exception{
        File tempDir = testFolder.newFolder();
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props);
        List<String> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 1);
        assertTrue(sliceList.get(0).equals(""));
    }
    
    @Test
    public void testGetSlicesOnSingleSliceDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_7");
        assertTrue(subDir.mkdirs());
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props);
        List<String> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 1);
        assertTrue(sliceList.get(0).equals("slice_7"));
    }
    

    @Test
    public void testGetSlicesOnTwoSliceDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_7");
        assertTrue(subDir.mkdirs());
        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_4");
        assertTrue(subDir.mkdirs());

        
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props);
        List<String> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 2);
        assertTrue(sliceList.get(0).equals("slice_4"));
        assertTrue(sliceList.get(1).equals("slice_7"));
    }

    @Test
    public void testGetSlicesOnTwoSliceDirWhereOneSliceLacksNumberAfterUnderscore() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_7");
        assertTrue(subDir.mkdirs());
        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_xs");
        assertTrue(subDir.mkdirs());

        
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props);
        List<String> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 2);
        assertTrue(sliceList.get(0).equals("slice_xs"));
        assertTrue(sliceList.get(1).equals("slice_7"));
    }
    
    @Test
    public void testGetSlicesOnThreeoSliceDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_7");
        assertTrue(subDir.mkdirs());
        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_4");
        assertTrue(subDir.mkdirs());

        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceMonitorImpl.SLICE_PREFIX+"_5");
        assertTrue(subDir.mkdirs());
        
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props);
        List<String> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 3);
        assertTrue(sliceList.get(0).equals("slice_4"));
        assertTrue(sliceList.get(1).equals("slice_5"));
        assertTrue(sliceList.get(2).equals("slice_7"));
    }

    @Test
    public void testGetSlicesOnOneThousandSlicesDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = null;
        
        for (int i = 1000; i > 0; i--){
            subDir = new File(tempDir.getAbsolutePath()+
                    File.separator+SliceMonitorImpl.SLICE_PREFIX+"_"+
                    Integer.toString(i));
            assertTrue(subDir.mkdirs());
        }
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props);
        List<String> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 1000);
        assertTrue(sliceList.get(0).equals("slice_1"));
        assertTrue(sliceList.get(1).equals("slice_2"));
        assertTrue(sliceList.get(999),sliceList.get(999).equals("slice_1000"));
    }
    
    
    
}