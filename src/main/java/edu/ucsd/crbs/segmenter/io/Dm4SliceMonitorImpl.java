/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.io;

import java.util.List;
import java.util.Properties;

/**
 * Slice monitor that watches, via thread, for dm4 files.  Any dm4 files
 * found are put in list ordered by age and the thread iterates through them
 * one at a time converting them to tiled png images stored in slice_####
 * directories that can be consumed by Segmenter.  
 * @author churas
 */
public class Dm4SliceMonitorImpl implements SliceMonitor{

    private Thread _daemon = null;
    private SliceMonitor _sliceMonitor = null;
        
    /**
     * Constructor
     * @param props 
     * @param scd 
     */
    public Dm4SliceMonitorImpl(Properties props, SliceConverterDaemon scd) {
        _sliceMonitor = new SliceMonitorImpl(props);
        _daemon = new Thread(scd);
        _daemon.setDaemon(true);
        _daemon.start();
    }

    
    @Override
    public List<String> getSlices() throws Exception {
        return _sliceMonitor.getSlices();
    }

    @Override
    public Properties getCollectionInformation() throws Exception {
        return _sliceMonitor.getCollectionInformation();
    }
    
}
