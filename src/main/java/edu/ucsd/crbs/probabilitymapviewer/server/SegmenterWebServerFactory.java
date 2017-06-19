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
