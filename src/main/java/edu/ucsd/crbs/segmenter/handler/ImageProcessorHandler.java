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

package edu.ucsd.crbs.segmenter.handler;

import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.processor.ImageProcessor;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 * Runs CHM on any requests that match the tile format
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ImageProcessorHandler extends AbstractHandler {

    
    private static final Logger _log = Logger.getLogger(ImageProcessorHandler.class.getName());
    private ImageProcessor _processor;
    private ContextHandler _contextHandler;
    
    private HashSet<String> _imagesToProcess = new HashSet<String>();
    
    public ImageProcessorHandler(ImageProcessor processor){
        _processor = processor;
    }

    public void setImageProcessor(ImageProcessor processor){
        _processor = processor;
    }
    
    public ImageProcessor getImageProcessor(){
        return _processor;
    }

    public void clearProcessedImages(){
        _imagesToProcess.clear();
    }
    
    public void setContextHandler(ContextHandler cHandler){
        _contextHandler = cHandler;
    }
    
    public ContextHandler getContextHandler(){
        return _contextHandler;
    }
    
   /**
    * Sends any tiles not already in the list to the {@link ImageProcessor} defined
    * in the constructor of this object.  Only tiles that match the pattern
    * <code>^[0-9]+-r[0-9]+_c[0-9]+\\.png$</code> are passed to the {@link ImageProcessor}
    * This method does <b>NOT</b> handle the request at all or provide a response,
    *  it is merely an observer
    * @param string
    * @param request
    * @param servletRequest
    * @param servletResponse
    * @throws IOException
    * @throws ServletException 
    */ 
    public void handle(String string, Request request, 
            HttpServletRequest servletRequest, 
            HttpServletResponse servletResponse) throws IOException, ServletException {

        if (_processor == null){
            request.setHandled(false);
            return;
        }
        
        int slashPos = servletRequest.getRequestURI().lastIndexOf('/');
        String imageToProcess = servletRequest.getRequestURI().substring(slashPos+1);
        
        //only process images with non negative positions
        if (imageToProcess.matches("^[0-9]+-r[0-9]+_c[0-9]+\\.png$")){
            if (App.latestSlice != null && !App.latestSlice.equals("")){
                int slicePos = servletRequest.getRequestURI().lastIndexOf(App.latestSlice);
                imageToProcess = servletRequest.getRequestURI().substring(slicePos);
            }
            if (!_imagesToProcess.contains(imageToProcess)){
            //submit job
                _processor.process(imageToProcess);
                _imagesToProcess.add(imageToProcess);
            }
        }
        request.setHandled(false);
    }
}
