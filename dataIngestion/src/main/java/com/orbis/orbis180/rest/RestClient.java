/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import com.orbis.orbis180.storage.DatabaseQuery;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;


/**
 *
 * @author Ankit Parmar
 * Created REST endpoints for write to file, parse json data &
 * Search Query
 */
@Path("/data")
public class RestClient {

    
  @GET()
  @Path("/writeToFile")
  @Produces(MediaType.TEXT_HTML)  
  public String writeDataToFile() throws IOException{ 
      OpenFDAClient writeObj = new OpenFDAClient();
      writeObj.checkRecordLimit();
      return "{\"sucess\": true}";
      
  }  
  
  @GET()
  @Path("/parsingJsonDataValue")
  @Produces(MediaType.TEXT_HTML)  
  public String addDataToDatabase() throws IOException{      
      OpenFDAClient addObj = new OpenFDAClient();
      addObj.getNumOfRecordsBtwYears();
      return "{\"sucess\": true}";
  }
  
  @GET()
  @Path("/searchQuery")
  @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
  public String queryDatabase(@Context UriInfo info) throws IOException{

          String bngDateRng =  info.getQueryParameters().getFirst("bngDateRng");
          String endDateRng = info.getQueryParameters().getFirst("endDateRng");
          String loc = info.getQueryParameters().getFirst("loc");
          String advSearch = info.getQueryParameters().getFirst("advSearch");
          String foodGroup = info.getQueryParameters().getFirst("foodGroup");

          if(bngDateRng.isEmpty() || endDateRng.isEmpty() || loc.isEmpty())
          {
              return "Begnning Date or End Date or Location cannot be empty";
          }else{
          
              if(advSearch.isEmpty() && foodGroup.isEmpty())
              {   
                    DatabaseQuery dbQuery = new DatabaseQuery(bngDateRng,endDateRng,loc,"","");
                    String dbOutput = dbQuery.databaseQuery();

                  return dbOutput;
              }
              else if(advSearch.isEmpty())
              {
                    DatabaseQuery dbQuery = new DatabaseQuery(bngDateRng,endDateRng,loc,advSearch,foodGroup);
                     String dbOutput = dbQuery.databaseQuery();
                  return dbOutput;
              }
              else if(foodGroup.isEmpty())
              {
                    DatabaseQuery dbQuery = new DatabaseQuery(bngDateRng,endDateRng,loc,advSearch,"");
                     String dbOutput = dbQuery.databaseQuery();
                  return dbOutput;
              }
              else
              {
                        DatabaseQuery dbQuery = new DatabaseQuery(bngDateRng,endDateRng,loc,advSearch,foodGroup);
                        String dbOutput = dbQuery.databaseQuery();
                  return dbOutput;
              }
          
          }

  }
      
}
