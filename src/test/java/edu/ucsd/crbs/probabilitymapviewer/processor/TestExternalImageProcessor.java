package edu.ucsd.crbs.probabilitymapviewer.processor;


import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.job.ExternalCommandLineJob;
import edu.ucsd.crbs.probabilitymapviewer.layer.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import org.junit.Assert;

import edu.ucsd.crbs.probabilitymapviewer.processor.ExternalImageProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author churas
 */
@RunWith(JUnit4.class)
public class TestExternalImageProcessor {
    
    @Rule
    public TemporaryFolder _tmpFolder = new TemporaryFolder();
    
    public TestExternalImageProcessor() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        App.tilesToProcess.clear();
        App.latestSlice = null;
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testProcessNullImage(){
        
        ExternalImageProcessor eip = new ExternalImageProcessor("dir",
                                                                "wdir","binary",
                                                                "Red","convert",
                                                                "2","atile",
                                                                "");
        eip.process(null);
        assertTrue(App.tilesToProcess.isEmpty());
    }
    
    @Test
    public void testProcessImageNotFoundOnFilesystem() throws IOException {
        
        File tmpFolder = _tmpFolder.newFolder();
        
        ExternalImageProcessor eip = new ExternalImageProcessor(tmpFolder.getCanonicalPath(),
                                                                "wdir","binary",
                                                                "Red","convert",
                                                                "2","atile",
                                                                "");
        eip.process("foo");
        assertTrue(App.tilesToProcess.isEmpty());
    }
    
    @Test
    public void testProcessWithImageFileThatExists() throws IOException {
        
        File tmpFolder = _tmpFolder.newFolder();
        
        String imageFilePath = tmpFolder.getPath() + File.separator + "foo";
                                      
        FileWriter fw = new FileWriter(imageFilePath);
        fw.write("hello\n");
        fw.flush();
        fw.close();
        ExternalImageProcessor eip = new ExternalImageProcessor(tmpFolder.getPath(),
                                                                "wdir","binary",
                                                                "Red","convert",
                                                                "2","atile",
                                                                "");
        eip.process("foo");
        assertTrue(App.tilesToProcess.size() == 1);
        ExternalCommandLineJob job = (ExternalCommandLineJob)App.tilesToProcess.getFirst();
        File bPath = new File("binary");
        assertTrue("expect " + bPath.getCanonicalPath(),
                   job.getBinary().equals(bPath.getCanonicalPath()));
        assertTrue("expect Red",job.getColorsToZeroOut().equals("Red"));
        assertTrue("expect atile",job.getAnalyzingTile().equals("atile"));
        assertTrue("expect input",job.getInputImage().equals(imageFilePath));
        assertTrue("expect empty str",job.getOptArgs().equals(""));
        assertTrue("expecte 2x2",job.getTileSize().equals("2x2"));
        assertTrue("expecte wdir",job.getOutDir().equals("wdir"));
    }
    
    
    @Test
    public void testProcessWithLatestSliceSet() throws IOException {
        
        File tmpFolder = _tmpFolder.newFolder();
        
        String imageFilePath = tmpFolder.getPath() + File.separator + "foo";
       
        App.latestSlice = "slice_0"; 
        
        FileWriter fw = new FileWriter(imageFilePath);
        fw.write("hello\n");
        fw.flush();
        fw.close();
        ExternalImageProcessor eip = new ExternalImageProcessor(tmpFolder.getPath(),
                                                                "wdir",
                                                                "binary",
                                                                "Red","convert",
                                                                "2","atile",
                                                                "");
        eip.process("foo");
        assertTrue(App.tilesToProcess.size() == 1);
        ExternalCommandLineJob job = (ExternalCommandLineJob)App.tilesToProcess.getFirst();
        File bPath = new File("binary");
        assertTrue("expect " + bPath.getCanonicalPath(),
                   job.getBinary().equals(bPath.getCanonicalPath()));
        assertTrue("expect Red",job.getColorsToZeroOut().equals("Red"));
        assertTrue("expect atile",job.getAnalyzingTile().equals("atile"));
        assertTrue("expect input",job.getInputImage().equals(imageFilePath));
        assertTrue("expect empty str",job.getOptArgs().equals(""));
        assertTrue("expecte 2x2",job.getTileSize().equals("2x2"));
        assertTrue("expecte wdir",job.getOutDir().equals("wdir" +
                                                         File.separator +
                                                         App.latestSlice));

    }
    
    @Test
    public void testProcessWithLatestSliceEmptyString() throws IOException {
        
        File tmpFolder = _tmpFolder.newFolder();
        
        String imageFilePath = tmpFolder.getPath() + File.separator + "foo";
       
        App.latestSlice = ""; 
        
        FileWriter fw = new FileWriter(imageFilePath);
        fw.write("hello\n");
        fw.flush();
        fw.close();
        ExternalImageProcessor eip = new ExternalImageProcessor(tmpFolder.getPath(),
                                                                "wdir",
                                                                "binary",
                                                                "Red","convert",
                                                                "2","atile",
                                                                "");
        eip.process("foo");
        assertTrue(App.tilesToProcess.size() == 1);
        ExternalCommandLineJob job = (ExternalCommandLineJob)App.tilesToProcess.getFirst();
        File bPath = new File("binary");
        assertTrue("expect " + bPath.getCanonicalPath(),
                   job.getBinary().equals(bPath.getCanonicalPath()));
        assertTrue("expect Red",job.getColorsToZeroOut().equals("Red"));
        assertTrue("expect atile",job.getAnalyzingTile().equals("atile"));
        assertTrue("expect input",job.getInputImage().equals(imageFilePath));
        assertTrue("expect empty str",job.getOptArgs().equals(""));
        assertTrue("expecte 2x2",job.getTileSize().equals("2x2"));
        assertTrue("expecte wdir",job.getOutDir().equals("wdir"));

    }
   
    

}
