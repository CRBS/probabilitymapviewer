package edu.ucsd.crbs.probabilitymapviewer.io;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
public class TestResourceToFileImpl implements StringReplacer {

    @Rule
    public TemporaryFolder _testFolder = new TemporaryFolder();
    
    @Override
    public String replace(String line) {
        return "hi";
    }
   
    public TestResourceToFileImpl() {
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
    public void testNullResourcePath() throws Exception {
        ResourceToFileImpl rfi = new ResourceToFileImpl();
        try {
           rfi.writeResourceToScript(null, null, null);
           fail("Expected IllegalArgumentException");
        }
        catch(IllegalArgumentException npe){
            assertTrue(npe.getMessage().equals("resourcePath method parameter "
                    + "cannot be null"));
        }
    }
    
    @Test
    public void testNullDestinationPath() throws Exception {
        ResourceToFileImpl rfi = new ResourceToFileImpl();
        try {
           rfi.writeResourceToScript("/foo", null, null);
           fail("Expected IllegalArgumentException");
        }
        catch(IllegalArgumentException npe){
            assertTrue(npe.getMessage().equals("destinationScript method "
                    + "parameter cannot be null"));
        }
    }
    
    @Test
    public void testInvalidResourcePath() throws Exception {
        ResourceToFileImpl rfi = new ResourceToFileImpl();
        try {
           rfi.writeResourceToScript("/doesnotexist", "hi", null);
           fail("Expected NullPointerException");
        }
        catch(NullPointerException npe){
        }
    }
    
    @Test
    public void testValidEverythingNoReplacer() throws Exception {
        ResourceToFileImpl rfi = new ResourceToFileImpl();
        File tmpDir = _testFolder.newFolder();
        String dest = tmpDir + File.separator + "foo";
        
        rfi.writeResourceToScript("/index.html", dest, null);
        
        List<String> lines = IOUtils.readLines(new FileReader(dest));
        assertTrue(lines.get(0).equals("<html>"));
    }

    @Test
    public void testValidEverythingWithReplacer() throws Exception {
        ResourceToFileImpl rfi = new ResourceToFileImpl();
        File tmpDir = _testFolder.newFolder();
        String dest = tmpDir + File.separator + "foo";
        
        rfi.writeResourceToScript("/index.html", dest, this);
        
        List<String> lines = IOUtils.readLines(new FileReader(dest));
        assertTrue(lines.get(0).equals("hi"));
    }


}
