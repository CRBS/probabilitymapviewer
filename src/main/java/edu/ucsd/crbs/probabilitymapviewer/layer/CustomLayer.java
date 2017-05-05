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

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CustomLayer {
    
    private static final Logger _log = Logger.getLogger(CustomLayer.class.getName());
    
    private String _script;
    private String _name;
    private String _color;
    private String _optargs;
    private String _internalDir;
    
    public CustomLayer(final String name,final String color,
            final String script,
            final String optargs){
        _script = script;
        _name = name;
        _color = color;
        
        if (optargs == null){
            _optargs = "";
        }
        else {
            _optargs = optargs;
        }
        
        if (_name == null){
            _internalDir = "uhoh";
        }
        else {
            _internalDir = _name;
        }
    }

    /**
     * Gets any optional arguments that should be passed
     * to the script
     * @return String with optional arguments or empty string
     */
    public String getOptArgs(){
        return _optargs;
    }
    
    public String getScript() {
        return _script;
    }

    public String getName() {
        return _name;
    }

    public String getColor() {
        return _color;
    }
    
    public String getVarName(){
        return _internalDir;
    }
    
    public String getImagesPath(){
        return _internalDir+"/"+getImageName();
    }
    
    public String getImageName(){
        return "{z}-r{y}_c{x}.png";
    }
    
    public String getBackgroundColorCSS(){
        if (_color == null){
            return "";
        }
        if (_color.equalsIgnoreCase("yellow")){
            return "background-color: darkgray;";
        }
        return "";
    }
    
    /**
     * Based on color passed into constructor 
     * {@link CustomLayer#CustomLayer(java.lang.String, java.lang.String, 
     * java.lang.String, java.lang.String) }
     * This method returns tile name that matches that color.
     * @return Name of tile matching color ie analyzing_blue_50opac.png.
     *         if color was null then analyzing.png is returned. 
     */
    public String getAnalyzingTile(){
        if (_color == null){
            _log.log(Level.WARNING,"No color was set");
            return "analyzing.png";
        }
        if (_color.equalsIgnoreCase("green")){
            return "analyzing_green_50opac.png";
        }
        else if (_color.equalsIgnoreCase("blue")){
            return "analyzing_blue_50opac.png";
        }
        else if (_color.equalsIgnoreCase("yellow")){
            return "analyzing_yellow_50opac.png";
        }
        else if (_color.equalsIgnoreCase("magenta")){
            return "analyzing_magenta_50opac.png";
        }
        else if (_color.equalsIgnoreCase("red")){
            return "analyzing_red_50opac.png";
        }
        else if (_color.equalsIgnoreCase("cyan")){
            return "analyzing_cyan_50opac.png";
        }
        return "analyzing_red_50opac.png";
    }
    
    /**
     * Gets color channels that the convert command should zero
     * out when converting probability map grayscale images into 
     * semi-transparent colored images. The value returned is
     * based on color value passed into constructor 
     * {@link CustomLayer#CustomLayer(java.lang.String, java.lang.String, 
     * java.lang.String, java.lang.String) }
     * 
     * @return String denoting comma delimited list of color channels 
     *         (Red,Green,Blue) to 
     *         gray out. For example, if green was the color this method 
     *         will return Red, Blue
     */
    public String getConvertColor(){
        if (_color == null){
            _log.log(Level.WARNING,"No color was set");
            return "Red,Blue";
        }
        if (_color.equalsIgnoreCase("green")){
            return "Red,Blue";
        }
        else if (_color.equalsIgnoreCase("blue")){
            return "Red,Green";
        }
        else if (_color.equalsIgnoreCase("yellow")){
            return "Blue";
        }
        else if (_color.equalsIgnoreCase("magenta")){
            return "Green";
        }
        else if (_color.equalsIgnoreCase("red")){
            return "Blue,Green";
        }
        else if (_color.equalsIgnoreCase("cyan")){
            return "Red";
        }
        return "Red";
    }
}