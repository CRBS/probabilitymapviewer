package edu.ucsd.crbs.probabilitymapviewer.io;

import static edu.ucsd.crbs.probabilitymapviewer.App.DIR_ARG;
import static edu.ucsd.crbs.probabilitymapviewer.App.TEMP_DIR_CREATED_FLAG;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates Working directory
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class WorkingDirCreatorImpl implements WorkingDirCreator{

        private static final Logger _log = Logger.getLogger(WorkingDirCreatorImpl.class.getName());

    
    /**
     * Examines <b>props</b> looking for {@value #DIR_ARG} property, if found
     * method checks if directory exists, if it does not the code attempts to
     * create it and if successful the <b>props</b> has a new property 
     * {@value #TEMP_DIR_CREATED_FLAG} is added and set to <b>true</b>
     * @param props
     * @return Working Directory even if method did not create it
     * @throws Exception If <b>props</b> is null or {@value #DIR_ARG} is not set
     *  or if there is a problem creating the directory
     */
    @Override
    public File createWorkingDir(Properties props) throws Exception {
        if (props == null){
            throw new NullPointerException("Properties is null");
        }
        String dirArg = props.getProperty(DIR_ARG);
        if (dirArg == null || dirArg.trim().equals("")){
            throw new Exception(DIR_ARG+" property not set");
        }
        
        File workingDir = new File(dirArg);

        if (workingDir.exists() == false) {
            
            _log.log(Level.INFO,"--"+DIR_ARG+" " + workingDir.getAbsolutePath() 
                    + " does not exist.  Creating directory");
            if (workingDir.mkdirs() == false) {
                throw new Exception("Unable to create " 
                        + workingDir.getAbsolutePath());
            }
            props.setProperty(TEMP_DIR_CREATED_FLAG, "true");
        }
        return workingDir;
    }

}
