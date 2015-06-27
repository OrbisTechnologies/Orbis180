/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import com.orbis.orbis180.dataStructures.SummaryData;
import com.orbis.orbis180.dataStructures.WileyQuery;
import com.orbis.orbis180.storage.DatabaseDAO;
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

/**
 *
 * @author cblount
 */
@Path("/monitor")
public class SystemMonitorRestClient {
    private DatabaseDAO dbStore;
  
    /**
     * records the query history record
     * @param newquery
     * @return 
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
  @POST
  @Path("/query")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response consumeJSON( WileyQuery newquery) {
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
    String output = newquery.toString();
    this.dbStore.addQuery(newquery.location, newquery.searchField, newquery.responseTime);
    return Response.status(200).entity(output).build();
  }

  int mHitCounter = 666;
 
  @GET
  @Path("/access")
  @Produces(MediaType.TEXT_PLAIN)
  public Response consumeJSON( @Context HttpServletRequest requestContext,@Context SecurityContext context ) {
   String yourIP = requestContext.getRemoteAddr().toString() + " " + mHitCounter++;
   mHitCounter=mHitCounter+187;
   //return "<html> " + "<title>" + "Webservice Status" + "</title>"        + "<body><h1>" + "Orbis180 Data Ingestion Webservice is Alive" + "</body></h1>" + "</html> ";  
    return Response.status(200).entity(yourIP).build();
  }
  
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/queryCount")
  public String  queryCount() {
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
        
     mHitCounter=mHitCounter+187;
   //return "<html> " + "<title>" + "Webservice Status" + "</title>"        + "<body><h1>" + "Orbis180 Data Ingestion Webservice is Alive" + "</body></h1>" + "</html> ";  
      
     String retval = this.dbStore.getQueryCount().toString();
     this.dbStore.uninit();
     return retval;
   }
  
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/summary")
  public SummaryData sendSummary( ) {
      
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
        SummaryData retVal = new SummaryData();
        retVal.A_V_G_QueryTime = this.dbStore.getAvgQueryTime().toString();
        
        
      this.dbStore.uninit();
   return retVal;
  }
  
    @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/topten")
  public List<WileyQuery> sendTopTen( ) {
      
      //TODO: Implement Caching and Connection pooling
        dbStore = new DatabaseDAO();
        dbStore.init(false);
        List<WileyQuery> retVal = dbStore.getTopTen();
        this.dbStore.uninit();
   return retVal;
  }
  
}
