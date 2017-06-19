package edu.ucsd.crbs.probabilitymapviewer.slice;

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
    
    private LinkedList<SliceDir> _slicesToReturnViaGetSlices;
    private List<SliceDir> _slicesLeft;
    private Properties _collectionProps;
    
    public SimulatedSliceMonitor(Properties props){
        SliceMonitor smi = new SliceMonitorImpl(props, null);
        try {
            _slicesLeft = smi.getSlices();
            _collectionProps = smi.getCollectionInformation();
        }
        catch(Exception ex){
            _log.log(Level.WARNING, "Caught Exception: "+ex.getMessage(),ex);
        }
        _slicesToReturnViaGetSlices = new LinkedList<SliceDir>();
        
    }
    
    @Override
    public List<SliceDir> getSlices() throws Exception {
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
