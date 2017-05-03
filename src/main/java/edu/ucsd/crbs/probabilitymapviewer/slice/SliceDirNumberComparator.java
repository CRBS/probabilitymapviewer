/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.probabilitymapviewer.slice;

import java.util.Comparator;

/**
 * Compares slices by slice number
 * @author churas
 */
public class SliceDirNumberComparator implements Comparator {
    
    /**
     * Compares slices by slice number ie {@link SliceDir#getSliceNumber()} where
     * 
     * @param o1 {@link SliceDir}
     * @param o2 {@link SliceDir}
     * @return -1 if <b>o1</b> {@link SliceDir#getSliceNumber()} is null or lower 
     * then <b>o2</b> 0 if equal or both null and 1 if <b>o1</b> is larger
     */
    @Override
    public int compare(Object o1, Object o2) {
        SliceDir one = (SliceDir)o1;
        SliceDir two = (SliceDir)o2;
        
        if (one == null && two == null){
            return 0;
        }
        
        if (one == null && two != null){
            return -1;
        }
        if (one != null && two == null){
            return 1;
        }
        
        //okay both are not null.        
        if (one.getSliceNumber() < two.getSliceNumber()){
            return -1;
        }
        if (one.getSliceNumber() == two.getSliceNumber()){
            return 0;
        }
        return 1;
    }    
    
}
