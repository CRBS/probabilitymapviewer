/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

/**
 * Daemon that converts data from image of some type to slice
 * @author churas
 */
public interface SliceConverterDaemon extends Runnable {

    @Override
    public void run();
    
    /**
     * Tells daemon to shutdown
     */
    public void shutdown();
}
