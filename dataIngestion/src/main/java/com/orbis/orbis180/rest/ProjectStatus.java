/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 *
 * @author aparmar
 */
@Path("/status")
public class ProjectStatus {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String sayHtmlStatus() {
    return "<html> " + "<title>" + "Webservice Status" + "</title>"
        + "<body><h1>" + "Orbis180 Data Ingestion Webservice is Alive" + "</body></h1>" + "</html> ";  
  }
  
  @GET()
  @Path("/getOpenFDAData/addToDatabase")
  @Produces(MediaType.TEXT_HTML)  
  public void addDataToDatabase() throws IOException{      
      OpenFDAClient addObj = new OpenFDAClient();
      addObj.getNumOfRecordsBtwYears();

  }
  
  
   @GET()
  @Path("/getOpenFDAData/writeToFile")
  @Produces(MediaType.TEXT_HTML)  
  public void writeDataToFile() throws IOException{ 
      OpenFDAClient writeObj = new OpenFDAClient();
      writeObj.checkRecordLimit();
      
  }
      
}
