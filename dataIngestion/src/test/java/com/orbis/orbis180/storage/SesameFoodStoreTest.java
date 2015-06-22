package com.orbis.orbis180.storage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
    @Test
    public void testStoreFromJson() {
        System.out.println("storeFromJson");
        
        String jsonData = "";
        SesameFoodStore instance = new SesameFoodStore();
        instance.storeFromJson(jsonData);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
