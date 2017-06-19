package edu.ucsd.crbs.probabilitymapviewer.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * Reads in a resource and writes it out as a script with logic that enables
 * caller to adjust each line of text before it is written
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ResourceToFileImpl implements ResourceToFile {
    
   
    /**
     * Writes a given resource to an executable script file.  The result is written to
     * <b>destinationScript</b> and this script is made executable via File setExecutable
     * method.
     * 
     * @param resourcePath Path that can be loaded via {@link Class.class.getResourceAsStream} 
     * @param destinationScript File to write script to
     * @param replacer Optional object that lets caller alter script on a line by line basis before it is written
     * @throws Exception if there is an io error
     * @throws IllegalArgumentException if either <b>resourcePath</b> or <b>destinationScript</b> are null
     */
    @Override
    public void writeResourceToScript(final String resourcePath,final String destinationScript,StringReplacer replacer) throws Exception {
        if (resourcePath == null){
            throw new IllegalArgumentException("resourcePath method parameter cannot be null");
        }
         
        if (destinationScript == null){
            throw new IllegalArgumentException("destinationScript method parameter cannot be null");
        }
        
        //load script
        List<String> scriptLines = IOUtils.readLines(Class.class.getResourceAsStream(resourcePath));

        BufferedWriter bw = new BufferedWriter(new FileWriter(destinationScript));
                        
        for (String line : scriptLines){
            if (replacer != null){
                bw.write(replacer.replace(line));
            }
            else {
                bw.write(line);
            }
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
}
