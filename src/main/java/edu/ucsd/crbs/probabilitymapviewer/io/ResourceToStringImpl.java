package edu.ucsd.crbs.probabilitymapviewer.io;

import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class ResourceToStringImpl implements ResourceToString {

    @Override
    public String getResourceAsString(String resourcePath, StringReplacer replacer) throws Exception {
          if (resourcePath == null){
            throw new IllegalArgumentException("resourcePath method parameter cannot be null");
        }
        
        //load script
        List<String> scriptLines = IOUtils.readLines(Class.class.getResourceAsStream(resourcePath));
        
        StringBuilder sb = new StringBuilder();
                        
        for (String line : scriptLines){
            if (replacer != null){
                sb.append(replacer.replace(line));
            }
            else {
                sb.append(line);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
