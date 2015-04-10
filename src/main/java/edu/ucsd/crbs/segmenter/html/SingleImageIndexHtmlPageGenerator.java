/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this segmenter for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this segmenter into commercial products
 * or use for commercial purposes should contact the Technology Transfer Office, 
 * University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, 
 * La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, 
 * E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS segmenter, EVEN IF THE UNIVERSITY 
 * OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE segmenter PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE segmenter WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */
package edu.ucsd.crbs.segmenter.html;

import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.io.ResourceToFile;
import edu.ucsd.crbs.segmenter.io.ResourceToFileImpl;
import edu.ucsd.crbs.segmenter.io.StringReplacer;
import edu.ucsd.crbs.segmenter.layer.CustomLayer;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SingleImageIndexHtmlPageGenerator implements HtmlPageGenerator, StringReplacer {

    private Properties _props;

    public static final String BASE_LAYER_VAR_NAME = "baseLayer";
    public static final String MITO_LAYER_VAR_NAME = "mitoLayer";

    public static final String MITO_LAYER_VAR_NAME_TOKEN = "@@MITO_LAYER_VAR_NAME@@";
    public static final String BASE_LAYER_VAR_NAME_TOKEN = "@@BASE_LAYER_VAR_NAME@@";

    public static final String BASE_MAP_NAME_TOKEN = "@@BASE_MAP_NAME@@";

    public static final String IMAGE_HEIGHT_NEGATIVE_TOKEN = "@@IMAGE_HEIGHT_NEGATIVE@@";

    public static final String IMAGE_WIDTH_TOKEN = "@@IMAGE_WIDTH@@";

    public static final String BASE_IMAGE_LAYER_DEC_TOKEN = "@@BASE_IMAGE_LAYER_DEC@@";
    public static final String MITO_LAYER_DEC_TOKEN = "@@MITO_LAYER_DEC@@";

    public static final String CUSTOM_LAYERS_DECS_TOKEN = "@@CUSTOM_LAYERS_DECS@@";

    public static final String CUSTOM_LAYERS_OVERLAYS_TOKEN = "@@CUSTOM_LAYERS_OVERLAYS@@";

    public static final String CUSTOM_LAYERS_REDRAW_TOKEN = "@@CUSTOM_LAYERS_REDRAWS@@";
    
    public static final String TITLE_TOKEN = "@@TITLE@@";
    
    public static final String COLLECTION_BLOCK_DISPLAY_TOKEN = "@@COLLECTION_BLOCK_DISPLAY@@";
    
    public static final String ADD_SEGMENTER_DISPLAY_TOKEN = "@@ADD_SEGMENTER_DISPLAY@@";
    


    private String _baseLayerDeclaration;
    private String _mitoLayerDeclaration;
    private String _imageWidth;
    private String _imageHeight;
    private String _overlayOpacity;
    private String _tileSize;
    private String _customLayersDescs;
    private String _customLayersOverlays;
    private String _customLayersRedraws;
    private String _title;
    private String _imageName;
    private String _collectionBlockDisplay;
    private String _addSegmenterDisplay;

    private List<CustomLayer> _layers;

    public SingleImageIndexHtmlPageGenerator(Properties props, List<CustomLayer> layers) {
        _props = props;

        _imageHeight = Integer.toString((Integer.parseInt(props.getProperty(App.IMAGE_HEIGHT_ARG, "50000")) * -1) + 1);
        _imageWidth = Integer.toString(Integer.parseInt(props.getProperty(App.IMAGE_WIDTH_ARG, "50000")) - 1);
        _overlayOpacity = props.getProperty(App.OVERLAY_OPACITY_ARG, "0.3");
        _tileSize = props.getProperty(App.TILE_SIZE_ARG, "128");
        _layers = layers;
        _title = props.getProperty(App.TITLE_ARG,"Segmenter");
        _imageName = props.getProperty(App.IMAGE_NAME_ARG,"Base image");
        if (props.getProperty(App.SIMULATE_COLLECTION_ARG,"false").equals("true") ||
            props.getProperty(App.COLLECTION_MODE_ARG,"false").equals("true")){
            _collectionBlockDisplay = "inline-block;";
            _addSegmenterDisplay = "none;";
        }
        else {
            _collectionBlockDisplay = "none;";
            _addSegmenterDisplay = "inline-block;";
        }
    }

    @Override
    public String replace(String line) {
        if (line == null) {
            return line;
        }

        return line.replaceAll(BASE_MAP_NAME_TOKEN,_imageName)
                .replaceAll(BASE_IMAGE_LAYER_DEC_TOKEN, _baseLayerDeclaration)
                .replaceAll(IMAGE_HEIGHT_NEGATIVE_TOKEN, _imageHeight)
                .replaceAll(IMAGE_WIDTH_TOKEN, _imageWidth)
                .replaceAll(BASE_LAYER_VAR_NAME_TOKEN, BASE_LAYER_VAR_NAME)
                .replaceAll(MITO_LAYER_VAR_NAME_TOKEN, MITO_LAYER_VAR_NAME)
                .replaceAll(CUSTOM_LAYERS_DECS_TOKEN, _customLayersDescs)
                .replaceAll(CUSTOM_LAYERS_OVERLAYS_TOKEN, _customLayersOverlays)
                .replaceAll(COLLECTION_BLOCK_DISPLAY_TOKEN,_collectionBlockDisplay)
                .replaceAll(ADD_SEGMENTER_DISPLAY_TOKEN,_addSegmenterDisplay)
                .replaceAll(TITLE_TOKEN,_title);

    }

    @Override
    public void generateHtmlPage(final String workingDir) throws Exception {

        //generate base layer declaration
        LayerDeclarationGenerator decGenerator = new TemplateLayerDeclarationGenerator(BASE_LAYER_VAR_NAME,
                _tileSize, "'"+_imageName+"'", "'images'", "images/{z}-r{y}_c{x}.png", "'/analyzing.png'", "1.0");
        _baseLayerDeclaration = decGenerator.getLayerDeclaration();

        if (_baseLayerDeclaration == null) {
            throw new Exception("something is wrong we got null back for base layer declaration");
        }

        //generate custom layer info
        generateCustomLayersReplacementValues();
        
        ResourceToFile scriptWriter = new ResourceToFileImpl();
        scriptWriter.writeResourceToScript("/index.html", workingDir + File.separator + "index.html", this);
    }

    private void generateCustomLayersReplacementValues() throws Exception {

        if (_layers == null || _layers.isEmpty() == true) {
            System.out.println("no custom layers");
            _customLayersDescs = "";
            _customLayersOverlays = "";
            _customLayersRedraws = "";
            return;
        }
        LayerDeclarationGenerator decGenerator = null;
        StringBuilder decs = new StringBuilder();
        StringBuilder overlays = new StringBuilder();
        for (CustomLayer cl : _layers) {

            decGenerator = new TemplateLayerDeclarationGenerator(cl.getVarName(),
                    _tileSize, "'<div style=\""
                            +cl.getBackgroundColorCSS()
                            +"color: "+cl.getColor()
                            +";display: inline-block;\">" 
                            + cl.getName() + "</div>'", "'" 
                                    + cl.getVarName() + "'",
                    App.LAYER_HANDLER_BASE_DIR+"/"+cl.getImagesPath(), 
                    "'/analyzing_"+cl.getColor()+".png'",
                    _overlayOpacity);
            
            decs.append(decGenerator.getLayerDeclaration()).append('\n');

            overlays.append("\"<div style='")
                    .append(cl.getBackgroundColorCSS())
                    .append("color: ")
                    .append(cl.getColor())
                    .append(";display:inline-block;'>")
                    .append(cl.getName()).append("</div>\": ")
                    .append(cl.getVarName()).append(",\n");
        }
        _customLayersDescs = decs.toString();
        _customLayersOverlays = overlays.toString();

    }
}
