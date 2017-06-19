package edu.ucsd.crbs.probabilitymapviewer.server;

import edu.ucsd.crbs.probabilitymapviewer.handler.ImageProcessorHandler;
import java.util.List;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SegmenterWebServer {
    
    private List<ImageProcessorHandler> _imageProcHandlers;
    private Server _server;

    public List<ImageProcessorHandler> getImageProcHandlers() {
        return _imageProcHandlers;
    }

    public void setImageProcHandlers(List<ImageProcessorHandler> imageProcHandlers) {
        _imageProcHandlers = imageProcHandlers;
    }

    public Server getServer() {
        return _server;
    }

    public void setServer(Server server) {
        _server = server;
    }
    

}
