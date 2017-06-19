package edu.ucsd.crbs.probabilitymapviewer.html;

/**
 * Generates Tile Layer declaraction javascript
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface LayerDeclarationGenerator {

    public String getLayerDeclaration() throws Exception;
    
}
