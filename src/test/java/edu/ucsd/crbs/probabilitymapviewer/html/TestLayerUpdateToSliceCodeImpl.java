package edu.ucsd.crbs.probabilitymapviewer.html;

import edu.ucsd.crbs.probabilitymapviewer.html.LayerUpdateToSliceCodeImpl;
import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestLayerUpdateToSliceCodeImpl {

    public TestLayerUpdateToSliceCodeImpl() {
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

    @Test public void testValidLayer(){
        CustomLayer cl = new CustomLayer("foo","blue","script", null);
        LayerUpdateToSliceCodeImpl updateCode = new LayerUpdateToSliceCodeImpl(cl);
        assertTrue(updateCode.getLayerUpdateToSliceCode(),
                updateCode.getLayerUpdateToSliceCode().startsWith("foo.setUrl('layerhandlers/foo/'+sliceName+'/"+cl.getImageName()+"');"));
    }
}