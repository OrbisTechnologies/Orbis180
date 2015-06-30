/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import com.orbis.orbis180.dataStructures.SummaryData;
import com.orbis.orbis180.dataStructures.WileyQuery;
import com.orbis.orbis180.storage.DatabaseDAO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.codehaus.jackson.JsonNode;

import org.codehaus.jackson.map.ObjectMapper;


/**
 * monitors the queries and responses to Wiley
 * @author cblount
 */
@Path("/monitor")
public class SystemMonitorRestClient {
    private DatabaseDAO dbStore;
  
    
    /***
     * Initializes the Record of queries in the DB
     * @return a 200 Response
     */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/init")
  public String init( ) {
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(true);
    return "init complete.";
  }
  
  
    /**
     * records the query history record
     * @param new query
     * @return 
     */
  @POST
  @Path("/query")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response consumeJSON( String newquery) {
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
    String output = newquery.toString();
    ObjectMapper mapper = new ObjectMapper();
    try{
        //(manual deserialization is a workaround for an issue in Tomcat)
    JsonNode actualObj = mapper.readTree(newquery);
    this.dbStore.addQuery(actualObj.get("location").asText(),actualObj.get("searchField").asText(),actualObj.get("responseTime").asInt());
    }
    catch(IOException ex){
        System.out.println (ex.toString());
    }
    return Response.status(200).entity(output).build();
  }

  int mHitCounter = 0;
 
  /***
   * queryCount
   * @return A count of queries recorded
   */
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/queryCount")
  public String  queryCount() {
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
     String retval = this.dbStore.getQueryCount().toString();
     this.dbStore.uninit();
     return retval;
   }
  
  /***
   * Summary
   * @return A Summary of aggregate statistics of usage 
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/summary")
  public String sendSummary( ) {
      
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
        SummaryData retVal = new SummaryData();
        retVal.A_V_G_QueryTime = this.dbStore.getAvgQueryTime().toString();
        dbStore = new DatabaseDAO();
        dbStore.init(false);//TODO: Implement Connection pooling
        retVal.QueriesPerDay = this.dbStore.getQueriesPerDay();
        
        Calendar cal = Calendar.getInstance();
        cal.clear();

        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, 6);
        cal.set(Calendar.DATE, 1);
        java.util.Date utilDate = cal.getTime();
        
        dbStore.init(false);//TODO: Implement Connection pooling
        retVal.QueriesSince=this.dbStore.getQueryCountSince(utilDate).toString();
        
        cal.clear();

        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        utilDate = cal.getTime();
        retVal.Y_T_D_Queries=this.dbStore.getQueryCountSince(utilDate).toString();
        
      this.dbStore.uninit();
   return retVal.toJSONString();
  }
  
  
  /**
   * 
   * @return The top ten queries to the server
   */
    @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/topten")
  public String sendTopTen( ) {
      
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
        final List<WileyQuery> retVal = dbStore.getTopTen();
        
        
    final OutputStream out = new ByteArrayOutputStream();
    final ObjectMapper mapper = new ObjectMapper();
        
        
        String returnString = "{}";
        this.dbStore.uninit();
        try{
            //(manual serialization is a workaround for an issue in Tomcat)
            returnString = mapper.writeValueAsString(retVal);
        }    
        catch(IOException ex){
            System.out.println (ex.toString());
        }
        return returnString;
    }
  
    /**
   * 
   * @return The list of dates and quantities of all queries
   */
    @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/querytimes")
  public String sendQueryTimes( ) {
      
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
        final List<WileyQuery> retVal = dbStore.getQueryFreq();
        
        
    final OutputStream out = new ByteArrayOutputStream();
    final ObjectMapper mapper = new ObjectMapper();
        
        
        String returnString = "{}";
        this.dbStore.uninit();
        try{
            //(manual serialization is a workaround for an issue in Tomcat)
            returnString = mapper.writeValueAsString(retVal);
        }    
        catch(IOException ex){
            System.out.println (ex.toString());
        }
        return returnString;
    }
  
}
