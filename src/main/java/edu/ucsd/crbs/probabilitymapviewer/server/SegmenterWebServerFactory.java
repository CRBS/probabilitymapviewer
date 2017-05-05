/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.probabilitymapviewer.server;


import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.handler.CHMImageProcessorHandlerFactory;
import edu.ucsd.crbs.probabilitymapviewer.handler.ImageProcessorHandler;
import edu.ucsd.crbs.probabilitymapviewer.handler.ShutdownHandler;
import edu.ucsd.crbs.probabilitymapviewer.handler.StatusHandler;
import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SegmenterWebServerFactory {
    
    public SegmenterWebServer getSegmenterWebServer(ExecutorService es,Properties props,List<CustomLayer> layers) throws Exception {
        
        SegmenterWebServer sws = new SegmenterWebServer();
         // Create a basic Jetty server object that will listen on port 8080.  Note that if you set this to port 0
        // then a randomly available port will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        Server server = new Server(Integer.parseInt(props.getProperty(App.PORT_ARG)));
        sws.setServer(server);
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        
        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        ResourceHandler inputImageHandler = new ResourceHandler();
        inputImageHandler.setDirectoriesListed(true);
        
        inputImageHandler.setResourceBase(props.getProperty(App.ADJUSTED_INPUT_IMAGE_ARG));
        
        ContextHandler imageContext = new ContextHandler("/"+App.IMAGES_CONTEXT_PATH);
        imageContext.setHandler(inputImageHandler);
        contexts.addHandler(imageContext);
        
        ResourceHandler workingDirHandler = new ResourceHandler();
        workingDirHandler.setDirectoriesListed(true);
        
        // if we denote what tiles are being analyzed we need to disable
        // caching
        if (props.getProperty(App.DISABLE_ANALYZING_TILE_ARG,"false").equals("false")){
            workingDirHandler.setCacheControl("no-cache, no-store, must-revalidate");
        }
        
        workingDirHandler.setResourceBase(props.getProperty(App.DIR_ARG));
        workingDirHandler.setWelcomeFiles(new String[]{"index.html"});
        ContextHandler workingDirContext = new ContextHandler("/");
        workingDirContext.setHandler(workingDirHandler);
        contexts.addHandler(workingDirContext);
        
        
        StatusHandler statusHandler = new StatusHandler(Integer.parseInt(props.getProperty(App.NUM_CORES_ARG)));
        ContextHandler statusContext = new ContextHandler("/status");
        statusContext.setHandler(statusHandler);
        contexts.addHandler(statusContext);
        
        
        ShutdownHandler shutdownHandler = new ShutdownHandler();
        ContextHandler shutdownContext = new ContextHandler("/shutdown");
        shutdownContext.setHandler(shutdownHandler);
        
        contexts.addHandler(shutdownContext);
        
        CHMImageProcessorHandlerFactory chf = new CHMImageProcessorHandlerFactory();
        List<ImageProcessorHandler> handlers = chf.getImageProcessorHandlers(es, props, layers);
        if (handlers != null && !handlers.isEmpty()) {
            for (ImageProcessorHandler iph : handlers) {
                contexts.addHandler(iph.getContextHandler());
            }
        }

        server.setHandler(contexts);

        return sws;
        
        
    }

}
