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