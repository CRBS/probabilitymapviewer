/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import java.util.Comparator;

/**
 * Compares SliceDir by lastModified value
 * @author churas
 */
public class SliceDirLastModifiedComparator implements Comparator {
    
    /**
     * Compares slices by last modified ie {@link SliceDir#getLastModified() } 
     * where
     * 
     * @param o1 {@link SliceDir}
     * @param o2 {@link SliceDir}
     * @return -1 if <b>o1</b> {@link SliceDir#getLastModified() } is null
     * or lower 
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
        if (one.getLastModified() < two.getLastModified()){
            return -1;
        }
        if (one.getLastModified() == two.getLastModified()){
            return 0;
        }
        return 1;
    }    
}
