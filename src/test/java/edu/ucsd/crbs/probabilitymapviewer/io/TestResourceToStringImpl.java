package edu.ucsd.crbs.probabilitymapviewer.io;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author churas
 */
public class TestResourceToStringImpl implements StringReplacer {

    @Override
    public String replace(String line) {
        return "hi";
    }
   
    
    public TestResourceToStringImpl() {
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
        ResourceToStringImpl rsi = new ResourceToStringImpl();
        try {
            rsi.getResourceAsString(null, null);
           fail("Expected IllegalArgumentException");
        }
        catch(IllegalArgumentException npe){
            assertTrue(npe.getMessage().equals("resourcePath method parameter "
                    + "cannot be null"));
        }
    }

    @Test
    public void testReadingIndexHtmlWithNullStringReplacer() throws Exception {
        ResourceToStringImpl rsi = new ResourceToStringImpl();
        String res = rsi.getResourceAsString("/index.html", null);
        assertTrue(res != null);
        assertTrue(res.startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void testReadingIndexHtmlWith() throws Exception {
        ResourceToStringImpl rsi = new ResourceToStringImpl();
        String res = rsi.getResourceAsString("/index.html", this);
        assertTrue(res != null);
        assertTrue(res.startsWith("hi"));
    }
}
