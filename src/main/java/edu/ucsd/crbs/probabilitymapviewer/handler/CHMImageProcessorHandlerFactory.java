package edu.ucsd.crbs.probabilitymapviewer.handler;

import edu.ucsd.crbs.probabilitymapviewer.App;
import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;
import edu.ucsd.crbs.probabilitymapviewer.processor.ImageProcessorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CHMImageProcessorHandlerFactory {

    private static final Logger _log = Logger.getLogger(CHMImageProcessorHandlerFactory.class.getName());

    public List<ImageProcessorHandler> getImageProcessorHandlers(ExecutorService es, Properties props, List<CustomLayer> layers) throws Exception {

        ImageProcessorFactory ipf = new ImageProcessorFactory(props);
        ArrayList<ImageProcessorHandler> iHandlers = new ArrayList<ImageProcessorHandler>();

        if (layers != null) {
            for (CustomLayer cl : layers) {

                ImageProcessorHandler chmHandler = new ImageProcessorHandler(ipf.getImageProcessor(cl));

                String contextHandlerPath = "/" + App.LAYER_HANDLER_BASE_DIR +
                        "/" + cl.getVarName();
                ContextHandler chmContext = new ContextHandler(contextHandlerPath);
                chmContext.setHandler(chmHandler);
                chmHandler.setContextHandler(chmContext);
                iHandlers.add(chmHandler);
            }
        }

        EmptyImageProcessorHandlerFactory echf = new EmptyImageProcessorHandlerFactory();
        iHandlers.addAll(echf.getImageProcessorHandlers());

        return iHandlers;       
    }
}
