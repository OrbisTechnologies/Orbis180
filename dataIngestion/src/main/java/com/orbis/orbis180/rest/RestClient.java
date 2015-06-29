package com.orbis.orbis180.rest;

import com.orbis.orbis180.data.Location;
import com.orbis.orbis180.storage.DatabaseQuery;
import com.orbis.orbis180.storage.SesameInterface;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.orbis.orbis180.storage.DatabaseQuery;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ankit Parmar
 * Created REST endpoints:
 *   - Write openFDA data to file
 *   - Add openFDA data to database
 *   - Search database and return data
 *   - Obtains the locations available in the Sesame store and looks up their 
 *     coordinates in Clavin
 */
@Path("/data")
public class RestClient {

  final static protected org.slf4j.Logger logger = LoggerFactory.getLogger(RestClient.class);
  private Map<String, Location> locations;
  private SesameInterface sesame;
  private String clavinUrl;
  
  /**
   * 
   * Write openFDA data to file
   * @return success message.
   */
  @GET()
  @Path("/writeToFile")
  @Produces(MediaType.TEXT_HTML)  
  public String writeDataToFile() throws IOException{ 
      OpenFDAClient writeObj = new OpenFDAClient();
      writeObj.checkRecordLimit();
      return "{\"sucess\": true}";
      
  }  
  
  /**
   * 
   * Write openFDA data to database
   * @return success message.
   */
  @GET()
  @Path("/parsingJsonDataValue")
  @Produces(MediaType.TEXT_HTML)  
  public String addDataToDatabase() throws IOException{      
      OpenFDAClient addObj = new OpenFDAClient();
      addObj.getNumOfRecordsBtwYears();
      return "{\"sucess\": true}";
  }
  
  
  /**
   * 
   * Get query parameters from the URL and search database using those parameters
   * @return Json data from the database.
   */
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
  
