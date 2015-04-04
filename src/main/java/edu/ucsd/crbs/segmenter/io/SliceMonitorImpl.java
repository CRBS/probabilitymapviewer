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

package edu.ucsd.crbs.segmenter.io;

import edu.ucsd.crbs.segmenter.App;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

/**
 * Gets current list of slices by examining {@link edu.ucsd.crbs.segmenter.App#INPUT_IMAGE_ARG}
 * directory for sub directories with name (need to fill in)
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SliceMonitorImpl implements SliceMonitor,Comparator {

    private Properties _props;

    public static String SLICE_PREFIX = "slice";
    
    
    public SliceMonitorImpl(Properties props){
        _props = props;
    }
    
    @Override
    public List<String> getSlices() throws Exception{
        if (_props == null){
            throw new NullPointerException("Properties passed in constructor is null");
        }
        
        String inputImage = _props.getProperty(App.INPUT_IMAGE_ARG);
        if (inputImage == null){
            throw new NullPointerException("INPUT_IMAGE_ARG property is null");
        }
        
        TreeSet<String> sliceList = new TreeSet<String>(this);
        
        File imageDir = new File(_props.getProperty(App.INPUT_IMAGE_ARG));
        File[] sliceDirs = imageDir.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        
        if (sliceDirs == null ||
            sliceDirs.length == 0){
            sliceList.add("");
            return new ArrayList<String>(sliceList);
        }
        
        for (int i = 0; i < sliceDirs.length; i++){
            if (sliceDirs[i].getName().startsWith(SLICE_PREFIX)){
                sliceList.add(sliceDirs[i].getName());
            }
        }        
        
        return new ArrayList<String>(sliceList);
    }

    @Override
    public int compare(Object o1, Object o2) {
        String one = (String)o1;
        String two = (String)o2;
        
        if (one == null && two == null){
            return 0;
        }
        
        if (one == null && two != null){
            return -1;
        }
        if (one != null && two == null){
            return -1;
        }
        
        //okay both are not null.
        int oneInt = 0;
        int twoInt = 0;
        try {
            oneInt = Integer.parseInt(lopOffPrefix(one));
        }
        catch(NumberFormatException nfe){
           oneInt = -1;   
        }   
        
        try {
            twoInt = Integer.parseInt(lopOffPrefix(two));
        }
        catch(NumberFormatException nfe){
            twoInt = -1;
        }
        
        if (oneInt < twoInt){
            return -1;
        }
        if (oneInt == twoInt){
            return 0;
        }
        return 1;
    }
    
    private String lopOffPrefix(final String val){
        int prefixPos = val.lastIndexOf("_");
        if (prefixPos < 0){
            return val;
        }
        return val.substring(prefixPos+1);
    }
    
    

}
