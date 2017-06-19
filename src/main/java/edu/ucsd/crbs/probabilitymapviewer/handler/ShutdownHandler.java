package edu.ucsd.crbs.probabilitymapviewer.handler;

import edu.ucsd.crbs.probabilitymapviewer.App;
import java.io.IOException;
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
public class ShutdownHandler extends AbstractHandler {

    private static final Logger _log = Logger.getLogger(ShutdownHandler.class.getName());
    
    @Override
    public void handle(String string, Request request, 
            HttpServletRequest servletRequest, 
            HttpServletResponse servletResponse) throws IOException, ServletException {
        
        _log.log(Level.INFO,"Shutdown handler called with this url {0},"
                + " ignoring request",
                request.getRequestURI());
        
        //App.SIGNAL_RECEIVED = true;
    }
    
    

}
