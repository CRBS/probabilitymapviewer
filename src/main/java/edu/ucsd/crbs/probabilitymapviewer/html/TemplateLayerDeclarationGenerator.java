package edu.ucsd.crbs.probabilitymapviewer.html;

import edu.ucsd.crbs.probabilitymapviewer.io.ResourceToString;
import edu.ucsd.crbs.probabilitymapviewer.io.ResourceToStringImpl;
import edu.ucsd.crbs.probabilitymapviewer.io.StringReplacer;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class TemplateLayerDeclarationGenerator implements LayerDeclarationGenerator,StringReplacer {

    public static final String LAYER_FILE_PATH = "@@LAYER_FILE_PATH@@";
    public static final String MAX_ZOOM = "@@MAX_ZOOM@@";
    public static final String MIN_ZOOM = "@@MIN_ZOOM@@";
    public static final String MAX_NATIVE_ZOOM = "@@MAX_NATIVE_ZOOM@@";
    public static final String TILE_SIZE = "@@TILE_SIZE@@";
    public static final String OPACITY = "@@OPACITY@@";
    public static final String ATTRIBUTION = "@@ATTRIBUTION@@";
    public static final String LAYER_ID = "@@LAYER_ID@@";
    public static final String ERROR_TILE_URL = "@@ERROR_TILE_URL@@";
    public static final String LAYER_VAR_NAME = "@@LAYER_NAME@@";
    
    
    private String _varName;
    private String _tileSize;
    private String _attribution;
    private String _id;
    private String _imagePath;
    private String _errorTileUrl;
    private String _opacity;
    
    
    public TemplateLayerDeclarationGenerator(final String varName,final String tileSize,final String attribution,
            final String id,final String imagePath,final String errorTileUrl,
            final String opacity){
        _tileSize = tileSize;
        _attribution = attribution;
        _id = id;
        _imagePath = imagePath;
        _errorTileUrl = errorTileUrl;
        _opacity = opacity;
        _varName = varName;
    }
    
    
    @Override
    public String replace(String line) {
        if (line == null){
            return null;
        }
        return line.replaceAll(LAYER_FILE_PATH,_imagePath)
                .replaceAll(MAX_ZOOM,"0")
                .replaceAll(MIN_ZOOM,"0")
                .replaceAll(MAX_NATIVE_ZOOM,"0")
                .replaceAll(TILE_SIZE, _tileSize)
                .replaceAll(OPACITY,_opacity)
                .replaceAll(ATTRIBUTION,_attribution)
                .replaceAll(LAYER_ID,_id)
                .replaceAll(ERROR_TILE_URL,_errorTileUrl)
                .replaceAll(LAYER_VAR_NAME,_varName);
    }

    @Override
    public String getLayerDeclaration() throws Exception {
        ResourceToString resToStr = new ResourceToStringImpl();
        return resToStr.getResourceAsString("/layerdeclaration.template", this);
    }

}
