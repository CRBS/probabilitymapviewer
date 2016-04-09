/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.io;

import java.util.List;

/**
 *
 * @author churas
 */
public class Dm4SliceConverterDaemon implements SliceConverterDaemon {

    public Dm4SliceConverterDaemon() {
       //need to set directory to watch,
       //slice_### should go, starting slice#
       //flag if should start from fresh or continue
       //
    }

    
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); 
        /**
         while(1){
         * updatedm4filelist()
         * dm4 = getnextdm4file()
         * if dm4 is null sleep & continue
         * dest = gendestpath()
         * _sliceconverter.convert(dm4,dest)
         * slice_num++
         * updatemapping()
         * add_to_complete_list(dm4)
         * sleep
         */
    }
    
    public String getNextDm4File(){
        return null; //return todo_list.remove(oldest);
    }
    
    public List<String> getdm4list(){
        return null; 
        // do list dir on watcher dir
        // and get files with .dm4 ending
    }
    
}
