package edu.ucsd.crbs.probabilitymapviewer.slice;

import edu.ucsd.crbs.probabilitymapviewer.slice.SliceDir;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceMonitorImpl;
import edu.ucsd.crbs.probabilitymapviewer.App;
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
        
        try {
           SliceMonitorImpl smi = new SliceMonitorImpl(null,null);
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
        
        try {
           SliceMonitorImpl smi = new SliceMonitorImpl(new Properties(),null);
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
        SliceMonitorImpl smi = new SliceMonitorImpl(props,null);
        List<SliceDir> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 0);
    }

    @Test
    public void testGetSlicesOnEmpty() throws Exception{
        File tempDir = testFolder.newFolder();
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props,null);
        List<SliceDir> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 0);
    }
    
    @Test
    public void testGetSlicesOnSingleSliceDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"7");
        assertTrue(subDir.mkdirs());
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props,null);
        List<SliceDir> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 1);
        assertTrue(sliceList.get(0).getSliceName().equals("slice_7"));
    }
    

    @Test
    public void testGetSlicesOnTwoSliceDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"7");
        assertTrue(subDir.mkdirs());
        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"4");
        assertTrue(subDir.mkdirs());

        
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props,null);
        List<SliceDir> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 2);
        assertTrue(sliceList.get(0).getSliceName().equals("slice_4"));
        assertTrue(sliceList.get(1).getSliceName().equals("slice_7"));
    }

    @Test
    public void testGetSlicesOnTwoSliceDirWhereOneSliceLacksNumberAfterUnderscore() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"7");
        assertTrue(subDir.mkdirs());
        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"xs");
        assertTrue(subDir.mkdirs());

        
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props,null);
        List<SliceDir> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 2);
        assertTrue(sliceList.get(0).getSliceName().equals("slice_xs"));
        assertTrue(sliceList.get(1).getSliceName().equals("slice_7"));
    }
    
    @Test
    public void testGetSlicesOnThreeoSliceDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"7");
        assertTrue(subDir.mkdirs());
        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"4");
        assertTrue(subDir.mkdirs());

        subDir = new File(tempDir.getAbsolutePath()+
                File.separator+SliceDir.SLICE_PREFIX+"5");
        assertTrue(subDir.mkdirs());
        
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props,null);
        List<SliceDir> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 3);
        assertTrue(sliceList.get(0).getSliceName().equals("slice_4"));
        assertTrue(sliceList.get(1).getSliceName().equals("slice_5"));
        assertTrue(sliceList.get(2).getSliceName().equals("slice_7"));
    }

    @Test
    public void testGetSlicesOnOneThousandSlicesDir() throws Exception{
        File tempDir = testFolder.newFolder();
        File subDir = null;
        
        for (int i = 1000; i > 0; i--){
            subDir = new File(tempDir.getAbsolutePath()+
                    File.separator+SliceDir.SLICE_PREFIX+
                    Integer.toString(i));
            assertTrue(subDir.mkdirs());
        }
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tempDir.getAbsolutePath());
        SliceMonitorImpl smi = new SliceMonitorImpl(props,null);
        List<SliceDir> sliceList = smi.getSlices();
        assertTrue(sliceList != null);
        assertTrue(sliceList.size() == 1000);
        assertTrue(sliceList.get(0).getSliceName().equals("slice_1"));
        assertTrue(sliceList.get(1).getSliceName().equals("slice_2"));
        assertTrue(sliceList.get(999).getSliceName().equals("slice_1000"));
    }
    
    
    
}