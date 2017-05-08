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

import edu.ucsd.crbs.probabilitymapviewer.util.RunCommandLineProcessImpl;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;



/**
 *
 * @author Christopher Churas <churas@ncmir.ucsd.edu>
 */
@RunWith(JUnit4.class)
public class TestRunCommandLineProcessImpl {

    
    public static String FALSE_BINARY = File.separator+"bin"+File.separator+"false";
    
    public TestRunCommandLineProcessImpl() {
        FALSE_BINARY = getBinary(FALSE_BINARY);
    }
    
    public static String getBinary(final String basePath){
        File baseCheck = new File(basePath);
        if (!baseCheck.exists()){
            return File.separator+"usr"+basePath;
        }
        return basePath;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRunCommandLineProcessWithListOfArgs() throws Exception {
        try{
            RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
            ArrayList<String> cmd = new ArrayList<String>();
            cmd.add("/bin/pwd");
            rclp.runCommandLineProcess(cmd);
            fail("Expected exception");
        }
        catch(UnsupportedOperationException e){
                
        }
    }
    @Test 
    public void testGetLastCommandWithoutCommandInvocation(){
       RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
       assertTrue(rclp.getLastCommand() == null);
    }

    /**
     * This test calls the program /bin/echo and checks the output is as expected
     */
     @Test
    public void TestRunCommandLineProcessWithEchoCommand() throws Exception {
       RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
       String output = rclp.runCommandLineProcess("/bin/echo","hello world");
       assertTrue("Expected hello world\nbut got: "+output,
               output.contains("hello world\n"));
    }
    
     @Test
    public void TestRunCommandLineProcessWithExtraEmptyArg() throws Exception {
       RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
       String output = rclp.runCommandLineProcess("/bin/echo","hello world", 
                                                  "");
       assertTrue("Expected hello world\nbut got: "+output,
               output.contains("hello world\n"));
    }
    
    @Test
    public void TestRunCommandLineProcessWithEmptyEnvVar() throws Exception {
       RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
       HashMap<String,String> envMap = new HashMap<String,String>();
       rclp.setEnvironmentVariables(envMap);
       String output = rclp.runCommandLineProcess("/bin/echo", "hello world");
       assertTrue("Couldnt find hello world in " + output.replaceAll("\n",""),
                  output.replaceAll("\n","").contains("hello world"));
    }
    
    @Test
    public void TestRunCommandLineProcessWithEnvVar() throws Exception {
       RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
       HashMap<String,String> envMap = new HashMap<String,String>();
       envMap.put("HI", "hello");
       rclp.setEnvironmentVariables(envMap);
       String output = rclp.runCommandLineProcess("/usr/bin/env");
       assertTrue("Couldnt find HI=hello in " + output.replaceAll("\n",""),
                  output.replaceAll("\n","").contains("HI=hello"));
    }
    
    @Test
    public void TestRunCommandLineProcessSetWorkingDir() throws Exception {
       RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
       rclp.setWorkingDirectory(File.separator);
       String output = rclp.runCommandLineProcess("/bin/pwd");
       assertTrue("Couldnt find / in " + output.replaceAll("\n",""),
                  output.replaceAll("\n","").contains("/"));
    }
    
    /**
     * This test calls the program /bin/echo and checks the output is as expected
     */
     @Test
    public void TestRunCommandLineProcessWithFailingCommand() throws Exception {
       RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
       try {
       String output = rclp.runCommandLineProcess(FALSE_BINARY,"hello");
       fail("Expected exception");
       }
       catch(Exception ex){
           assertTrue(ex.getMessage().startsWith("Non zero exit code (1) received from "+FALSE_BINARY+": "));
       }
    } 

     @Test
     public void TestRunCommandLineProcessWithNonExistantCommand() throws Exception {
         RunCommandLineProcessImpl rclp = new RunCommandLineProcessImpl();
         try {
            String output = rclp.runCommandLineProcess("nonexistantcommandasdf","-j","1");
            fail("Expected IOException");
         }
         catch(java.io.IOException ex){
             assertTrue("expected IOException of cannot run program",ex.getMessage().contains("Cannot run program"));
         }
     }
}