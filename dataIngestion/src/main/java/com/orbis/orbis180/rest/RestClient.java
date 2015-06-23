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
@Path("/data")
public class RestClient {

    
  @GET()
  @Path("/writeToFile")
  @Produces(MediaType.TEXT_HTML)  
  public void writeDataToFile() throws IOException{ 
      OpenFDAClient writeObj = new OpenFDAClient();
      writeObj.checkRecordLimit();
      
  }  
  
  @GET()
  @Path("/parsingJsonDataValue")
  @Produces(MediaType.TEXT_HTML)  
  public void addDataToDatabase() throws IOException{      
      OpenFDAClient addObj = new OpenFDAClient();
      addObj.getNumOfRecordsBtwYears();

  }
  
  
 
      
}
