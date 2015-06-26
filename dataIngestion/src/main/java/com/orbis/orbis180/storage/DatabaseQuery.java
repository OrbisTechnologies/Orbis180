/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.storage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.net.URLEncoder;
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
    
    private String sparqlQuery =    "PREFIX openFDA: <http://www.orbistechnologies.com/ontologies/openFDA#>\n" +
                                    "PREFIX : <http://www.w3.org/2002/07/owl#>\n" +
                                    "Select ?id ?recallNumber ?reportDate ?eventId ?recallingFirm ?status (CONCAT(?city, \",\",?state) AS ?location) ?classification ?recallInitiationDate ?productQty ?codeInfo ?distPattern ?recallReason ?voluntaryMandated ?notification\n" +
                                    "Where {\n" +
                                    "	?id a openFDA:EnforcementReport ;\n" +
                                    "    	openFDA:codeInfo ?codeInfo ;\n" +
                                    "    	openFDA:distributionPattern ?distPattern ;\n" +
                                    "    	openFDA:hasClassification ?classification ;\n" +
                                    "    	openFDA:hasInitialFirmNotification ?notification ;\n" +
                                    "    	openFDA:hasRecallInitiationDate ?recallDateId ;\n" +
                                    "    	openFDA:hasRecallingFirm ?firmId ;\n" +
                                    "    	openFDA:recallNumber ?recallNumber ;\n" +
                                    "    	openFDA:hasStatus ?status ;\n" +
                                    "    	openFDA:productQuantity ?productQty ;\n" +
                                    "    	openFDA:reasonForRecall ?recallReason;\n" +
                                    "    	openFDA:voluntaryMandated ?voluntaryMandated ;\n" +
                                    "    	openFDA:eventId ?eventId ;\n" +
                                    "    	openFDA:hasLocation ?locationId ;\n" +
                                    "    	openFDA:hasReportDate ?reportDateId .\n" +
                                    "  ?firmId openFDA:organizationName ?recallingFirm .\n" +
                                    "  ?recallDateId openFDA:timestamp ?recallInitiationDate .\n" +
                                    "  ?reportDateId openFDA:timestamp ?reportDate .\n" +
                                    "  ?locationId openFDA:city ?city ;\n" +
                                    "    	openFDA:state ?state ;\n" +
                                    "    	openFDA:country ?country .\n" +
                                    "  FILTER( ?reportDate > \"$begnningDate$\"^^xsd:dateTime) .\n" +
                                    "  FILTER( ?reportDate < \"$endDate$\"^^xsd:dateTime) .\n" +
                                    "  FILTER( ?state = \"$location$\") .\n" +
                                    "}";
    
    String endpoint = "http://54.208.5.141/openrdf-sesame/repositories/openFDA-test?query=";
    
    
    public DatabaseQuery(String bngDateRang, String endDateRang, String location, String advancedSearch, String foodGroup)
    {
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
    public String dateTimeFormat(String date)
    {
        return (date + "T00:00:00Z");
    }

    
}
