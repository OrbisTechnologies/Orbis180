
package com.orbis.orbis180.rest;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

 
import java.io.File;
import java.util.Properties;


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
    
    @Test
    public void testCheckRecordLimit() throws IOException
    { 
        Properties config = new Properties();
        
        config.load(getClass().getResourceAsStream("/conf/config.properties"));
        
        String directoryPath = config.getProperty("com.orbis.orbis180.rest.openFDAClient.jsonDirPath");
        OpenFDAClient writeObj = new OpenFDAClient();
        writeObj.checkRecordLimit();
         
        String expResult = "81";
        
        int result =  new File(directoryPath).listFiles().length;
    
        assertEquals(expResult,Integer.toString(result));
    }
    
}
