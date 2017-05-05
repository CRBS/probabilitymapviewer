/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this probabilitymapviewer for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this probabilitymapviewer into commercial products
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
 * THE probabilitymapviewer PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE probabilitymapviewer WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */

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
