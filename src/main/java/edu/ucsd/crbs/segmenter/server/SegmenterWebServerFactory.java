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

package edu.ucsd.crbs.segmenter.server;


import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.handler.CHMImageProcessorHandlerFactory;
import edu.ucsd.crbs.segmenter.handler.ImageProcessorHandler;
import edu.ucsd.crbs.segmenter.handler.ShutdownHandler;
import edu.ucsd.crbs.segmenter.handler.StatusHandler;
import edu.ucsd.crbs.segmenter.handler.ccdb.CcdbAddChmTrainedModelHandler;
import edu.ucsd.crbs.segmenter.handler.ccdb.CcdbChmTrainedModelListHandler;
import edu.ucsd.crbs.segmenter.handler.ccdb.model.ModelDownloaderImpl;
import edu.ucsd.crbs.segmenter.layer.CustomLayer;
import edu.ucsd.crbs.segmenter.layer.CustomLayerFromCCDBFactory;
import edu.ucsd.crbs.segmenter.processor.ImageProcessorFactory;
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
        inputImageHandler.setResourceBase(props.getProperty(App.INPUT_IMAGE_ARG));
        ContextHandler imageContext = new ContextHandler("/images");
        imageContext.setHandler(inputImageHandler);
        contexts.addHandler(imageContext);
        
        ResourceHandler workingDirHandler = new ResourceHandler();
        workingDirHandler.setDirectoriesListed(true);
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
        
        CcdbChmTrainedModelListHandler ccdbHandler = new CcdbChmTrainedModelListHandler(props.getProperty(App.CCDB_ARG));
        ContextHandler ccdbContext = new ContextHandler("/ccdb/chm_models");
        ccdbContext.setHandler(ccdbHandler);
        contexts.addHandler(ccdbContext);
        
        ModelDownloaderImpl mdi = new ModelDownloaderImpl(props.getProperty(App.CCDB_ARG),
        props.getProperty(App.LAYER_MODEL_BASE_DIR));
        
        CcdbAddChmTrainedModelHandler ccdbAddHandler = new CcdbAddChmTrainedModelHandler(
                props.getProperty(App.CCDB_ARG),"0","0","0",
                props.getProperty(App.TILE_SIZE_ARG),
                props.getProperty(App.OVERLAY_OPACITY_ARG),
                mdi,
                new ImageProcessorFactory(props),
                new CustomLayerFromCCDBFactory(props));
        
        ContextHandler ccdbAddContext = new ContextHandler("/ccdb/add_chm_layer");
        ccdbAddContext.setHandler(ccdbAddHandler);
        contexts.addHandler(ccdbAddContext);
       
        CHMImageProcessorHandlerFactory chf = new CHMImageProcessorHandlerFactory();
        List<ImageProcessorHandler> handlers = chf.getImageProcessorHandlers(es, props, layers);
        if (handlers != null && !handlers.isEmpty()) {
            ccdbAddHandler.setProcessingContextHandlers(handlers);
            for (ImageProcessorHandler iph : handlers) {
                contexts.addHandler(iph.getContextHandler());
            }
        }

        server.setHandler(contexts);

        return sws;
        
        
    }

}
