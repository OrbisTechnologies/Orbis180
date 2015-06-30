/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ankit Parmar
 */
public class OpenFDAClientTest {
    
    public OpenFDAClientTest() {
    }
    
    
    @Test
    public void testValidateDateFormatOne()
    { 
        OpenFDAClient testDateFormat = new OpenFDAClient();
        
        String resultOne = testDateFormat.validateDateFormat("2014-01-27");
        String expResultOne = "20140127";
        
        assertEquals(expResultOne,resultOne);
    }
    
    @Test
    public void testValidateDateFormatTwo()
    { 
        OpenFDAClient testDateFormat = new OpenFDAClient();
        
        String resultOne = testDateFormat.validateDateFormat("01-27-2014");
        String expResultOne = "The date format must be YYYY-MM-DD";
        
        assertEquals(expResultOne,resultOne);
    }
   
}
