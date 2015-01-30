/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this realtime-segmentation for 
 * educational, research and non-profit purposes, without fee, and without a 
 * written agreement is hereby granted, provided that the above copyright notice, 
 * this paragraph and the following three paragraphs appear in all copies.
 * 
 * Those desiring to incorporate this realtime-segmentation into commercial products
 * or use for commercial purposes should contact the Technology Transfer Office, 
 * University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, 
 * La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, 
 * E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS realtime-segmentation, EVEN IF THE UNIVERSITY 
 * OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE realtime-segmentation PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY 
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO 
 * REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE realtime-segmentation WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
 */

package edu.ucsd.crbs.realtimeseg.handler.ccdb;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Provides a list of trained chm models from CCDB.  Utilizing these REST
 * end points:
 * 
 * #List all models
 * http://elephanta.crbs.ucsd.edu:8080/CCDBSlashChmService/rest/chm_models
 *
 * #Retrieve a single model by setting the model ID equals to 5235515
 * http://elephanta.crbs.ucsd.edu:8080/CCDBSlashChmService/rest/chm_models/5235515
 *
 * #Download the model files as a zip file
 * http://elephanta.crbs.ucsd.edu:8080/CCDBSlashChmService/ChmModelDownloadServlet?id=5235515
 * 
 * 
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CcdbChmTrainedModelListHandler extends AbstractHandler {

    private static final Logger _log = Logger.getLogger(CcdbChmTrainedModelListHandler.class.getName());
    
    private String _restURL;
    
    public CcdbChmTrainedModelListHandler(String url){
        _restURL = url;
    }
    
    /**
     * Handles CCDB web requests from web client
     * @param string
     * @param rqst
     * @param hsr
     * @param hsr1
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void handle(String string, Request request, 
            HttpServletRequest servletRequest, 
            HttpServletResponse servletResponse) throws IOException, ServletException {
        
        _log.log(Level.INFO, servletRequest.getRequestURI());
        _log.log(Level.INFO,servletRequest.getQueryString());
        
        //Map<String,String[]> pMap = servletRequest.getParameterMap();
        
        //for (String s : pMap.keySet()){
        //    _log.log(Level.INFO,"("+s+")");
       // }
        //@TODO REPLACE WITH call to ccdb web service.  Ideally in constructor to 
        // minimize user delay
        String responseString = "[{ \"id\": \"5235515\", \"name\": \"lysosome_D4_L2_S2\" },"
                + "{ \"id\": \"5235516\", \"name\": \"lysosome_D6_L1_S2\" }]";
        
        
         servletResponse.setContentType("application/json");
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.getWriter().write(responseString);
            servletResponse.setStatus(HttpServletResponse.SC_OK);
            
            request.setHandled(true);
    }
}
