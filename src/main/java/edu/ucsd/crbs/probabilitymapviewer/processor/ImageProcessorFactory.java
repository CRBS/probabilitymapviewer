package edu.ucsd.crbs.probabilitymapviewer.processor;

import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;
import java.io.File;
import java.util.Properties;

/**
 * Creates ImageProcessor objects
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ImageProcessorFactory {

    public static final String CHM_TEST_SH = "CHM_test.sh";
    public static final String RUN_ILASTIK_SH = "run_ilastik.sh";
    
    private Properties _props;
    
    public ImageProcessorFactory(Properties props){
        _props = props;
    }
    
    public ImageProcessor getImageProcessor(CustomLayer layer){
        String workingDir =  _props.getProperty(App.LAYER_HANDLER_BASE_DIR)
                +File.separator+layer.getVarName();
        return getExternalImageProcessor(workingDir, layer);
    }
    
    private ImageProcessor getExternalImageProcessor(final String workingDir,
            CustomLayer layer){
        return new ExternalImageProcessor(
                _props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG),
                   workingDir,layer.getScript(),layer.getConvertColor(),
                _props.getProperty(App.CONVERT_ARG),
                   _props.getProperty(App.TILE_SIZE_ARG),
                getAnalyzingTile(layer),
                layer.getOptArgs());
    }
    
    private String getAnalyzingTile(CustomLayer layer){
        String analyzingTile = null;
        if (_props.getProperty(App.DISABLE_ANALYZING_TILE_ARG,"true").equals("false")){
            analyzingTile = _props.getProperty(App.DIR_ARG)+File.separator
                            +layer.getAnalyzingTile();
        }
        return analyzingTile;
    }
}
