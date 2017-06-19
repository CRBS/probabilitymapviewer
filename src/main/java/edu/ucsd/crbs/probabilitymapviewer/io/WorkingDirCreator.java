package edu.ucsd.crbs.probabilitymapviewer.io;

import java.io.File;
import java.util.Properties;

/**
 * Defines method to create working directory
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface WorkingDirCreator {

    /**
     * Creates working directory based on values in <b>props</b> parameter
     * @param props
     * @return Created working directory
     * @throws Exception 
     */
    public File createWorkingDir(Properties props) throws Exception;
}
