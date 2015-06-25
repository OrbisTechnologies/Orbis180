/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import com.orbis.orbis180.dataStructures.WileyQuery;
import com.orbis.orbis180.storage.DatabaseDAO;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author aparmar
 */
@Path("/monitor")
public class SystemMonitorRestClient {
    private DatabaseDAO dbStore;
    
  @GET()
  @Path("/parsingJsonDataValue")
  @Produces(MediaType.TEXT_HTML)  
  public void addDataToDatabase() throws IOException{      
      OpenFDAClient addObj = new OpenFDAClient();
      addObj.getNumOfRecordsBtwYears();

  }
  
  
  @POST
  @Path("/query")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response consumeJSON( WileyQuery newquery) {
    String output = newquery.toString();

    return Response.status(200).entity(output).build();
  }

  int mHitCounter = 666;
 
  @GET
  @Path("/access")
  public Response consumeJSON( @Context HttpServletRequest requestContext,@Context SecurityContext context ) {
   String yourIP = requestContext.getRemoteAddr().toString() + " " + mHitCounter++;
   mHitCounter=mHitCounter+187;
   //return "<html> " + "<title>" + "Webservice Status" + "</title>"        + "<body><h1>" + "Orbis180 Data Ingestion Webservice is Alive" + "</body></h1>" + "</html> ";  
    return Response.status(200).entity(yourIP).build();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/summary")
  public String sendTopKeywords( @Context HttpServletRequest requestContext,@Context SecurityContext context ) {
   String yourIP = requestContext.getRemoteAddr().toString() + " " + mHitCounter++;
   mHitCounter=mHitCounter+187;
   this.dbStore = new DatabaseDAO();
 // return "<html> " + "<title>" + "Webservice Status" + "</title>"  + "<body><h1>" + "Orbis180 Data Ingestion Webservice is Alive" + "</body></h1>" + "</html> ";  
  //return Response.status(200).entity(yourIP).build();
   return "woot";
  }
  
}
