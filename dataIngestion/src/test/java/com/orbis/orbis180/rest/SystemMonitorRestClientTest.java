/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cblount
 */
public class SystemMonitorRestClientTest {
    public static SystemMonitorRestClient clientInstance;
    public SystemMonitorRestClientTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        clientInstance = new SystemMonitorRestClient();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        clientInstance.init();
    }
    
    @After
    public void tearDown() {
         clientInstance.clearData();
    }

    /**
     * Test of init method, of class SystemMonitorRestClient.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        SystemMonitorRestClient instance = new SystemMonitorRestClient();
        String expResult = "init complete.";
        String result = instance.init();
        assertEquals(expResult, result);
    }

    /**
     * Test of consumeJSON method, of class SystemMonitorRestClient.
     */
    @Test
    public void testConsumeJSON() {
        System.out.println("consumeJSON");
        String newquery = "{\"responseTime\":22, \"searchField\":\"rice\", \"location\":\"NY\"}";
        clientInstance = new SystemMonitorRestClient();
        Response expResult = Response.status(200).entity(newquery).build();
        Response result = clientInstance.consumeJSON(newquery);
        //TODO: find a better way to test this.. this is fine for now since it is indirectly tested by most other tests
        assertEquals(expResult.getStatus(), result.getStatus());
    }

    /**
     * Test of queryCount method, of class SystemMonitorRestClient.
     */
    @Test
    public void testQueryCount() {
        System.out.println("queryCount");
        String newquery = "{\"responseTime\":22, \"searchField\":\"rice\", \"location\":\"NY\"}";
        clientInstance = new SystemMonitorRestClient();
        clientInstance.consumeJSON(newquery);
        String expResult = "1";
        String result = clientInstance.queryCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of sendSummary method, of class SystemMonitorRestClient.
     */
    @Test
    public void testSendSummary() {
        System.out.println("sendSummary");
        String newquery = "{\"responseTime\":22, \"searchField\":\"rice\", \"location\":\"NY\"}";
        clientInstance = new SystemMonitorRestClient();
        clientInstance.consumeJSON(newquery);
        String expResult = "{\"QueriesSince\": 1, \"A_V_G_QueryTime\": 22, \"Y_T_D_Queries\": 1, \"QueriesPerDay\": 1, \"YearlyChangeInQueries\": null }";
        String result = clientInstance.sendSummary();
        assertEquals(expResult, result);
    }

    /**
     * Test of sendTopTen method, of class SystemMonitorRestClient.
     */
    @Test
    public void testSendTopTen() {
        System.out.println("sendTopTen");
        clientInstance = new SystemMonitorRestClient();
        String newquery = "{\"responseTime\":22, \"searchField\":\"rice\", \"location\":\"NY\"}";
        clientInstance = new SystemMonitorRestClient();
        clientInstance.consumeJSON(newquery);
        String expResult = "[{\"Date\":null,\"Location\":null,\"SearchField\":\"rice\",\"Count\":1,\"ResponseTime\":null}]";
        String result = clientInstance.sendTopTen();
        assertEquals(expResult, result);
    }
    
}
