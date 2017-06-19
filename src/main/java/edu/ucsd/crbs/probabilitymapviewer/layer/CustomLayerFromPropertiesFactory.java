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
