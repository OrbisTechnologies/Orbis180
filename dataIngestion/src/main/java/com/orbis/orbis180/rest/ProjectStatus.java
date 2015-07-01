
package com.orbis.orbis180.rest;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Ankit Parmar
 * Display Orbis180 Data Ingestion Webservice Service status
 * 
 */
@Path("/status")
public class ProjectStatus {
    
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String sayHtmlStatus() {
    return "<html> " + "<title>" + "Webservice Status" + "</title>"
        + "<body><h1>" + "Orbis180 Data Ingestion Webservice is Alive" + "</body></h1>" + "</html> ";  
  }
    
}
