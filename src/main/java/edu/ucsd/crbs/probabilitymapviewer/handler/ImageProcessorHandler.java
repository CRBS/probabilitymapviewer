package edu.ucsd.crbs.probabilitymapviewer.handler;

import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.processor.ImageProcessor;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
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
                _log.log(Level.FINE,servletRequest.getRequestURI() 
                        + " & Latest slice is: " + App.latestSlice);
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
