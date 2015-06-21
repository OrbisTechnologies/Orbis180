/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author aparmar
 */
public class GetOpenFDAData {
        
    private final String openFDADataLink= "https://api.fda.gov/food/enforcement.json?api_key=1mhfdv4IKKTKLbJ8DlDuzQoWKYWXnTf0cQOiLaYl&search=report_date:[20040101+TO+20131231]&limit=2";
    
    protected void getOpenFDAData() {
        
        JsonNode recall_number, reason_for_recall, status, distribution_pattern, product_quantity,
                         recall_initiation_date,state, event_id, product_type, product_description,
                         country, city, recalling_firm, report_date, epoch, 
                         voluntary_mandated, classification, code_info, id, openfda,initial_firm_notification;
        
        ObjectMapper mapperObj = new ObjectMapper();
        
        try {

            Client client = Client.create();

            WebResource webResource = client.resource(openFDADataLink);

            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

            if (response.getStatus() != 200) 
            {
               System.out.println("Failed : HTTP error code : " + response.getStatus());
            }

            String output = response.getEntity(String.class);
            
            JsonNode rootNode = mapperObj.readTree(output);            
            JsonNode resultsNode = rootNode.path("results");
            
            //For testing purpose
//            System.out.println("Output from openFDA Data: \n");
//            System.out.println("results: " + resultsNode);
//            System.out.println("Node_Size: " + resultsNode.size());
//            System.out.println("=================================================================================================");
            
            for(int i = 0; i < resultsNode.size(); i++)
            {   
                recall_number = resultsNode.get(i).get("recall_number");
                reason_for_recall = resultsNode.get(i).get("reason_for_recall");
                status = resultsNode.get(i).get("status");
                distribution_pattern = resultsNode.get(i).get("distribution_pattern");
                product_quantity = resultsNode.get(i).get("product_quantity");
                
                recall_initiation_date = resultsNode.get(i).get("recall_initiation_date");
                state = resultsNode.get(i).get("state");
                event_id = resultsNode.get(i).get("event_id");
                product_type = resultsNode.get(i).get("product_type");
                product_description = resultsNode.get(i).get("product_description");
                
                country = resultsNode.get(i).get("country");
                city = resultsNode.get(i).get("city");
                recalling_firm = resultsNode.get(i).get("recalling_firm");
                report_date = resultsNode.get(i).get("report_date");
                epoch = resultsNode.get(i).get("@epoch");
                
                voluntary_mandated = resultsNode.get(i).get("voluntary_mandated");
                classification = resultsNode.get(i).get("classification");
                code_info = resultsNode.get(i).get("code_info");
                id = resultsNode.get(i).get("@id");
                openfda = resultsNode.get(i).get("openfda");
                initial_firm_notification = resultsNode.get(i).get("initial_firm_notification");
                
                
                //For testing purpose
//                System.out.println("=================================================================================================");
//                System.out.println("Data: " + '\n' + recall_number + '\n' +  reason_for_recall + '\n' +  status + '\n' +  distribution_pattern + '\n' + product_quantity + '\n' + 
//                         recall_initiation_date + '\n' +  state + '\n' +  event_id + '\n' +  product_type + '\n' +  product_description + '\n' + 
//                         country + '\n' +  city + '\n' +  recalling_firm + '\n' +  report_date + '\n' +  epoch + '\n' +  
//                         voluntary_mandated + '\n' +  classification + '\n' +  code_info + '\n' +  id + '\n' +  openfda + '\n' + initial_firm_notification);
            }
             
            
        } catch (Exception e) {
 
		e.printStackTrace();
 
	}
    }
    
}
