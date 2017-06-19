package edu.ucsd.crbs.probabilitymapviewer.handler;

import edu.ucsd.crbs.probabilitymapviewer.App;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 * Instances of this factory create 10 {@link ContextHandler}s that don't do
 * anything, but can be used later
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class EmptyImageProcessorHandlerFactory {
    
    public List<ImageProcessorHandler> getImageProcessorHandlers(){
        //add 10 more handlers that don't do anything just yet
        ArrayList<ImageProcessorHandler> iHandlers = new ArrayList<ImageProcessorHandler>();
        for (int i  = 1 ; i <= 10; i++){
            ContextHandler chmContext = new ContextHandler("/"+App.LAYER_HANDLER_BASE_DIR+"/"+i);
            ImageProcessorHandler iHandler = new ImageProcessorHandler(null);
            chmContext.setHandler(iHandler);
            iHandler.setContextHandler(chmContext);
            iHandlers.add(iHandler);
        }
        return iHandlers;
    }

}