  /**
   * Obtains the locations available in the Sesame store and looks up their 
   * coordinates in Clavin.  Once coordinates have been obtained, they are 
   * added to Sesame.
   * 
   * @return The success or failure of the request.
   */
  @GET()
  @Path("/update/coordinates")
    @Produces(MediaType.APPLICATION_JSON)
    public String updateGeoCoordinates() {

        sesame = new SesameInterface();
        //TODO: Add a main config loader to pull from System properties instead
        Properties config = new Properties();
        try {
            config.load(getClass().getResourceAsStream("/conf/config.properties"));
            String repositoryName = config.getProperty("com.orbis.orbis180.sesame.repository");
            clavinUrl = config.getProperty("com.orbis.orbis180.clavin.server");
            sesame.openRepository(repositoryName);
            RepositoryConnection connection = sesame.getRepository().getConnection();
            locations = new HashMap<>();
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
                logger.debug("Obtained {} locations.", locations.size());
                
                loadCoordinatesFromClavin();
                saveCoordinatesToSesame();
                
            } finally {
                connection.close();
            }

        } catch (OpenRDFException ex) {
            logger.error("Could not open the Sesame repository.\n{}", ex);
            return "{\"sucess\": false}";
        } catch (IOException ex) {
          logger.error("Could not configure the client.  Properties file was not found.\n{}", ex);
          return "{\"sucess\": false}";
      }
        return "{\"sucess\": true}";
    }
    
   /**
   * Asks Clavin for the coordinates of a given location.
   * 
   */
    private String getCoordinates(String locationsFile) {

        Client client = Client.create();

        WebResource webResource = client
                .resource(clavinUrl);

        ClientResponse response = webResource.type("text/plain")
                .post(ClientResponse.class, locationsFile);
        String output = response.getEntity(String.class);

        return output;
    }
    
  /**
   * Adds the latitude and longitude coordinates obtained from Clavin to 
   * the collection of locations.
   * 
   */
    private void loadCoordinatesFromClavin() {
        //Store the locations in a single String to submit to Clavin in one request
        //StringBuilder locationEntries = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        for (Map.Entry<String, Location> entry : locations.entrySet()) {
            Location location = entry.getValue();
            //It is safe to presume that all fields have value, if not, 
            //they would not return from the Sesame query.
            String searchLocation = location.getCity() + ", " + location.getState();
            String latLong = getCoordinates(searchLocation);
            
            try {
                JsonNode results = mapper.readTree(latLong).path("resolvedLocationsMinimum");
                if (results.size() > 0) {
                    location.setLatitude(results.get(0).get("latitude").asDouble());
                    location.setLongitude(results.get(0).get("longitude").asDouble());
                }
            } catch (IOException ex) {
                Logger.getLogger(RestClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
    * Updates the the latitude and longitude of the Location entities in Sesame.
    */
    private void saveCoordinatesToSesame(){
        List<Statement> triples = new ArrayList<>();
        ValueFactory valueFactory = sesame.getValueFactory();
        URI latitudeUri = valueFactory.createURI(sesame.getBaseUri() + "#latitude");
        URI longitudeUri = valueFactory.createURI(sesame.getBaseUri() + "#longitude");
        
        for(Map.Entry<String,Location> entry : locations.entrySet()){
            URI locationId = valueFactory.createURI(entry.getKey());
            triples.add(new StatementImpl(locationId,latitudeUri, 
                    valueFactory.createLiteral(entry.getValue().getLatitude())));
            triples.add(new StatementImpl(locationId,longitudeUri, 
                    valueFactory.createLiteral(entry.getValue().getLongitude())));
        }
        sesame.storeTriplesInBatch(triples);
    }
    
   /**
   * Loads the product description of each report loaded into Sesame and add a 
   * relationship with appropriate Food Group based on string matching with the 
   * food groups list.
   * 
   * @return The success or failure of the request.
   */
  @GET()
  @Path("/update/foodgroups")
    @Produces(MediaType.APPLICATION_JSON)
    public String addFoodGroups() {
        
        sesame = new SesameInterface();
        
        Properties config = new Properties();
        List<Statement> triples = new ArrayList<>();
        Map<String, String> foodGroups = new HashMap<>();
        loadFoodGroups(foodGroups);
        try {
            //TODO: Add a main config loader to pull from System properties instead
            config.load(getClass().getResourceAsStream("/conf/config.properties"));
            String repositoryName = config.getProperty("com.orbis.orbis180.sesame.repository");
            
            sesame.openRepository(repositoryName);
            ValueFactory valueFactory = sesame.getValueFactory();
            URI foodGroupRelUri = valueFactory.createURI(sesame.getBaseUri() + "#hasFoodGroup");
        
            RepositoryConnection connection = sesame.getRepository().getConnection();
            try {
                //Query Sesame for all the available locations
                String queryStr = "PREFIX openFDA: <http://www.orbistechnologies.com/ontologies/openFDA#>\n" +
                    "PREFIX : <http://www.w3.org/2002/07/owl#>\n" +
                    "SELECT ?reportId ?productDescription\n" +
                    "WHERE {\n" +
                    "	?reportId a openFDA:EnforcementReport ;\n" +
                    "  		openFDA:productDescription ?productDescription .\n" +
                    "}";
                TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryStr);
                TupleQueryResult result = tupleQuery.evaluate();

                try {
                    while (result.hasNext()) {  // iterate over the result
                        BindingSet bindingSet = result.next();
                        Value reportId = bindingSet.getValue("reportId");
                        String prodDescription = bindingSet.getValue("productDescription").stringValue();
                        URI report = valueFactory.createURI(reportId.stringValue());
                        for (Map.Entry<String,String> entry : foodGroups.entrySet()) {
                            if (prodDescription.toLowerCase().contains(entry.getKey().toLowerCase())) {
                                URI prodGroup = valueFactory.createURI(sesame.getBaseUri() + "#" + entry.getValue());
                                triples.add(new StatementImpl(report,foodGroupRelUri, prodGroup));
                            }
                            
                        }
                    }
                } finally {
                    result.close();
                }
                logger.debug("Loaded {} triples to add food groups to the data set.", triples.size());                
                sesame.storeTriplesInBatch(triples);
                
            } finally {
                connection.close();
            }
        } catch (OpenRDFException ex) {
            logger.error("Could not open the Sesame repository.\n{}", ex);
            return "{\"sucess\": false}";
        } catch (IOException ex) {
          logger.error("Could not configure the client.  Properties file was not found.\n{}", ex);
          return "{\"sucess\": false}";
        }
        
        return "{\"sucess\": true}";
    }
    
    private void loadFoodGroups(Map<String,String> foodGroups){
        
        BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
 
	try {
 
            br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("data/food_groups.csv").getPath()));
            while ((line = br.readLine()) != null) {
                
                // use comma as separator
                String[] item = line.split(cvsSplitBy);
                String itemId = item[0];
                String group = item[1];
                foodGroups.put(itemId, group);

            }
 
	} catch (FileNotFoundException e) {
            logger.error("Could not load food goup file.\n{}", e.getLocalizedMessage());
	} catch (IOException e) {
            logger.error("Could not load food goup file.\n{}", e.getLocalizedMessage());
	} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
  
    }
}
