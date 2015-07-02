
package com.orbis.orbis180.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore; 
import org.json.JSONObject;
/**
 *This class contains Junit test for DatabaseQuery class
 * @author Ankit Parmar
 */
public class DatabaseQueryTest {
    
    public DatabaseQueryTest() {
    }
    
    @Test
    public void testdatabaseQueryOne() throws IOException {
        
        DatabaseQuery db = new DatabaseQuery("2015-01-08","2015-01-15",null,null,null);
        File sampleData = new File(getClass().getClassLoader().getResource("data/DBQueryTestOne.json").getFile());
        String expResult = readFile(sampleData.getAbsolutePath(), Charset.forName("UTF-8"));
        String result = db.databaseQuery();
        assertEquals(expResult,result);
        
    }
    
     @Test
    public void testdatabaseQueryTwo() throws IOException {
        
        DatabaseQuery db = new DatabaseQuery("2015-01-01","2015-01-25","MD",null,null);
        File sampleData = new File(getClass().getClassLoader().getResource("data/DBQueryTestTwo.json").getFile());
        String expResult = readFile(sampleData.getAbsolutePath(), Charset.forName("UTF-8"));
        String result = db.databaseQuery();
        assertEquals(expResult,result);
        
    }
    
    @Test
    public void testDateTimeFormatOne() {
        DatabaseQuery db = new DatabaseQuery("2006-05-20","2010-05-20","MD","","");
        String result = db.dateTimeFormat("2006-05-20");
        String expResult = "2006-05-20T00:00:00Z";
        assertEquals(expResult,result);
        
    }
    
    @Test
    public void testDateTimeFormatTwo() {
        DatabaseQuery db = new DatabaseQuery("05-20-2006","2010-05-20","MD","","");
        String result = db.dateTimeFormat("05-20-2006");
        String expResult = "The date format must be YYYY-MM-DD";
        assertEquals(expResult,result);
        
    }
    
    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        
        return new String(encoded, encoding);
    }
    
    
    
    
}
