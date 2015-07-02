package com.orbis.orbis180.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Carlos Andino <candino@orbistechnologies.com>
 */
public class SesameFoodStoreTest {
    
    public SesameFoodStoreTest() {
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

    /**
     * Test of storeFromJson method, of class SesameFoodStore.
     */
    @Ignore
    @Test
    public void testStoreFromJson() throws IOException {
        System.out.println("storeFromJson");
        
        
        File sampleData = new File(getClass().getClassLoader().getResource("data/sampleFoodReports.json").getFile());
        String jsonData = readFile(sampleData.getAbsolutePath(), Charset.forName("UTF-8"));
        System.out.println(jsonData);
        IStore instance = StoreFactory.getFoodAPIStore();
        instance.storeFromJson(jsonData);
        
        fail("The test case is a prototype.");
    }
    
    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        
        return new String(encoded, encoding);
    }
            
}
