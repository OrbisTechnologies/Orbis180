
package com.orbis.orbis180.rest;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *This class contains Junit test for OpenFDAClient class
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
