package com.orbis.orbis180.rest;

import com.orbis.orbis180.data.Location;
import com.orbis.orbis180.storage.SesameInterface;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;


/**
 *
 * @author aparmar
 */
@Path("/data")
public class RestClient {

  final static protected org.slf4j.Logger logger = LoggerFactory.getLogger(RestClient.class);
  
  
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
  
  /**
   * Obtains the locations available in the Sesame store and looks up their 
   * coordinates in Clavin.  Once coordinates have been obtained, they are 
   * added to Sesame.
   * 
   * @return The success or failure of the request.
   */
  
  @GET()
  @Path("/getcoordinates")
    @Produces(MediaType.APPLICATION_JSON)
    public String updateGeoCoordinates() {

        SesameInterface sesame = new SesameInterface();
        try {

            sesame.openRepository("openFDA-test");
            RepositoryConnection connection = sesame.getRepository().getConnection();
            Map<String, Location> locations = new HashMap<>();
            try {
                //Query Sesame for all the available locations
                String queryStr = "PREFIX openFDA: <http://www.orbistechnologies.com/ontologies/openFDA#>\n" +
                    "PREFIX : <http://www.w3.org/2002/07/owl#>\n" +
                    "SELECT ?locationId ?city ?state\n" +
                    "WHERE {\n" +
                    "	?locationId a openFDA:Location ;\n" +
                    "  		openFDA:city ?city ;\n" +
                    "    	openFDA:state ?state ;\n" +
                    "    	openFDA:country ?country .\n" +
                    "}";
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryStr);
                TupleQueryResult result = tupleQuery.evaluate();

                try {
                    while (result.hasNext()) {  // iterate over the result
                        BindingSet bindingSet = result.next();
                        Value locationId = bindingSet.getValue("locationId");
                        Value locationCity = bindingSet.getValue("city");
                        Value locationState = bindingSet.getValue("state");
                        
                        Location loc = new Location(locationId.stringValue());
                        loc.setCity(locationCity.stringValue());
                        loc.setState(locationState.stringValue());
                        //Store the locations in a Map to use later with Clavin
                        locations.put(locationId.stringValue(), loc);
                    }
                } finally {
                    result.close();
                }
                System.out.println("Obtained " + locations.size() + "locations.");
                //Store the locations in a single String to submit to Clavin in one request
                StringBuilder locationEntries = new StringBuilder();
                for(Map.Entry<String,Location> entry : locations.entrySet()){
                    Location location = entry.getValue();
                    //It is safe to presume that all fields have value, if not, 
                    //they would not return from the Sesame query.
                    locationEntries.append(location.getCity()).append(", ")
                            .append(location.getState()).append("\n");
                }
                String latLong = getCoordinates(locationEntries.toString());
            } finally {
                connection.close();
            }

        } catch (OpenRDFException ex) {
            logger.error("Could not open the Sesame repository.\n{}", ex);
            return "{\"sucess\": false}";
        }
        return "{\"sucess\": true}";
    }
    
    private String getCoordinates(String locationsFile){
        
        Client client = Client.create();
 
		WebResource webResource = client
		   .resource("http://localhost:9090api/v0/geotagmin");
 
		ClientResponse response = webResource.type("text/plain")
		   .post(ClientResponse.class, locationsFile);
 
//		if (response.getStatus() != 201) {
//			throw new RuntimeException("Failed : HTTP error code : "
//			     + response.getStatus());
//		}
 
		System.out.println("Output from Server .... \n");
		String output = response.getEntity(String.class);
		System.out.println(output);
        
        return output;
    }
      
}
