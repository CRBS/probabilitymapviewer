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

package edu.ucsd.crbs.segmenter.layer;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CustomLayer {
    
    private static final Logger _log = Logger.getLogger(CustomLayer.class.getName());
    
    private String _trainedModelDir;
    private String _name;
    private String _color;
    private String _binary;
    private String _internalDir;
    
    public CustomLayer(final String trainedModelDir,final String name,
            final String color,final String binary){
        _trainedModelDir = trainedModelDir;
        _name = name;
        _color = color;
        _binary = binary;
        if (_name == null){
            _internalDir = "uhoh";
        }
        else {
            _internalDir = _name;
        }
    }

    public String getTrainedModelDir() {
        return _trainedModelDir;
    }

    public String getName() {
        return _name;
    }

    public String getColor() {
        return _color;
    }

    public String getBinary() {
        return _binary;
    }
    
    public String getVarName(){
        return _internalDir;
    }
    
    public String getImagesPath(){
        return _internalDir+"/{z}-r{y}_c{x}.png";
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
