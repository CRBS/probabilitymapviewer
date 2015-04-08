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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This slice monitor slowly adds slices from a path set in constructor.
 * The delay is also set in the constructor.
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class SimulatedSliceMonitor implements SliceMonitor {

    private static final Logger _log = Logger.getLogger(SimulatedSliceMonitor.class.getName());
    
    private LinkedList<String> _slicesToReturnViaGetSlices;
    private List<String> _slicesLeft;
    private Properties _collectionProps;
    
    public SimulatedSliceMonitor(Properties props){
        SliceMonitor smi = new SliceMonitorImpl(props);
        try {
            _slicesLeft = smi.getSlices();
            _collectionProps = smi.getCollectionInformation();
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught Exception: "+ex.getMessage(),ex);
        }
        _slicesToReturnViaGetSlices = new LinkedList<String>();
        
    }
    
    @Override
    public List<String> getSlices() throws Exception {
        if (_slicesLeft == null){
            return _slicesToReturnViaGetSlices;
        }
        if (_slicesLeft.isEmpty() == false){
            _slicesToReturnViaGetSlices.add(_slicesLeft.remove(0));
        }
        return _slicesToReturnViaGetSlices;
    }

    @Override
    public Properties getCollectionInformation() throws Exception {
        return _collectionProps;
    }
}
