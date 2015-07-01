/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.storage;

import com.orbis.orbis180.dataStructures.WileyQuery;
import java.util.Calendar;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author cblount
 */

public class MonitorDAOTest{
    DatabaseDAO dbStore;
    @Before
        public void setUp() throws Exception {
        dbStore = new DatabaseDAO();
        dbStore.init(true);
        
        //this is out of order, should get sorted correctly on function call
        dbStore.addQuery("NY","Beets",100);
        
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        dbStore.addQuery("NY","Taco",0);
        
        
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        dbStore.addQuery("NY","HotDog",0);
        
        
        dbStore.addQuery("NY","Apple",0);
        dbStore.addQuery("NY","Apple",0);
        dbStore.addQuery("NY","Apple",0);
        dbStore.addQuery("NY","Apple",0);
        dbStore.addQuery("NY","Apple",0);
        dbStore.addQuery("NY","Apple",0);
        dbStore.addQuery("NY","Apple",0);
        dbStore.addQuery("NY","Apple",0);
        
        
        dbStore.addQuery("NY","Orange",0);
        dbStore.addQuery("NY","Orange",0);
        dbStore.addQuery("NY","Orange",0);
        dbStore.addQuery("NY","Orange",0);
        dbStore.addQuery("NY","Orange",0);
        dbStore.addQuery("NY","Orange",0);
        dbStore.addQuery("NY","Orange",0);
        
        
        dbStore.addQuery("NY","Peach",0);
        dbStore.addQuery("NY","Peach",0);
        dbStore.addQuery("NY","Peach",0);
        dbStore.addQuery("NY","Peach",0);
        dbStore.addQuery("NY","Peach",0);
        dbStore.addQuery("NY","Peach",0);
        
        
        dbStore.addQuery("NY","Juice",0);
        dbStore.addQuery("NY","Juice",0);
        dbStore.addQuery("NY","Juice",0);
        dbStore.addQuery("NY","Juice",0);
        dbStore.addQuery("NY","Juice",0);
        
        
        dbStore.addQuery("NY","Beef",0);
        dbStore.addQuery("NY","Beef",0);
        dbStore.addQuery("NY","Beef",0);
        dbStore.addQuery("NY","Beef",0);
        
        dbStore.addQuery("NY","Chicken",0);
        dbStore.addQuery("NY","Chicken",0);
        dbStore.addQuery("NY","Chicken",0);
        
        dbStore.addQuery("NY","Carrot",0);
        dbStore.addQuery("NY","Carrot",0);
        
        
        
        
    }

    @After
    public void tearDown() throws Exception {
         dbStore.clear();
    }
    
    
    
    @Test
    public void dbTestQueryCount() throws Exception {
        int count = 0;
        dbStore.addQuery("NY","Beets",100);
        count = dbStore.getQueryCount();
        assertEquals(56,count);
        
        dbStore.addQuery("NY","Taco",0);
        count = dbStore.getQueryCount();
        assertEquals(57, count);
    }
    
    @Test
    public void dbTestQueriesSinceQueries() throws Exception {
        int count = 0;
        dbStore.addQuery("NY","Beets",100);
        
        Calendar cal = Calendar.getInstance();
        cal.clear();

        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        java.util.Date utilDate = cal.getTime();
        count = dbStore.getQueryCountSince(utilDate);
        assertEquals(56,count);
    }
    
      @Test
    public void dbTestResponseTimeCalculation() throws Exception {
        int count = 0;
        dbStore.addQuery("NY","Beets",100);
        count = dbStore.getQueryCount();
        assertEquals(count,56);

        dbStore.addQuery("NY","Taco",0);
        int avrRespTime = dbStore.getAvgQueryTime();
        assertEquals(3,avrRespTime);
    }
    
     
      @Test
public void dbTestQueriesPerDay() throws Exception {
        int count = 0;
        count = dbStore.getQueriesPerDay();
        assertEquals(count,55);
    }
    
        
      @Test
    public void dbTestTopQueryList() throws Exception {
        int count = 0;
        
        List<WileyQuery> topTenList =  dbStore.getTopTen();
        assertEquals(topTenList.size(),10);
        int expected_count=10;
        
        //iterate through the descending list 
        for (int i = 1; i<10; i++){
            assertEquals((int)topTenList.get(i-1).count, (int)expected_count);
            expected_count--;
        }
    }
}
