/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.storage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.antlr.stringtemplate.*;
import org.openrdf.repository.Repository;

/**
 *
 * @author Ankit Parmar
 * This class creates Sparql query and query Sesame database.
 * The returned data from Sesame is send to rest endpoint
 */
public class DatabaseQuery {
    private String bngDateRang;
    private String endDateRang;
    private String location;
    private String keywordSearch;
    private String foodGroup;
       
    final static protected Logger logger = LoggerFactory.getLogger(DatabaseQuery.class);
    private String endpoint;
    private String sparqlQuery = "PREFIX openFDA: <http://www.orbistechnologies.com/ontologies/openFDA#>\n" +
                                    "PREFIX : <http://www.w3.org/2002/07/owl#>\n" +
                                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                                    "Select ?recallNumber ?reportDate ?eventId ?recallingFirm ?status (CONCAT(?city, \",\",?state) AS ?location) ?latitude ?longitude ?foodGroup ?classification ?recallInitiationDate ?productDescription ?productQty ?codeInfo ?distPattern ?recallReason ?voluntaryMandated ?notification\n" +
                                    "Where {\n" +
                                    "	?id a openFDA:EnforcementReport ;\n" +
                                    "    	?predicate ?object ;\n" +
                                    "    	openFDA:codeInfo ?codeInfo ;\n" +
                                    "    	openFDA:distributionPattern ?distPattern ;\n" +
                                    "    	openFDA:hasClassification ?classificationId ;\n" +
                                    "    	openFDA:hasInitialFirmNotification ?notificationId ;\n" +
                                    "    	openFDA:hasRecallInitiationDate ?recallDateId ;\n" +
                                    "    	openFDA:hasRecallingFirm ?firmId ;\n" +
                                    "    	openFDA:recallNumber ?recallNumber ;\n" +
                                    "    	openFDA:hasStatus ?statusId ;\n" +
                                    "    	openFDA:productDescription ?productDescription ;\n" +
                                    "    	openFDA:productQuantity ?productQty ;\n" +
                                    "    	openFDA:reasonForRecall ?recallReason;\n" +
                                    "    	openFDA:voluntaryMandated ?voluntaryMandated ;\n" +
                                    "    	openFDA:eventId ?eventId ;\n" +
                                    "    	openFDA:hasLocation ?locationId ;\n" +
                                    "    	openFDA:hasReportDate ?reportDateId .\n" +
                                    "  ?firmId openFDA:organizationName ?recallingFirm .\n" +
                                    "  ?recallDateId openFDA:timestamp ?recallInitiationDate .\n" +
                                    "  ?reportDateId openFDA:timestamp ?reportDate .\n" +
                                    "  ?classificationId rdfs:label ?classification .\n" +
                                    "  ?notificationId rdfs:label ?notification .\n" +
                                    "  ?statusId rdfs:label ?status .\n" +
                                    "  ?locationId openFDA:city ?city ;\n" +
                                    "    	openFDA:state ?state ;\n" +
                                    "    	openFDA:country ?country ;\n" +
                                    "    	openFDA:latitude ?latitude ;\n" +
                                    "    	openFDA:longitude ?longitude .\n" +
                                    "   OPTIONAL {\n" +
                                    "  	?id openFDA:hasFoodGroup ?foodGroupId .\n" +
                                    "    ?foodGroupId rdfs:label ?foodGroup .\n" +
                                    "  }\n"+
                                    "  $begnningDate$ \n" +
                                    "  $endDate$ \n" +
                                    "  $location$ \n" +
                                    "  $foodGroup$ \n" +
                                    "  $keywordSearch$ \n" +
                                    "}"; 
           

    public DatabaseQuery(String bngDateRang, String endDateRang, String location, String keywordSearch, String foodGroup)
    {
        Properties config = new Properties();
        try {
            config.load(getClass().getResourceAsStream("/conf/config.properties"));
            
            endpoint = config.getProperty("com.orbis.orbis180.storage.databaseQuery.endpoint");
                        
        } catch (Exception e) {
            e.printStackTrace();

        } 
        
        this.bngDateRang = bngDateRang;
        this.endDateRang = endDateRang;         
        this.location = location;
        this.keywordSearch = keywordSearch;
        this.foodGroup = foodGroup;       
    }
    
    /**
     * This method setup connection with sesame database, query database and 
     * return data to the rest end points call
     * @return 
     */ 
    public String databaseQuery(){
        
        String dbOutput = "";
        
       try {
                
                                
                StringTemplate query = new StringTemplate(sparqlQuery);
                
                if(bngDateRang != null)
                {
                    bngDateRang = dateTimeFormat(bngDateRang);
                    
                    String upperDateLimit = "FILTER( ?reportDate > \"" + bngDateRang +"\"^^xsd:dateTime) .";
                                        
                    query.setAttribute("begnningDate", upperDateLimit);
                }
               
                if(endDateRang != null)
                {
                    endDateRang = dateTimeFormat(endDateRang); 
                    
                    String lowerDateLimit = "FILTER( ?reportDate < \"" + endDateRang +"\"^^xsd:dateTime) .";
                    
                    query.setAttribute("endDate", lowerDateLimit);
                }
                
                if(location != null)
                { 
                    ArrayList aList= new ArrayList(Arrays.asList(location.split(",")));
                    
                    StringBuilder stateValue = new StringBuilder();
                    
                    for(int i=0;i<aList.size();i++)
                    {
                        if(i==0)
                        {
                            stateValue.append("\"" + aList.get(i) +"\"");
                        }
                        else{
                            stateValue.append(" " + "\"" + aList.get(i) +"\"");
                        }
                    }
                    
                    String loc = "VALUES ?state { " + stateValue +"} .";
                    
                    query.setAttribute("location", loc);
                }
                
                if(foodGroup != null)
                { 
                    String foodGrp = "VALUES ?foodGroup { \"" + foodGroup +"\"} .";
                    
                    query.setAttribute("foodGroup", foodGrp);
                }
                
                if(keywordSearch != null) 
                {
                    String keywrdSearch = "FILTER(regex(str(?object), \"" + keywordSearch +"\", \"i\")) .";
                    
                    query.setAttribute("keywordSearch", keywrdSearch);
                }
                    
                
                String urlEncode = URLEncoder.encode(query.toString(), "UTF-8");
 
		Client client = Client.create();
 
		WebResource webResource = client.resource(endpoint + urlEncode);
 
		ClientResponse response = webResource.accept("application/sparql-results+json", "application/json").get(ClientResponse.class);
 
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
                
                
                 
                dbOutput = response.getEntity(String.class);
                
		logger.debug("Output from Server .... \n");
		logger.debug("dbOutput: \n" + dbOutput);
                logger.debug("Query: \n" + query);
                
	  } catch (Exception e) {
 
		e.printStackTrace();
 
	  }
       return dbOutput;
    }
    
   /**
    * format date into data time format
    * @param date date that needs to be
    * @return  return date into date time format
    */ 
    protected String dateTimeFormat(String dateValue)
    {
        String val = "The date format must be YYYY-MM-DD";
       
       String datePattern = "\\d{4}-\\d{2}-\\d{2}";
       boolean isDate = dateValue.matches(datePattern);
       
       logger.debug("isdate: " + isDate);
       
       if(isDate)
       {
           logger.debug("dateVal: " + dateValue); 
           val = dateValue + "T00:00:00Z";
           logger.debug("dateVal: " + val);
       
           
       }else{
           logger.error("The date format must be YYYY-MM-DD");
       }
        return val;
    }

    
}
