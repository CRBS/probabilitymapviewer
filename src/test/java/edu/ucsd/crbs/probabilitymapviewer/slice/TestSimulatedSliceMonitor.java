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