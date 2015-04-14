/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2015 The Regents of the University of California All Rights Reserved
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

package edu.ucsd.crbs.segmenter.util;

import edu.ucsd.crbs.segmenter.App;
import java.util.Properties;

/**
 * Based on number of slices collected instances of this class
 * let caller know which cube to display in cube progress bar image
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class CubeProgressBarImpl implements CubeProgressBar {

    private int _expectedSlices;
    
    public static final String CUBES_PREFIX_PATH="cubes/";
    
    public CubeProgressBarImpl(Properties props){
        if (props == null){
            _expectedSlices = 1000;
            return;
        }
        _expectedSlices = Integer.parseInt(props.getProperty(App.EXPECTED_SLICES_ARG,"1000"));
    }
    
    public void setExpectedSlices(int val){
        _expectedSlices = val;
    }
    
    /**
     * Returns appropriate cube to display based on progress of collection. 
     * @param slicesCollected
     * @return 
     */
    @Override
    public String getCubeImage(int slicesCollected) {
        if (slicesCollected <= 0 || _expectedSlices == 0){
            return CUBES_PREFIX_PATH+"cube.png";
        }
        
        double percentComplete = (double)slicesCollected/(double)_expectedSlices;
        percentComplete*=10;
        percentComplete = Math.floor(percentComplete);
        long percentAsLong = Math.round(percentComplete)*10;
        if (percentAsLong < 10){
            percentAsLong = 10L;
        } else if (percentAsLong > 100){
            percentAsLong = 100L;
        }
        
        
        return CUBES_PREFIX_PATH+"cube"+Long.toString(percentAsLong)+".png";
    }
}
