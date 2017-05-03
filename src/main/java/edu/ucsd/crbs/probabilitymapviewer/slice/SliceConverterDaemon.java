/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.slice;

/**
 * Daemon that converts data from image of some type to slice
 * @author churas
 */
public interface SliceConverterDaemon extends Runnable {

    /**
     * Runs the daemon
     */
    @Override
    public void run();
    
    /**
     * Tells daemon to shutdown
     */
    public void shutdown();

    /**
     * Returns the destination directory for the converted slices
     * @return Path to converted slices
     */
    public String getDestinationDirectory();

}
