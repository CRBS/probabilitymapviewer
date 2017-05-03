/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.slice;

import edu.ucsd.crbs.probabilitymapviewer.slice.Dm4SliceMonitorImpl;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceConverterDaemon;
import edu.ucsd.crbs.probabilitymapviewer.slice.Dm4ToSliceConverter;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceDir;
import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

/**
 *
 * @author churas
 */
public class TestDm4SliceMonitorImpl {
    
    @Rule
    public TemporaryFolder _testFolder = new TemporaryFolder();
    
    public TestDm4SliceMonitorImpl() {
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
    public void testNullSliceConverterDaemon() throws Exception{
        try {
            Dm4SliceMonitorImpl mon = new Dm4SliceMonitorImpl(null);
            fail("Expected NullPointerException");
        } catch(NullPointerException npe){
            
        }
        
    }
    
    //test getCollectionInformation
    @Test
    public void testGetCollectionInformation() throws Exception {
        SliceConverterDaemon scd = mock(SliceConverterDaemon.class);
        Dm4SliceMonitorImpl mon = new Dm4SliceMonitorImpl(scd);
        assertTrue(mon.getCollectionInformation() == null);
    }
    
    @Test
    public void testGetSlicesNullDestinationDirectory() throws Exception {
        SliceConverterDaemon scd = mock(SliceConverterDaemon.class);
        Dm4SliceMonitorImpl mon = new Dm4SliceMonitorImpl(scd);
        try{
            mon.getSlices();
            fail("Expected NullPointerException");
        }catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("Path to input images is null"));
        }
    }
    
    @Test
    public void testGetSlices() throws Exception {
        File tmpDir = this._testFolder.newFolder();
        SliceConverterDaemon scd = mock(SliceConverterDaemon.class);
        when(scd.getDestinationDirectory()).thenReturn(tmpDir.getAbsolutePath());
        Dm4SliceMonitorImpl mon = new Dm4SliceMonitorImpl(scd);
        List<SliceDir> sliceList = mon.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.isEmpty());
        verify(scd).getDestinationDirectory();
        
        //test one directory
        File one = new File(tmpDir + File.separator + SliceDir.SLICE_PREFIX 
                + "foo");
        assertTrue(one.mkdirs());
        sliceList = mon.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 1);
        assertTrue(sliceList.get(0).getFullPath().equals(one.getAbsolutePath()));
        
        //test several directories
        File two = new File(tmpDir + File.separator + SliceDir.SLICE_PREFIX 
                + "second");
        assertTrue(two.mkdirs());
        assertTrue(two.isDirectory());
        
        File third = new File(tmpDir + File.separator + SliceDir.SLICE_PREFIX 
                + "third");
        assertTrue(third.mkdirs());
        assertTrue(third.isDirectory());
        
        File tfile = new File(third.getAbsolutePath() 
                + Dm4ToSliceConverter.TMP_SUFFIX);
        assertTrue(tfile.createNewFile());
        
        File randofile = new File(tmpDir + File.separator + "hi");
        assertTrue(randofile.createNewFile());
        
        third.setLastModified(two.lastModified() - 1000000L);
        
        sliceList = mon.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 3);
        assertTrue(sliceList.get(0).getFullPath().equals(third.getAbsolutePath()));
        assertTrue(sliceList.get(1).getFullPath().equals(one.getAbsolutePath()));
        assertTrue(sliceList.get(2).getFullPath().equals(two.getAbsolutePath()));
    }
}
