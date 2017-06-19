package edu.ucsd.crbs.probabilitymapviewer.server;

import edu.ucsd.crbs.probabilitymapviewer.handler.ImageProcessorHandler;
import java.util.ArrayList;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author churas
 */
public class TestSegmenterWebServer {
    
    public TestSegmenterWebServer() {
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
    public void testGetSetImageProcHandlers(){
        SegmenterWebServer sws = new SegmenterWebServer();
        assertTrue(sws.getImageProcHandlers() == null);
        ArrayList<ImageProcessorHandler> alist = new ArrayList<ImageProcessorHandler>();
        sws.setImageProcHandlers(alist);
        assertTrue(sws.getImageProcHandlers().isEmpty());
        alist.add(new ImageProcessorHandler(null));
        assertTrue(sws.getImageProcHandlers().size() == 1);
    }
    
    @Test
    public void testGetSetServer(){
        SegmenterWebServer sws = new SegmenterWebServer();
        assertTrue(sws.getServer() == null);
        sws.setServer(null);
        assertTrue(sws.getServer() == null);
        Server s = new Server();
        sws.setServer(s);
        Server ret = sws.getServer();
        assertTrue(ret == s);
    }
}
