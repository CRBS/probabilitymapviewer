/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.slice;

import edu.ucsd.crbs.probabilitymapviewer.slice.Dm4SliceFile;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceConverter;
import edu.ucsd.crbs.probabilitymapviewer.slice.Dm4SliceConverterDaemon;
import edu.ucsd.crbs.probabilitymapviewer.slice.SliceDir;
import edu.ucsd.crbs.probabilitymapviewer.App;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TestDm4SliceConverterDaemon {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public TestDm4SliceConverterDaemon() {
    }

    @BeforeClass
    public static void setUpClass() {
        Logger log = Logger.getLogger(TestDm4SliceConverterDaemon.class.getName());
        log.getParent().setLevel(Level.ALL);
        for (Handler h : log.getParent().getHandlers()) {
            h.setLevel(Level.ALL);
        }
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

    /**
     * Creates file specified by <b>path</b> parameter writing hello in the
     * file.
     *
     * @param path
     */
    private void createDummyFile(File path) throws Exception {
        FileWriter fw = new FileWriter(path);
        fw.write("hello\n");
        fw.flush();
        fw.close();
    }

    @Test
    public void testConstructorNullProps() {
        try {
            new Dm4SliceConverterDaemon(null, null);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals("props is null"));
        }
    }

    @Test
    public void testConstructorInputImageArgIsNull() {
        try {
            Properties props = new Properties();
            new Dm4SliceConverterDaemon(props, null);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage(),
                    npe.getMessage().equals(App.INPUT_IMAGE_ARG
                            + " property is null"));
        }
    }

    @Test
    public void testConstructorAdjustedInputImageArgIsNull() {
        try {
            Properties props = new Properties();
            props.setProperty(App.INPUT_IMAGE_ARG, "");

            new Dm4SliceConverterDaemon(props, null);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage(),
                    npe.getMessage().equals(App.ADJUSTED_INPUT_IMAGE_ARG
                            + " property is null"));
        }
    }

    @Test
    public void testGetDestinationDirectory() {
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "");
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);
        assertTrue(daemon.getDestinationDirectory().equals("hello"));
    }

    // test getSecondYoungestDm4File No files
    @Test
    public void testGetSecondYoungestDm4FileNoFiles() throws Exception {
        File tmpDir = testFolder.newFolder();

        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);

        assertTrue(daemon.getSecondYoungestDm4File() == null);

    }

    // test getSecondYoungestDm4File one file
    @Test
    public void testGetSecondYoungestDm4FileOneFile() throws Exception {
        File tmpDir = testFolder.newFolder();

        File one = new File(tmpDir + File.separator + "yo"
                + Dm4SliceFile.DM4_EXTENSION);
        FileWriter fw = new FileWriter(one);
        fw.write("hello\n");
        fw.flush();
        fw.close();

        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);

        assertTrue(daemon.getSecondYoungestDm4File() == null);

    }

    @Test
    public void testGetSecondYoungestDm4FileTwoFiles() throws Exception {
        File tmpDir = testFolder.newFolder();

        File one = new File(tmpDir + File.separator + "aaa"
                + Dm4SliceFile.DM4_EXTENSION);
        createDummyFile(one);

        File two = new File(tmpDir + File.separator + "bbb"
                + Dm4SliceFile.DM4_EXTENSION);
        createDummyFile(two);

        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);

        one.setLastModified(1461270631000L);
        two.setLastModified(1461270632000L);

        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(one.getName()));

        one.setLastModified(1461270632000L);
        two.setLastModified(1461270631000L);
        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(two.getName()));
    }

    @Test
    public void testGetSecondYoungestDm4FileThreeFiles() throws Exception {
        File tmpDir = testFolder.newFolder();

        File one = new File(tmpDir + File.separator + "aaa"
                + Dm4SliceFile.DM4_EXTENSION);
        createDummyFile(one);

        File two = new File(tmpDir + File.separator + "bbb"
                + Dm4SliceFile.DM4_EXTENSION);
        createDummyFile(two);

        File three = new File(tmpDir + File.separator + "ccc"
                + Dm4SliceFile.DM4_EXTENSION);
        createDummyFile(three);

        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);

        one.setLastModified(1461270631000L);
        two.setLastModified(1461270632000L);
        three.setLastModified(1461270633000L);

        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(two.getName()));

        one.setLastModified(1461270631000L);
        two.setLastModified(1461270635000L);
        three.setLastModified(1461270632000L);

        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(three.getName()));

        one.setLastModified(1461270637000L);
        two.setLastModified(1461270632000L);
        three.setLastModified(1461270634000L);

        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(three.getName()));

        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(three.getName()));

        one.setLastModified(1461270637000L);
        two.setLastModified(1461270635000L);
        three.setLastModified(1461270631000L);

        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(two.getName()));
    }

    @Test
    public void testGetSecondYoungestDm4FileFiftyFiles() throws Exception {
        File tmpDir = testFolder.newFolder();

        ArrayList<File> files = new ArrayList<File>();
        Long startVal = 1461270637000L;
        File f = null;
        for (int i = 0; i < 50; i++) {
            f = new File(tmpDir + File.separator + Integer.toString(i)
                    + Dm4SliceFile.DM4_EXTENSION);
            createDummyFile(f);
            files.add(f);
            f.setLastModified(startVal);
            startVal += 5000L;
        }

        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);

        assertTrue(daemon.getSecondYoungestDm4File().getName()
                .equals(files.get(files.size() - 2).getName()));
    }

    @Test
    public void testGenDestinationPath() throws Exception {
        File tmpDir = testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);
        
        assertTrue(daemon.genDestinationPath(null) == null);
        assertTrue(daemon.genDestinationPath(new File(tmpDir + File.separator 
                + "yo" + Dm4SliceFile.DM4_EXTENSION)).equals("hello/" + 
                        SliceDir.SLICE_PREFIX + "yo"));
    }
    
    @Test
    public void testRunOneIterationNoFiles() throws Exception {
        File tmpDir = testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        SliceConverter mock = mock(SliceConverter.class);
        
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, mock);
        daemon.runOneIteration();
        
        verify(mock,times(0)).convert(anyString(), anyString());
    }
    
     @Test
    public void testRunOneIterationRunSliceConverterNull()
            throws Exception {
        File tmpDir = testFolder.newFolder();
        File one = new File(tmpDir + File.separator + "aaa" 
                + Dm4SliceFile.DM4_EXTENSION);
        createDummyFile(one);
        
        File two = new File(tmpDir + File.separator + "bbb" 
                + Dm4SliceFile.DM4_EXTENSION);
        this.createDummyFile(two);
        
        one.setLastModified(1000L);
        two.setLastModified(50000L);
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, null);
        daemon.runOneIteration();
        daemon.runOneIteration();
        daemon.runOneIteration();
    }
    
    @Test
    public void testRunOneIterationRunAgainstSameTwoFilesTwice()
            throws Exception {
        File tmpDir = testFolder.newFolder();
        File one = new File(tmpDir + File.separator + "aaa" 
                + Dm4SliceFile.DM4_EXTENSION);
        createDummyFile(one);
        
        File two = new File(tmpDir + File.separator + "bbb" 
                + Dm4SliceFile.DM4_EXTENSION);
        this.createDummyFile(two);
        
        one.setLastModified(1000L);
        two.setLastModified(50000L);
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, tmpDir.getAbsolutePath());
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "hello");
        SliceConverter mock = mock(SliceConverter.class);
        
        Dm4SliceConverterDaemon daemon
                = new Dm4SliceConverterDaemon(props, mock);
        daemon.runOneIteration();
        daemon.runOneIteration();
        daemon.runOneIteration();
        verify(mock,times(1)).convert(one.getAbsolutePath(), 
                daemon.genDestinationPath(one));
    }
}
