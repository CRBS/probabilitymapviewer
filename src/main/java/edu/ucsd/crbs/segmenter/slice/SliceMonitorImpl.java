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

package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.App;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

/**
 * Gets current list of slices by examining {@link edu.ucsd.crbs.segmenter.App#INPUT_IMAGE_ARG}
 * directory for sub directories with name (need to fill in)
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SliceMonitorImpl implements SliceMonitor {

    
    private static final Logger _log = Logger.getLogger(SliceMonitorImpl.class.getName());
    
    private Properties _props;
    private String _inputImage;    
    private Comparator _comparator;

    public static final String COLLECTION_PROPS_README = "readme.props";
    
    public SliceMonitorImpl(Properties props,Comparator comparator){
        _props = props;
        
        if (comparator == null){
            _comparator = new SliceDirNumberComparator();
        }
        if (_props == null){
            throw new NullPointerException("Properties passed in constructor is null");
        }
        _inputImage = _props.getProperty(App.INPUT_IMAGE_ARG);
        if (_inputImage == null){
            throw new NullPointerException("INPUT_IMAGE_ARG property is null");
        }        
    }
    
    @Override
    public List<SliceDir> getSlices() throws Exception{
        if (_inputImage == null){
            throw new NullPointerException("INPUT_IMAGE_ARG property is null");
        }
        
        TreeSet<SliceDir> sliceList = 
                new TreeSet<SliceDir>(_comparator);
        
        File imageDir = new File(_inputImage);
        File[] sliceDirs = imageDir.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        
        if (sliceDirs == null ||
            sliceDirs.length == 0){
            return new ArrayList<SliceDir>(sliceList);
        }
        
        for (int i = 0; i < sliceDirs.length; i++){
            if (sliceDirs[i].getName().startsWith(SliceDir.SLICE_PREFIX)){
                sliceList.add(new SliceDir(sliceDirs[i].getAbsolutePath()));
            }
        }        
        
        return new ArrayList<SliceDir>(sliceList);
    }

    @Override
    public Properties getCollectionInformation() throws Exception {
        if (_inputImage == null) {
            _log.log(Level.WARNING,"Input Image path is null");
            return null;
        }
        try {
            File readmeFile = new File(_inputImage + File.separator + COLLECTION_PROPS_README);
            if (readmeFile.isFile()) {
                Properties collectionProps = new Properties();
                collectionProps.load(new FileReader(readmeFile));
                return collectionProps;
            }
        } catch (Exception ex) {
            _log.log(Level.WARNING, "caught exception trying to load "
                    + COLLECTION_PROPS_README + " file: " + ex.getMessage(),
                    ex);
        }
        return null;
    }
}
