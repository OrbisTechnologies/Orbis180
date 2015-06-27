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
 * This class create Sparql query and query Sesame database.
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
    private String sparqlQuery;
    
    
    public DatabaseQuery(String bngDateRang, String endDateRang, String location, String advancedSearch, String foodGroup)
    {
        Properties config = new Properties();
        try {
            config.load(getClass().getResourceAsStream("/conf/config.properties"));
            
            endpoint = config.getProperty("com.orbis.orbis180.storage.databaseQuery.endpoint");
            sparqlQuery = config.getProperty("com.orbis.orbis180.storage.databaseQuery.sparqlQuery");
            
            logger.info("Endpoint: " + endpoint);
            logger.info("SPARQL: "+ sparqlQuery);
            
        } catch (Exception e) {
            e.printStackTrace();

        }
	
        this.bngDateRang = bngDateRang;
        this.endDateRang = endDateRang;         
        this.location = location;
//        this.advancedSearch = advancedSearch;
//        this.foodGroup = foodGroup;       
        
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
//                query.setAttribute("foodGroup", foodGroup);
                
                
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
