/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.App;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

/**
 * Slice monitor that watches, via thread, for dm4 files.  Any dm4 files
 * found are put in list ordered by age and the thread iterates through them
 * one at a time converting them to tiled png images stored in slice_####
 * directories that can be consumed by Segmenter.  
 * @author churas
 */
public class Dm4SliceMonitorImpl implements SliceMonitor{

    
    private static final Logger _log = 
            Logger.getLogger(Dm4SliceMonitorImpl.class.getName());
    
    private Thread _daemon = null;
    private SliceMonitor _sliceMonitor = null;
    private Comparator _comparator;
    private String _inputImage = null;
    /**
     * Constructor that runs the <b>scd</b> passed in under a new thread in
     * {@link Thread#setDaemon(boolean) } set to true.  In addition, code
     * extracts path from {@link SliceConverterDaemon#getDestinationDirectory()} 
     * from <b>scd</b> passed in which is used in {@link #getSlices()} when 
     * looking for new slices
     * @param scd 
     */
    public Dm4SliceMonitorImpl(SliceConverterDaemon scd) {
        _comparator = new SliceDirLastModifiedComparator();
        _inputImage = scd.getDestinationDirectory();
        _daemon = new Thread(scd);
        _log.log(Level.INFO,"Starting thread... {0}", _daemon.getId());
        
        _daemon.setDaemon(true);
        _daemon.start();
    }

    
    /**
     * Examines destination directory obtained from 
     * {@link SliceConverterDaemon#getDestinationDirectory()} set in constructor
     * for directories that start with {@link SliceDir#SLICE_PREFIX} and do
     * <b>NOT</b> end with {@link Dm4ToSliceConverter#TMP_SUFFIX} suffix to get
     * a list of slices.
     * @return empty list if no directories match otherwise list of 
     * {@link SliceDir} objects sorted by age with oldest at start of list.
     * @throws NullPointerException if destination directory to search is 
     * <code>null</code>
     * @throws Exception if there is an IO error examining the directory
     */
    @Override
    public List<SliceDir> getSlices() throws Exception {
        _log.log(Level.INFO,"Request to getSlices()");
        if (_inputImage == null){
            throw new NullPointerException("Path to input images is null");
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
            if (sliceDirs[i].getName().startsWith(SliceDir.SLICE_PREFIX) &&
                !sliceDirs[i].getName()
                        .endsWith(Dm4ToSliceConverter.TMP_SUFFIX)){
                sliceList.add(new SliceDir(sliceDirs[i].getAbsolutePath()));
            }
        }
        return new ArrayList<SliceDir>(sliceList);
    }

    /**
     * Gets information about collection
     * @return always returns <code>null</b>
     * @throws Exception 
     */
    @Override
    public Properties getCollectionInformation() throws Exception {
        _log.log(Level.INFO,"Request to get collection information");
        return null;
    }
}
