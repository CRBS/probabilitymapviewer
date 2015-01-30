/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.realtimeseg.handler;

import edu.ucsd.crbs.realtimeseg.App;
import edu.ucsd.crbs.realtimeseg.layer.CustomLayer;
import edu.ucsd.crbs.realtimeseg.util.ImageProcessor;
import edu.ucsd.crbs.realtimeseg.util.SimpleCHMImageProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ContextHandlerFactory {

    
    public List<ContextHandler> getContextHandlers(ExecutorService es,Properties props, List<CustomLayer> layers) throws Exception {
        if (layers == null || layers.isEmpty()){
            return null;
        }
        
        ArrayList<ContextHandler> cHandlers = new ArrayList<ContextHandler>();
        for (CustomLayer cl : layers){
            ImageProcessor imageProc = new SimpleCHMImageProcessor(props.getProperty(App.INPUT_IMAGE_ARG),
                    props.getProperty(App.LAYER_HANDLER_BASE_DIR)+File.separator+cl.getVarName(),
                    cl.getTrainedModelDir(),
                    props.getProperty(App.CHM_BIN_ARG)+File.separator+"CHM_test.sh",
                    props.getProperty(App.MATLAB_ARG),cl.getConvertColor(),
                    props.getProperty(App.TILE_SIZE_ARG));
            CHMHandler chmHandler = new CHMHandler(imageProc);
            ContextHandler chmContext = new ContextHandler("/"+App.LAYER_HANDLER_BASE_DIR+"/"+cl.getVarName());
            chmContext.setHandler(chmHandler);
            
            cHandlers.add(chmContext);
        }
        
        EmptyContextHandlersFactory echf = new EmptyContextHandlersFactory();
        cHandlers.addAll(echf.getContextHandlers());
        
        return cHandlers;
    }
}
