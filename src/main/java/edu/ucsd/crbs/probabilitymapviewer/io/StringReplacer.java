package edu.ucsd.crbs.probabilitymapviewer.io;

/**
 * Implementing objects allow caller invoke {@link replace} method which takes
 * a line of text and returns the original line or a "replaced" version
 * 
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public interface StringReplacer {
 
    /**
     * Given a line of text this method will supply the identical line or
     * a different line.
     * @param line
     * @return Same string or a new possibly modified String
     */
    public String replace(final String line);
    
}
