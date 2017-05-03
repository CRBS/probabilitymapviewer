/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.io;

import edu.ucsd.crbs.probabilitymapviewer.io.WorkingDirCreatorImpl;
import edu.ucsd.crbs.probabilitymapviewer.App;
import static edu.ucsd.crbs.probabilitymapviewer.App.TEMP_DIR_CREATED_FLAG;
import java.io.File;
import java.util.Properties;
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

/**
 *
 * @author churas
 */
public class TestWorkingDirCreatorImpl {
    
    @Rule
    public TemporaryFolder _folder= new TemporaryFolder();
    
    public TestWorkingDirCreatorImpl() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Logger.getLogger(WorkingDirCreatorImpl.class.getName()).setLevel(Level.OFF);
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
    public void testNullProperties() throws Exception {
        WorkingDirCreatorImpl wdci = new WorkingDirCreatorImpl();
        try {
           wdci.createWorkingDir(null);
           fail("Expected NullPointerException");
        }
        catch(NullPointerException npe){
            assertTrue(npe.getMessage().equals("Properties is null"));
        }
    }
    
    @Test
    public void testDIRARGpropertyNotSet() throws Exception {
        WorkingDirCreatorImpl wdci = new WorkingDirCreatorImpl();
        try {
           Properties props = new Properties();
           wdci.createWorkingDir(props);
           fail("Expected Exception");
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().equals("dir property not set"));
        }
    }
    
    @Test
    public void testDirAlreadyExists() throws Exception {
        File tmpDir = _folder.newFolder();
        WorkingDirCreatorImpl wdci = new WorkingDirCreatorImpl();
         Properties props = new Properties();
         props.setProperty(App.DIR_ARG, tmpDir.getAbsolutePath());
         File result = wdci.createWorkingDir(props);
         assertTrue(result != null);
         assertTrue(props.getProperty(TEMP_DIR_CREATED_FLAG) == null);
         assertTrue(result.compareTo(tmpDir) == 0);
        
        
    }
    
    @Test
    public void testDirNeedsToBeCreated() throws Exception {
        File tmpDir = _folder.newFolder();
        WorkingDirCreatorImpl wdci = new WorkingDirCreatorImpl();
         Properties props = new Properties();
         File fooDir = new File(tmpDir.getAbsolutePath()+File.separator+"foo");
         props.setProperty(App.DIR_ARG, fooDir.getAbsolutePath());
         File result = wdci.createWorkingDir(props);
         assertTrue(result != null);
         assertTrue(props.getProperty(TEMP_DIR_CREATED_FLAG).equals("true"));
         assertTrue(result.compareTo(fooDir) == 0);
    }
    
}
