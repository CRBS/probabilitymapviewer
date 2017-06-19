package edu.ucsd.crbs.probabilitymapviewer.server;

import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.handler.ImageProcessorHandler;
import java.util.ArrayList;
import java.util.Properties;
import org.eclipse.jetty.server.Handler;
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
public class TestSegmenterWebServerFactory {
    
    public TestSegmenterWebServerFactory() {
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
    public void testWithNullExecutorServiceAndLayers() throws Exception{
        Properties props = new Properties();
        props.setProperty(App.PORT_ARG, "8080");
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "/foo");
        props.setProperty(App.DIR_ARG,"/dir");
        props.setProperty(App.NUM_CORES_ARG, "1");
        SegmenterWebServerFactory factory = new SegmenterWebServerFactory();
        SegmenterWebServer s = factory.getSegmenterWebServer(null,
                props, null);
        assertTrue(s != null);        
        Handler[] hlist = s.getServer().getHandlers();
        assertTrue("Hsize: " + Integer.toString(hlist.length),
                hlist.length == 1);

    }
    
    @Test
    public void testWithDisableAnalyzingTileTrueAndLayers() throws Exception{
        Properties props = new Properties();
        props.setProperty(App.PORT_ARG, "8080");
        props.setProperty(App.ADJUSTED_INPUT_IMAGE_ARG, "/foo");
        props.setProperty(App.DIR_ARG,"/dir");
        props.setProperty(App.NUM_CORES_ARG, "1");
        props.setProperty(App.DISABLE_ANALYZING_TILE_ARG, "true");
        SegmenterWebServerFactory factory = new SegmenterWebServerFactory();
        SegmenterWebServer s = factory.getSegmenterWebServer(null,
                props, null);
        assertTrue(s != null);     
        Handler[] hlist = s.getServer().getHandlers();
        assertTrue("Hsize: " + Integer.toString(hlist.length),
                hlist.length == 1);
    }
}
