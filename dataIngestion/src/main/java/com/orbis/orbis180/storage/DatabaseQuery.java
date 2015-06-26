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
                                    "Select ?id ?recallNumber ?reportDate ?eventId ?recallingFirm ?status (CONCAT(?city, \",\",?state) AS ?location) ?classification ?recallInitiationDate ?productDescription ?productQty ?codeInfo ?distPattern ?recallReason ?voluntaryMandated ?notification\n" +
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
                                    "  FILTER( ?advancedSearch = \"$advancedSearch$\") .\n" +
                                    "  FILTER( ?foodGroup = \"$foodGroup$\") .\n" +
                                    "}";
    String endpoint = "http://54.208.5.141/openrdf-sesame/repositories/openFDA-test?query=PREFIX%20openFDA%3A%20%3Chttp%3A%2F%2Fwww.orbistechnologies.com%2Fontologies%2FopenFDA%23%3E%0APREFIX%20%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0ASelect%20%3Fid%20%3FrecallNumber%20%3FreportDate%20%3FeventId%20%3FrecallingFirm%20%3Fstatus%20(CONCAT(%3Fcity%2C%20%22%2C%22%2C%3Fstate)%20AS%20%3Flocation)%20%3Fclassification%20%3FrecallInitiationDate%20%3FproductDescription%20%3FproductQty%20%3FcodeInfo%20%3FdistPattern%20%3FrecallReason%20%3FvoluntaryMandated%20%3Fnotification%0AWhere%20%7B%0A%09%3Fid%20a%20openFDA%3AEnforcementReport%20%3B%0A%20%20%20%20%09openFDA%3AcodeInfo%20%3FcodeInfo%20%3B%0A%20%20%20%20%09openFDA%3AdistributionPattern%20%3FdistPattern%20%3B%0A%20%20%20%20%09openFDA%3AhasClassification%20%3Fclassification%20%3B%0A%20%20%20%20%09openFDA%3AhasInitialFirmNotification%20%3Fnotification%20%3B%0A%20%20%20%20%09openFDA%3AhasRecallInitiationDate%20%3FrecallDateId%20%3B%0A%20%20%20%20%09openFDA%3AhasRecallingFirm%20%3FfirmId%20%3B%0A%20%20%20%20%09openFDA%3ArecallNumber%20%3FrecallNumber%20%3B%0A%20%20%20%20%09openFDA%3AhasStatus%20%3Fstatus%20%3B%0A%20%20%20%20%09openFDA%3AproductQuantity%20%3FproductQty%20%3B%0A%20%20%20%20%09openFDA%3AreasonForRecall%20%3FrecallReason%3B%0A%20%20%20%20%09openFDA%3AvoluntaryMandated%20%3FvoluntaryMandated%20%3B%0A%20%20%20%20%09openFDA%3AeventId%20%3FeventId%20%3B%0A%20%20%20%20%09openFDA%3AhasLocation%20%3FlocationId%20%3B%0A%20%20%20%20%09openFDA%3AhasReportDate%20%3FreportDateId%20.%0A%20%20%3FfirmId%20openFDA%3AorganizationName%20%3FrecallingFirm%20.%0A%20%20%3FrecallDateId%20openFDA%3Atimestamp%20%3FrecallInitiationDate%20.%0A%20%20%3FreportDateId%20openFDA%3Atimestamp%20%3FreportDate%20.%0A%20%20%3FlocationId%20openFDA%3Acity%20%3Fcity%20%3B%0A%20%20%20%20%09openFDA%3Astate%20%3Fstate%20%3B%0A%20%20%20%20%09openFDA%3Acountry%20%3Fcountry%20.%0A%20%20FILTER(%20%3FreportDate%20%3E%20%222006-05-20T00%3A00%3A00Z%22%5E%5Exsd%3AdateTime)%20.%0A%20%20FILTER(%20%3FreportDate%20%3C%20%2220010-05-20T00%3A00%3A00Z%22%5E%5Exsd%3AdateTime)%20.%0A%20%20FILTER(%20%3Fstate%20%3D%20%22MD%22)%20.%0A%7D%20LIMIT%2010";
    
    
    public DatabaseQuery(String bngDateRang, String endDateRang, String location, String advancedSearch, String foodGroup)
    {
        this.bngDateRang = bngDateRang;
        this.endDateRang = endDateRang;         
        this.location = location;
        this.advancedSearch = advancedSearch;
        this.foodGroup = foodGroup;       
        
    }
    
    // This method setup connection with sesame database, query database and 
    //return data to the rest end points
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
                query.setAttribute("advancedSearch", advancedSearch);
                query.setAttribute("foodGroup", foodGroup);
                
                
                String urlEncode = URLEncoder.encode(query.toString(), "UTF-8");
 
		Client client = Client.create();
 
		WebResource webResource = client.resource(endpoint +"?query=" + query);
 
		ClientResponse response = webResource.accept("application/sparql-results+json", "application/json").get(ClientResponse.class);
 
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
                
                
                 
                dbOutput = response.getEntity(String.class);
                
		System.out.println("Output from Server .... \n");
//		System.out.println(output);
 //               System.out.println(query);
                
	  } catch (Exception e) {
 
		e.printStackTrace();
 
	  }
       return dbOutput;
    }
    
    
    //format date to datatime format
    //@date: date that needs to be formatted
    protected String dateTimeFormat(String date)
    {
        return (date + "T00:00:00Z");
    }

    
}
