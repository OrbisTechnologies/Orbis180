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
    private String advancedSearch;
    private String foodGroup;
    
    private String server;
    private String repositoryID;
    private RemoteRepositoryManager manager;
    private Repository repository;
    private String context;
    private String baseURI;
    
    final static protected Logger logger = LoggerFactory.getLogger(DatabaseQuery.class);
    private String endpoint;
    private String sparqlQuery = "PREFIX openFDA: <http://www.orbistechnologies.com/ontologies/openFDA#>\n" +
"PREFIX : <http://www.w3.org/2002/07/owl#>\n" +
"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
"Select ?recallNumber ?reportDate ?eventId ?recallingFirm ?status (CONCAT(?city, \",\",?state) AS ?location) ?latitude ?longitude ?foodGroup ?classification ?recallInitiationDate ?productDescription ?productQty ?codeInfo ?distPattern ?recallReason ?voluntaryMandated ?notification\n" +
"Where {\n" +
"	?id a openFDA:EnforcementReport ;\n" +
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
"   FILTER( ?reportDate > \"$begnningDate$\"^^xsd:dateTime)" +
"   FILTER( ?reportDate < \"$endDate$\"^^xsd:dateTime) .\n" +
"   FILTER( ?state = \"$location$\") .\n" +
"  OPTIONAL {\n" +
"  	?id openFDA:hasFoodGroup ?foodGroupId .\n" +
"    ?foodGroupId rdfs:label ?foodGroup .\n" +
"    FILTER (?foodGroup = \"$foodGroup$\") .\n" +
"  }\n" +
"}";
    
//    upperLim =  "FILTER( ?reportDate > \""+ varName + "T00:00:00Z\"^^xsd:dateTime) .";
    public DatabaseQuery(String bngDateRang, String endDateRang, String location, String advancedSearch, String foodGroup)
    {
        Properties config = new Properties();
        try {
            config.load(getClass().getResourceAsStream("/conf/config.properties"));
            
            endpoint = config.getProperty("com.orbis.orbis180.storage.databaseQuery.endpoint");
 //           sparqlQuery = config.getProperty("com.orbis.orbis180.storage.databaseQuery.sparqlQuery");
            
            logger.info("Endpoint: " + endpoint);
            logger.info("SPARQL: "+ sparqlQuery);
            
        } catch (Exception e) {
            e.printStackTrace();

        }
	
        this.bngDateRang = bngDateRang;
        this.endDateRang = endDateRang;         
        this.location = location;
//        this.advancedSearch = advancedSearch;
        this.foodGroup = foodGroup;       
        
    }
    
    // This method setup connection with sesame database, query database and 
    //return data to the rest end points call
    public String databaseQuery(){
        
        String dbOutput = "";
        
       try {
                 
                bngDateRang = dateTimeFormat(bngDateRang);
                endDateRang = dateTimeFormat(endDateRang);
                logger.info(bngDateRang+ " " + endDateRang);
                                
                StringTemplate query = new StringTemplate(sparqlQuery);
                
                query.setAttribute("begnningDate", bngDateRang);
                query.setAttribute("endDate", endDateRang);
                query.setAttribute("location", location);
//                query.setAttribute("advancedSearch", advancedSearch);
                query.setAttribute("foodGroup", foodGroup);
                
                
                String urlEncode = URLEncoder.encode(query.toString(), "UTF-8");
 
		Client client = Client.create();
 
		WebResource webResource = client.resource(endpoint + urlEncode);
 
		ClientResponse response = webResource.accept("application/sparql-results+json", "application/json").get(ClientResponse.class);
 
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
                
                
                 
                dbOutput = response.getEntity(String.class);
                
		System.out.println("Output from Server .... \n");
//		System.out.println("dbOutput: " + dbOutput);
                System.out.println("Query: " + query);
                
	  } catch (Exception e) {
 
		e.printStackTrace();
 
	  }
       return dbOutput;
    }
    
    
    //format date to datatime format
    //@date: date that needs to be
    protected String dateTimeFormat(String date)
    {
        return (date + "T00:00:00Z");
    }

    
}
