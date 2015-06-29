/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.storage;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author clouddev
 */
public class DatabaseQueryTest {
    
    public DatabaseQueryTest() {
    }


    @Test
    public void testDateTimeFormat() {
        DatabaseQuery db = new DatabaseQuery("2006-05-20","2010-05-20","MD","","");
        String result = db.dateTimeFormat("2006-05-20");
        String expResult = "2006-05-20T00:00:00Z";
        assertEquals(expResult,result);
        
    }
    
}
