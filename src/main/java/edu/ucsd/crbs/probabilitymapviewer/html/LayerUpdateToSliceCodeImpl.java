package edu.ucsd.crbs.probabilitymapviewer.html;

import edu.ucsd.crbs.probabilitymapviewer.layer.CustomLayer;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class LayerUpdateToSliceCodeImpl implements LayerUpdateToSliceCode{

    private CustomLayer _layer;
    
    LayerUpdateToSliceCodeImpl(CustomLayer layer){
        _layer = layer;
    }
    
    @Override
    public String getLayerUpdateToSliceCode() {
        if (_layer == null){
            return "";
        }
        if (_layer.getVarName() == null || _layer.getVarName().trim().equals("")){
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(_layer.getVarName());
        sb.append(".setUrl('layerhandlers/");
        sb.append(_layer.getVarName());
        sb.append("/'+sliceName+'/");
        sb.append(_layer.getImageName());
        sb.append("');\n");
        return sb.toString();
    }
}

