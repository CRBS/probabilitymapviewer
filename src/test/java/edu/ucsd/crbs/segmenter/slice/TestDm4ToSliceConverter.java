/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import edu.ucsd.crbs.segmenter.App;
import edu.ucsd.crbs.segmenter.util.RunCommandLineProcess;
import java.io.File;
import java.io.FileWriter;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;


/**
 *
 * @author churas
 */
public class TestDm4ToSliceConverter {

    
    @Rule
    public TemporaryFolder _testFolder = new TemporaryFolder();
    
    public TestDm4ToSliceConverter() {
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

    //test constructor _props passed in is null
    @Test
    public void testConstructorNullProps() {
        try {
            Dm4ToSliceConverter d = new Dm4ToSliceConverter(null);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals("Properties passed in "
                    + "constructor is null"));
        }
    }

    //test constructor MRC2TIF_ARG is null
    @Test
    public void testConstructorMrc2tifargNull() {
        try {
            Properties props = new Properties();
            props.setProperty(App.INPUT_IMAGE_ARG, "foo");
            Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals(App.MRC2TIF_ARG
                    + " property is null"));
        }
    }

    //test constructor DM2MRC_ARG is null
    @Test
    public void testConstructorDm2mrcargNull() {
        try {
            Properties props = new Properties();
            props.setProperty(App.INPUT_IMAGE_ARG, "foo");
            props.setProperty(App.MRC2TIF_ARG, "mrc");
            Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals(App.DM2MRC_ARG
                    + " property is null"));
        }
    }

    //test constructor CONVERT_ARG is null
    @Test
    public void testConstructorConvertargNull() {
        try {
            Properties props = new Properties();
            props.setProperty(App.INPUT_IMAGE_ARG, "foo");
            props.setProperty(App.MRC2TIF_ARG, "mrc");
            props.setProperty(App.DM2MRC_ARG, "dm2");
            Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
            fail("Expected exception");
        } catch (NullPointerException npe) {
            assertTrue(npe.getMessage().equals(App.CONVERT_ARG
                    + " property is null"));
        }
    }

    @Test
    public void testConstructorNodownsampleargNull() {
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        assertTrue(d.getDownsampleFactor() == 1);
    }

    //test constructor invalid DOWNSAMPLEFACTOR_ARG
    @Test
    public void testConstructorInvaliddownsampleNull() {

        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "b");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        
        assertTrue(d.getDownsampleFactor() == 1);
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "-4");
        
        d = new Dm4ToSliceConverter(props);
        assertTrue(d.getDownsampleFactor() == 1);
        
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "3");
        d = new Dm4ToSliceConverter(props);
        assertTrue(d.getDownsampleFactor() == 3);
        
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "0");
        d = new Dm4ToSliceConverter(props);
        assertTrue(d.getDownsampleFactor() == 1);
    }

    //test convert sourcepath is not a file
    @Test
    public void testConvertSourcePathNotFile() throws Exception{
        
        File tempDir = _testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "3");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        String srcFile = tempDir.getAbsolutePath() + File.separator 
                    + "doesnotexist.dm4";
        try{
            d.convert(srcFile, tempDir.getAbsolutePath());
            fail("Expected exception");
        }catch(Exception ex){
            assertTrue(ex.getMessage().equals("Source file " + srcFile
                    + " is not a file"));
        }
        
    }
    
    //test convert unable to create temp directory
    @Test
    public void testConvertUnableToCreateTmpDir() throws Exception{
        File tempDir = _testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "3");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        String srcFile = tempDir.getAbsolutePath() + File.separator 
                    + "input.dm4";
        File inputFile = new File(srcFile);
        FileWriter fw = new FileWriter(inputFile);
        fw.write("blah blah");
        fw.flush();
        fw.close();
        
        String destDir = tempDir.getAbsolutePath() + File.separator
                + "foo";
        File destTmpDir = new File(destDir + Dm4ToSliceConverter.TMP_SUFFIX);
        assertTrue(destTmpDir.mkdirs());
        
        try{
            d.convert(srcFile, destDir);
            fail("Expected exception");
        }catch(Exception ex){
            assertTrue(ex.getMessage().equals("Unable to create "
                + destTmpDir.getAbsolutePath() + " tmp directory"));
        }
    }
    
    //test convert dm2mrc has non zero exit code
    @Test
    public void testConvertDm2mrcNonZeroExit() throws Exception{
        File tempDir = _testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "3");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        String srcFile = tempDir.getAbsolutePath() + File.separator 
                    + "input.dm4";
        File inputFile = new File(srcFile);
        FileWriter fw = new FileWriter(inputFile);
        fw.write("blah blah");
        fw.flush();
        fw.close();
        
        String destDir = tempDir.getAbsolutePath() + File.separator
                + "foo";
        
        String destTmpFile = destDir + Dm4ToSliceConverter.TMP_SUFFIX
                + File.separator + "out.mrc";
        RunCommandLineProcess mockrclp = mock(RunCommandLineProcess.class);
        when(mockrclp.runCommandLineProcess("dm2",srcFile,destTmpFile))
                .thenThrow(new Exception("dm2failed"));
        d.setRunCommandLineProcess(mockrclp);
        try{
            d.convert(srcFile, destDir);
            fail("Expected exception");
        }catch(Exception ex){
            assertTrue(ex.getMessage().equals("dm2failed"));
        }
    }
    //test convert mrc2tif has non zero exit code
    @Test
    public void testConvertMrc2tifNonZeroExit() throws Exception {
        File tempDir = _testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "3");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        String srcFile = tempDir.getAbsolutePath() + File.separator 
                    + "input.dm4";
        File inputFile = new File(srcFile);
        FileWriter fw = new FileWriter(inputFile);
        fw.write("blah blah");
        fw.flush();
        fw.close();
        
        String destDir = tempDir.getAbsolutePath() + File.separator
                + "foo";
        
        String mrcTmpFile = destDir + Dm4ToSliceConverter.TMP_SUFFIX
                + File.separator + "out.mrc";
        String pngTmpFile = destDir + Dm4ToSliceConverter.TMP_SUFFIX
                + File.separator + "out.png";
        RunCommandLineProcess mockrclp = mock(RunCommandLineProcess.class);
         when(mockrclp.runCommandLineProcess("dm2",srcFile,mrcTmpFile))
                .thenReturn("hello");
        when(mockrclp.runCommandLineProcess("mrc","-p",mrcTmpFile,pngTmpFile))
                .thenThrow(new Exception("mrcfailed"));
        d.setRunCommandLineProcess(mockrclp);
        
        try{
            d.convert(srcFile, destDir);
            fail("Expected exception");
        }catch(Exception ex){
            assertTrue(ex.getMessage().equals("mrcfailed"));
        }
    }
    
    //test convert convert has non zero exit code
    @Test
    public void testConvertConvertNonZeroExit() throws Exception {
        File tempDir = _testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "1");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        String srcFile = tempDir.getAbsolutePath() + File.separator 
                    + "input.dm4";
        File inputFile = new File(srcFile);
        FileWriter fw = new FileWriter(inputFile);
        fw.write("blah blah");
        fw.flush();
        fw.close();
        
        String destDir = tempDir.getAbsolutePath() + File.separator
                + "foo";
        String destTmpDir = destDir + Dm4ToSliceConverter.TMP_SUFFIX;
        String mrcTmpFile = destTmpDir
                + File.separator + "out.mrc";
        String pngTmpFile = destTmpDir
                + File.separator + "out.png";
        RunCommandLineProcess mockrclp = mock(RunCommandLineProcess.class);
         when(mockrclp.runCommandLineProcess("dm2",srcFile,mrcTmpFile))
                .thenReturn("hello");
        when(mockrclp.runCommandLineProcess("mrc","-p",mrcTmpFile,pngTmpFile))
                .thenReturn("hello2");
        when(mockrclp.runCommandLineProcess("convert",pngTmpFile,"-resize",
                "100%","-equalize","-crop","128x128","-set","filename:tile",
                "r%[fx:page.y/128]_c%[fx:page.x/128]","+repage","+adjoin",
                destTmpDir + File.separator + "0-%[filename:tile].png"))
                .thenThrow(new Exception("convertfailed"));
                
        d.setRunCommandLineProcess(mockrclp);
        
        try{
            d.convert(srcFile, destDir);
            
            fail("Expected exception");
        }catch(Exception ex){
            assertTrue(ex.getMessage().equals("convertfailed"));
        }
    }
    //test convert directory rename fails
    @Test
    public void testConvertDirectoryRenameFailsAnd4xDownsample()
            throws Exception {
        File tempDir = _testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "4");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        String srcFile = tempDir.getAbsolutePath() + File.separator 
                    + "input.dm4";
        File inputFile = new File(srcFile);
        FileWriter fw = new FileWriter(inputFile);
        fw.write("blah blah");
        fw.flush();
        fw.close();
        
        String destDir = tempDir.getAbsolutePath() + File.separator
                + "foo";
        File destDirFile = new File(destDir);
        assertTrue(destDirFile.createNewFile());
        String destTmpDir = destDir + Dm4ToSliceConverter.TMP_SUFFIX;
        String mrcTmpFile = destTmpDir
                + File.separator + "out.mrc";
        String pngTmpFile = destTmpDir
                + File.separator + "out.png";
        RunCommandLineProcess mockrclp = mock(RunCommandLineProcess.class);
         when(mockrclp.runCommandLineProcess("dm2",srcFile,mrcTmpFile))
                .thenReturn("hello");
        when(mockrclp.runCommandLineProcess("mrc","-p",mrcTmpFile,pngTmpFile))
                .thenReturn("hello2");
        when(mockrclp.runCommandLineProcess("convert",pngTmpFile,"-resize",
                "25%","-equalize","-crop","128x128","-set","filename:tile",
                "r%[fx:page.y/128]_c%[fx:page.x/128]","+repage","+adjoin",
                destTmpDir + File.separator + "0-%[filename:tile].png"))
                .thenReturn("hello3");
                
        d.setRunCommandLineProcess(mockrclp);
        
        try{
            d.convert(srcFile, destDir);
            
            fail("Expected exception");
        }catch(Exception ex){
            assertTrue(ex.getMessage().equals("Unable to rename "
                    + destTmpDir + " to " + destDir));
        }
        
    }
    
    //test successful convert
    @Test
    public void testConvertSuccess() throws Exception{
              File tempDir = _testFolder.newFolder();
        
        Properties props = new Properties();
        props.setProperty(App.INPUT_IMAGE_ARG, "foo");
        props.setProperty(App.MRC2TIF_ARG, "mrc");
        props.setProperty(App.DM2MRC_ARG, "dm2");
        props.setProperty(App.CONVERT_ARG, "convert");
        props.setProperty(App.DOWNSAMPLEFACTOR_ARG, "8");
        props.setProperty(App.TILE_SIZE_ARG, "256");
        
        Dm4ToSliceConverter d = new Dm4ToSliceConverter(props);
        String srcFile = tempDir.getAbsolutePath() + File.separator 
                    + "input.dm4";
        File inputFile = new File(srcFile);
        FileWriter fw = new FileWriter(inputFile);
        fw.write("blah blah");
        fw.flush();
        fw.close();
        
        String destDir = tempDir.getAbsolutePath() + File.separator
                + "foo";
        String destTmpDir = destDir + Dm4ToSliceConverter.TMP_SUFFIX;
        String mrcTmpFile = destTmpDir
                + File.separator + "out.mrc";
        String pngTmpFile = destTmpDir
                + File.separator + "out.png";
        RunCommandLineProcess mockrclp = mock(RunCommandLineProcess.class);
         when(mockrclp.runCommandLineProcess("dm2",srcFile,mrcTmpFile))
                .thenReturn("hello");
        when(mockrclp.runCommandLineProcess("mrc","-p",mrcTmpFile,pngTmpFile))
                .thenReturn("hello2");
        when(mockrclp.runCommandLineProcess("convert",pngTmpFile,"-resize",
                "12%","-equalize","-crop","256x256","-set","filename:tile",
                "\"r%[fx:page.y/256]_c%[fx:page.x/256]\"","+repage","+adjoin",
                destTmpDir + File.separator + "0-%[filename:tile].png"))
                .thenReturn("hello3");
                
        d.setRunCommandLineProcess(mockrclp);
        
        try{
            d.convert(srcFile, destDir);
            
        }catch(Exception ex){
            assertTrue(ex.getMessage().equals("Unable to rename "
                    + destTmpDir + " to " + destDir));
        }
        File destDirFile = new File(destDir);
        assertTrue(destDirFile.isDirectory());
        
    }
}
