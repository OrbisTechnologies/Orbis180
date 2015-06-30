/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.storage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
/**
 *
 * @author Ankit Parmar
 */
public class DatabaseQueryTest {
    
    public DatabaseQueryTest() {
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
    
    
    @Ignore
    public void testdatabaseQuery() throws IOException {
        DatabaseQuery db = new DatabaseQuery("2004-01-01","2015-05-20","MD","","");
        File sampleData = new File(getClass().getClassLoader().getResource("com.orbis.orbis180.jsonTestFiles/DBQueryTest.json").getFile());
        String expResult = readFile(sampleData.getAbsolutePath(), Charset.forName("UTF-8"));
        String result = db.databaseQuery();
        assertEquals(expResult,result,false);
        
    }
    
}
