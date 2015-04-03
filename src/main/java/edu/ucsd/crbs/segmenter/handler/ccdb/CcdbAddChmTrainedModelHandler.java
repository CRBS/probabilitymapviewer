/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.segmenter.handler.ccdb;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import edu.ucsd.crbs.segmenter.handler.ImageProcessorHandler;
import edu.ucsd.crbs.segmenter.handler.ccdb.model.ModelDownloader;
import edu.ucsd.crbs.segmenter.layer.CustomLayer;
import edu.ucsd.crbs.segmenter.layer.CustomLayerFromCCDBFactory;
import edu.ucsd.crbs.segmenter.processor.ImageProcessor;
import edu.ucsd.crbs.segmenter.processor.ImageProcessorFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CcdbAddChmTrainedModelHandler extends AbstractHandler {

    private static final Logger _log = Logger.getLogger(CcdbAddChmTrainedModelHandler.class.getName());
    
    public static final String ID_KEY = "id";
    public static final String COLOR_KEY = "color";
    
    
    private String _restURL;
    private List<ImageProcessorHandler> _imageProcessorHandlerList;
    private String _minZoom;
    private String _maxZoom;
    private String _maxNativeZoom;
    private String _tileSize;
    private String _opacity;
    private ModelDownloader _modelDownloader;
    private ImageProcessorFactory _imageProcessorFactory;
    private CustomLayerFromCCDBFactory _customLayerFactory;
    
    public CcdbAddChmTrainedModelHandler(final String url,final String minZoom,
            final String maxZoom,final String maxNativeZoom,final String tileSize,
            final String opacity,
            ModelDownloader downloader,
            ImageProcessorFactory imageProcessorFactory,
            CustomLayerFromCCDBFactory customLayerFactory){
        _restURL = url;
        _minZoom = minZoom;
        _maxZoom = maxZoom;
        _maxNativeZoom = maxNativeZoom;
        _tileSize = tileSize;
        _opacity = opacity;
        _modelDownloader = downloader;
        _imageProcessorFactory = imageProcessorFactory;
        _customLayerFactory = customLayerFactory;
    }
    
    public void setProcessingContextHandlers(List<ImageProcessorHandler> imageProcessorHandlerList){
        _imageProcessorHandlerList = imageProcessorHandlerList;
    }
    
    @Override
    public void handle(String string, Request request, 
            HttpServletRequest servletRequest, 
            HttpServletResponse servletResponse) throws IOException, ServletException {
        
         _log.log(Level.INFO, servletRequest.getRequestURI());
        _log.log(Level.INFO,servletRequest.getQueryString());
        
        //need to parse out id and color 
        Map<String,String[]> queryParameters = servletRequest.getParameterMap();
        String id = parseIdFromMap(queryParameters);
        String color = parseColorFromMap(queryParameters);
        
        servletResponse.setContentType("application/json");
        servletResponse.setCharacterEncoding("UTF-8");
        
        if (id == null){
            String resp = "{ \"error\": \"No id passed in\"}";
            servletResponse.getWriter().write(resp);
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setHandled(true);
            return;
        }
        if (color == null){
            String resp = "{ \"error\": \"No color set\"}";
            servletResponse.getWriter().write(resp);
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setHandled(true);
            return;
        }
        
        //find an available handler 
        ImageProcessorHandler iHandler = getAvailableImageProcessorHandler();
        if (iHandler == null){
            String resp = "{ \"error\": \"Unable to find handler\"}";
            servletResponse.getWriter().write(resp);
            servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setHandled(true);
            return;
        }
        
        //download the model
        _log.log(Level.INFO,"Found handler and this is the path: {0}",
                iHandler.getContextHandler().getContextPath());
        
        String handlerDir = iHandler.getContextHandler().getContextPath().replaceAll("^.*\\/", "");
        
        File destDir;
        try {
        //download the model
        destDir = _modelDownloader.downloadModel(id,
                "/"+handlerDir);
        }
        catch(Exception ex){
            String resp = "{ \"error\": \""+ex.getMessage()+"\"}";
            servletResponse.getWriter().write(resp);
            servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setHandled(true);
            return;
        }
        
        //Update the handler with a new image processor
        CustomLayer cl = _customLayerFactory.getCustomLayer(handlerDir, destDir.getAbsolutePath(), color);
        ImageProcessor ip = _imageProcessorFactory.getImageProcessor(cl);
        iHandler.setImageProcessor(ip);
        
        //generate json response so client can add the layer
        String resp = "{ \"layerPath\": \""+iHandler.getContextHandler().getContextPath()+"/{z}-r{y}_c{x}.png\","
                    + "\"minZoom\": "+_minZoom+","
                    + "\"maxZoom\": "+_maxZoom+","
                    + "\"maxNativeZoom\": "+_maxNativeZoom+","
                    + "\"tileSize\": "+_tileSize+","
                    + "\"opacity\": "+_opacity+","
                    + "\"backgroundcss\": \""+cl.getBackgroundColorCSS()+"\"}";
            
        servletResponse.getWriter().write(resp);
        servletResponse.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
    }
    
    private String parseIdFromMap(Map<String,String[]> theMap){
        return parseFirstElementFromMap(theMap,ID_KEY);
    }
    
    private String parseColorFromMap(Map<String,String[]> theMap){
        return parseFirstElementFromMap(theMap,COLOR_KEY);
    }
    
    private String parseFirstElementFromMap(Map<String,String[]> theMap,final String key){
        if (theMap == null || theMap.isEmpty()){
            return null;
        }
        
        if (theMap.containsKey(key)){
            String[] vals = theMap.get(key);
            if (vals == null || vals.length == 0){
                return null;
            }
            return vals[0];
        }
        return null;
    }
    
    private ImageProcessorHandler getAvailableImageProcessorHandler(){
        
        if (_imageProcessorHandlerList == null || _imageProcessorHandlerList.isEmpty()){
            return null;
        }
        for (ImageProcessorHandler iph : _imageProcessorHandlerList){
            if (iph.getImageProcessor() == null){
                return iph;
            }
        }
        
       return null; 
    }

}
