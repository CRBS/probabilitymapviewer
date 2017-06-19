package edu.ucsd.crbs.probabilitymapviewer.io;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface ResourceToFile {

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
    void writeResourceToScript(final String resourcePath, final String destinationScript, StringReplacer replacer) throws Exception;

}
