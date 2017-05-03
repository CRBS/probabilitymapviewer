/*
 * COPYRIGHT AND LICENSE
 * 
 * Copyright 2014 The Regents of the University of California All Rights Reserved
 * 
 * Permission to copy, modify and distribute any part of this CRBS Workflow 
 * Service for educational, research and non-profit purposes, without fee, and
 * without a written agreement is hereby granted, provided that the above 
 * copyright notice, this paragraph and the following three paragraphs appear
 * in all copies.
 * 
 * Those desiring to incorporate this CRBS Workflow Service into commercial 
 * products or use for commercial purposes should contact the Technology
 * Transfer Office, University of California, San Diego, 9500 Gilman Drive, 
 * Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, 
 * FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 * LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service, EVEN IF 
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 * THE CRBS Workflow Service PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
 * UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, 
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
 * NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR 
 * EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF 
 * THE CRBS Workflow Service WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS. 
 */

package edu.ucsd.crbs.probabilitymapviewer.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
public class RunCommandLineProcessImpl implements RunCommandLineProcess {

    
    private static final Logger _log
            = Logger.getLogger(RunCommandLineProcessImpl.class.getName());
    
    private String _workingDirectory;
    private Map<String, String> _environVars;
    private String _lastCommand;
    
    @Override
    public void setWorkingDirectory(final String workingDir) {
        _workingDirectory = workingDir;
    }

    @Override
    public void setEnvironmentVariables(Map<String, String> envVars) {
        _environVars = envVars;
    }

    @Override
    public String getLastCommand() {
        return _lastCommand;
    }
    
    
    @Override
    public String runCommandLineProcess(String... command) throws Exception {
        String[] mCmd;
        int i = 0;
        mCmd = new String[command.length];
        _lastCommand = null;
        StringBuilder lastCmdSb = new StringBuilder();
        for (String c : command) {
            mCmd[i] = c;
            if (lastCmdSb.length() > 0){
                lastCmdSb.append(" ");
            }
            lastCmdSb.append(mCmd[i]);
            i++;
        }
        _lastCommand = lastCmdSb.toString();
        
        _log.log(Level.FINE,"Running command: " + _lastCommand);
        
        ProcessBuilder pb = new ProcessBuilder(mCmd);

        //lets caller set working directory
        if (_workingDirectory != null) {
            pb.directory(new File(_workingDirectory));
        }

        //lets caller set 1 or more environment variables
        if (_environVars != null && _environVars.isEmpty() == false) {
            Map<String, String> env = pb.environment();
            for (String key : _environVars.keySet()) {
                env.remove(key);
                env.put(key, _environVars.get(key));
            }
        }

        pb.redirectErrorStream(true);

        Process proc = pb.start();

        StringBuilder sb = new StringBuilder();

        BufferedInputStream bis = new BufferedInputStream(proc.getInputStream());

        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        String line = br.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = br.readLine();
        }
        br.close();
        int retVal = proc.waitFor();
        if (retVal != 0){
            throw new Exception("Non zero exit code ("+retVal+") received from "+
                    mCmd[0]+": " + sb.toString());
        }
        return sb.toString();
    }

}
