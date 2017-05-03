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
