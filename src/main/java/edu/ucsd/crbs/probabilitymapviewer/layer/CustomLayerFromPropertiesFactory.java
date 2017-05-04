/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.probabilitymapviewer.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.StringReader;

import com.opencsv.CSVReader;
import edu.ucsd.crbs.probabilitymapviewer.App;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CustomLayerFromPropertiesFactory {

    public List<CustomLayer> getCustomLayers(Properties props) throws Exception{
        if (props == null){
            throw new IllegalArgumentException("props is null");
        }
        ArrayList<CustomLayer> layers = new ArrayList<CustomLayer>();
        String optargs = null;
        
        for (Object o : props.keySet()){
            String key = (String)o;
            if (!key.startsWith(App.CUSTOM_ARG)){
                continue;
            }
            StringReader sr = new StringReader(props.getProperty(key));
            CSVReader reader = new CSVReader(sr);
            List<String[]> items = reader.readAll();
            sr.close();
            reader.close();
            
            if (items.get(0).length == 4){
                optargs = items.get(0)[3];
            }
            else {
                optargs = null;
            }
            layers.add(new CustomLayer(items.get(0)[0],items.get(0)[1],
                       items.get(0)[2], optargs));
        }

        return layers;
    }
}
