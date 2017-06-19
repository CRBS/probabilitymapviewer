package edu.ucsd.crbs.probabilitymapviewer.io;

/**
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface ResourceToString {

     /**
     * Converts given resource to a String.  
     *
     * @param resourcePath Path that can be loaded via {@link Class.class.getResourceAsStream}
     * @param replacer Optional object that lets caller alter script on a line by line basis before it is written
     * @throws Exception if there is an io error
     * @throws IllegalArgumentException if either <b>resourcePath</b> or <b>destinationScript</b> are null
     * @return String with contents of resource
     */
    public String getResourceAsString(final String resourcePath, StringReplacer replacer) throws Exception;
}
