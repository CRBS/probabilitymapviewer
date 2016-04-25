/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsd.crbs.segmenter.slice;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author churas
 */
public class TestSliceDirLastModifiedComparator {
    
    @Rule
    public TemporaryFolder _testFolder = new TemporaryFolder();

    
    public TestSliceDirLastModifiedComparator() {
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
    public void testCompareBothNull(){
        SliceDirLastModifiedComparator compare = 
                new SliceDirLastModifiedComparator();
        assertTrue(compare.compare(null, null) == 0);
    }
    
    @Test
    public void testCompareFirstIsNull(){
        SliceDirLastModifiedComparator compare = 
                new SliceDirLastModifiedComparator();
        assertTrue(compare.compare(null, new SliceDir("foo")) == -1);
    }
  
    @Test
    public void testCompareSecondIsNull(){
        SliceDirLastModifiedComparator compare = 
                new SliceDirLastModifiedComparator();
        assertTrue(compare.compare(new SliceDir("hi"), null) == 1);
    }
    
    //test cast error
    @Test
    public void testCompareCastException(){
        SliceDirLastModifiedComparator compare = 
                new SliceDirLastModifiedComparator();
        try {
            compare.compare("hi","hi");
            fail("Expected CompareCastException");
        }
        catch(ClassCastException cce){
            
        }
    }
    
    @Test
    public void testCompareValidSliceDirObjects() throws Exception{
        File tmpDir = _testFolder.newFolder();
        
        SliceDirLastModifiedComparator compare = 
                new SliceDirLastModifiedComparator();

        File oneFile = new File(tmpDir + File.separator + "one");
        assertTrue(oneFile.createNewFile());
        
        File twoFile = new File(tmpDir + File.separator + "two");
        assertTrue(twoFile.createNewFile());
        
        
        
        oneFile.setLastModified(10000L);
        SliceDir one = new SliceDir(oneFile.getAbsolutePath());
        SliceDir two = new SliceDir(twoFile.getAbsolutePath());
        //test o1 less then o2
        assertTrue(compare.compare(one,two) == -1);
        //test o1 greater then o2
        assertTrue(compare.compare(two,one) == 1);
    
        //test o1 == o2
        assertTrue(compare.compare(one,one) == 0);
        
        //test where last modified matches but names are different
        //forcing lexigraphical compare
        oneFile.setLastModified(twoFile.lastModified());
        one = new SliceDir(oneFile.getAbsolutePath());
        two = new SliceDir(twoFile.getAbsolutePath());
        assertTrue(compare.compare(one,two) == -1);
        assertTrue(compare.compare(two,one) == 1);
    }
}
