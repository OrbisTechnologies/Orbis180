/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author aparmar
 */
public class OpenFDAClient {
    private int MAX_RECORD_LIMIT = 100;
    private int MAX_NEXT_RECORD_LIMIT = 5000; 
    
    private int MIN_DATE = 20040101;
    private int MAX_DATE = 20151231;
    
    private int NEXT_RECORD_LIMIT = 100;
    private int CURRENT_NUM_OF_RECORDS = 0;   
    private int TOTAL_RECORDS = 0; 
    private int RECORD_WRITED_TO_FILE = 0;
    private int tempDate = 20150101;
    private int minDate;
    private int maxDate;

    
    private final String openFDAFoodUrl= "https://api.fda.gov/food/enforcement.json?";
    private final String urlApiKey= "api_key=1mhfdv4IKKTKLbJ8DlDuzQoWKYWXnTf0cQOiLaYl";

    private String searchParameter= "&search=report_date:[" + MIN_DATE +"+TO+"+ MAX_DATE +"]";

    private String recordLimitParameter= "&limit=" + MAX_RECORD_LIMIT;
    private String nextRecordsLimitParameter = "&skip=" + NEXT_RECORD_LIMIT;
   
     
    
    private final String jsonDirPath = "/home/clouddev/Desktop/json_test";
    private String jsonFileName =  jsonDirPath + "/openFDAData_Next_"+ MIN_DATE + "_To_" + MAX_DATE + "_" + RECORD_WRITED_TO_FILE + ".json";
    
    private boolean isDirCreated = false;
    private boolean isFileCreated = false;
    private boolean isPullDataToFile = true;
    
    private static String dataOutput;
    private static ObjectMapper mapperObj;
    private static JsonNode resultsNode;
    private static boolean isThereNewDates = false;
    private static boolean initialDateCheck = false;
    private static boolean isThereRecords = true;
    
    
    protected void getOpenFDAData() {
        
        JsonNode recall_number, reason_for_recall, status, distribution_pattern, product_quantity,
                         recall_initiation_date,state, event_id, product_type, product_description,
                         country, city, recalling_firm, report_date, epoch, 
                         voluntary_mandated, classification, code_info, id, openfda,initial_firm_notification;
        
        mapperObj = new ObjectMapper();
        
        try {
            
            getRawData(getOpenFDADataLink());
            
            if(TOTAL_RECORDS == 0)
            {
                getNumOfRecords();
            }
            
            int nextFileCounter = MAX_RECORD_LIMIT;
            
            while((CURRENT_NUM_OF_RECORDS < TOTAL_RECORDS) && ((TOTAL_RECORDS - CURRENT_NUM_OF_RECORDS) > MAX_RECORD_LIMIT))
//            for(int j = 0; j < 5; j++)  //for testing purpose
            {
//                System.out.println("addDataToFile Function 2");
//                System.out.println("TOTAL_RECORDS: " + TOTAL_RECORDS);
//                System.out.println("CURRENT_NUM_OF_RECORDS: " + CURRENT_NUM_OF_RECORDS);
//                System.out.println("Difference: "+ (TOTAL_RECORDS - CURRENT_NUM_OF_RECORDS));
//                System.out.println("MAX_RECORD_LIMIT: "+ MAX_RECORD_LIMIT);
                
                setRecordLimitParameter(MAX_RECORD_LIMIT);
                setNextRecordsLimitParameter(nextFileCounter);
                
                getRawData(getOpenFDADataLink());

                JsonNode rootNode = mapperObj.readTree(dataOutput);            
                resultsNode = rootNode.path("results");

                //For testing purpose
//                System.out.println("Output from openFDA Data: \n");
//                System.out.println("results: " + resultsNode);
//                System.out.println("Node_Size: " + resultsNode.size());
//                System.out.println("=================================================================================================");



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
//                    System.out.println("=================================================================================================");
//                    System.out.println("Data: " + '\n' + recall_number + '\n' +  reason_for_recall + '\n' +  status + '\n' +  distribution_pattern + '\n' + product_quantity + '\n' + 
//                             recall_initiation_date + '\n' +  state + '\n' +  event_id + '\n' +  product_type + '\n' +  product_description + '\n' + 
//                             country + '\n' +  city + '\n' +  recalling_firm + '\n' +  report_date + '\n' +  epoch + '\n' +  
//                             voluntary_mandated + '\n' +  classification + '\n' +  code_info + '\n' +  id + '\n' +  openfda + '\n' + initial_firm_notification);
                }
                
                nextFileCounter = nextFileCounter + resultsNode.size();
                CURRENT_NUM_OF_RECORDS = CURRENT_NUM_OF_RECORDS + resultsNode.size();

            }
        } catch (Exception e) {

                e.printStackTrace();

        }
    }
    
    protected void getNumOfRecordsBtwYears() throws IOException
    {

        getDateLimit(MIN_DATE,MAX_DATE);        
        getOpenFDAData();
         
        
        while(isThereNewDates)
        {
            
            if(isThereNewDates)
             {
                setSearchParameter(tempDate,MAX_DATE);
                setNextRecordsLimitParameter(NEXT_RECORD_LIMIT);
                setCurrentNumOfRecords(0);

             }
        
            getRawData(getOpenFDADataLink());
            getNumOfRecords();

            if(TOTAL_RECORDS < MAX_NEXT_RECORD_LIMIT)
            {
                isThereNewDates = false;
                minDate = tempDate;
                maxDate = MAX_DATE;
                getOpenFDAData();
                
            }
        }
        
    }

    public void setNextRecordLimit(int nextRecordLimit) {
        this.NEXT_RECORD_LIMIT = nextRecordLimit;
    }

    public void setCurrentNumOfRecords(int currentNumOfRecords) {
        this.CURRENT_NUM_OF_RECORDS = currentNumOfRecords;
    }    
    
    public void setSearchParameter(int minDateLimit, int maxDateLimit) {
        this.searchParameter = "&search=report_date:[" + minDateLimit +"+TO+"+ maxDateLimit +"]";;
    }

    public void setRecordLimitParameter(int recordNumber) {
        this.recordLimitParameter = "&limit=" + recordNumber;
    }
    
    public void setJsonFileName(int min_Date, int max_Date, int nextRecordCount) {
        this.jsonFileName = jsonDirPath + "/openFDAData_"+ min_Date + "_To_" + max_Date + "_" + nextRecordCount + ".json";
    }
    
    public void setNextRecordsLimitParameter(int nextRecords) {
        this.nextRecordsLimitParameter = "&skip=" + nextRecords;
    }
       
    protected void getRawData(String openFDADataLink)
    {
            Client client = Client.create();

            WebResource webResource = client.resource(openFDADataLink);

            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

            if (response.getStatus() != 200) 
            {
               System.out.println("Failed : HTTP error code : " + response.getStatus());
            }

            dataOutput = response.getEntity(String.class);
        
    }
    
    protected void getNumOfRecords() throws IOException
    {        
             
        ObjectMapper tempMapper = new ObjectMapper();

        JsonNode rootNode = tempMapper.readTree(dataOutput);            
        JsonNode numOfRecords = rootNode.path("meta").get("results").get("total");

        TOTAL_RECORDS = numOfRecords.asInt();
                
        
    }
    
    protected void getDateLimit(int lowDateLimit, int highestDateLimit) throws IOException
    {        
        minDate = lowDateLimit;
        maxDate = highestDateLimit;
        int CHANGE_YEAR = 10000; 
        int NEXT_YEAR = 8870;        
                

        
        if(isThereNewDates)
        {
            setSearchParameter(minDate,maxDate);
            setNextRecordsLimitParameter(NEXT_RECORD_LIMIT);
            
        }
        
        getRawData(getOpenFDADataLink());

        getNumOfRecords();
        
        System.out.println("TOTAL_RECORDS " + TOTAL_RECORDS);
        System.out.println("MAX_NEXT_RECORD_LIMIT " + MAX_NEXT_RECORD_LIMIT);
        
        while(TOTAL_RECORDS > MAX_NEXT_RECORD_LIMIT)
        {
            setSearchParameter(minDate,maxDate);
            
            
            getRawData(getOpenFDADataLink());
        
            getNumOfRecords();
                        
            if (TOTAL_RECORDS > MAX_NEXT_RECORD_LIMIT )
            {
                maxDate = maxDate - CHANGE_YEAR;

            }else
            {
                System.out.println("tempDate " + tempDate);
                 tempDate = maxDate + NEXT_YEAR;
                 isThereNewDates = true;
            }
            
        }
        
        
        
        
    }
    
    protected String getOpenFDADataLink()
    {
        String openFDADataLink;
                          
        //Eg:- https://api.fda.gov/food/enforcement.json?api_key=1mhfdv4IKKTKLbJ8DlDuzQoWKYWXnTf0cQOiLaYl&search=report_date:[20040101+TO+20151231]&limit=100&skip=100"
        openFDADataLink = openFDAFoodUrl + urlApiKey + searchParameter + recordLimitParameter + nextRecordsLimitParameter;
        
        System.out.println("openFDADataLink 2: " + openFDADataLink);
        
        return openFDADataLink;
    }
    
    protected void addDataToFile() throws IOException
    {    
        int nextFileCounter = MAX_RECORD_LIMIT;
        boolean isFirstLimitedPull = true;
        
        mapperObj = new ObjectMapper();
                
        getRawData(getOpenFDADataLink());
        
        if(TOTAL_RECORDS == 0)
        {
            getNumOfRecords();
        }
                
            while((CURRENT_NUM_OF_RECORDS < TOTAL_RECORDS) && ((TOTAL_RECORDS - CURRENT_NUM_OF_RECORDS) > MAX_RECORD_LIMIT))
    //           for(int j = 0; j < 5; j++)  //for testing purpose
            {   
//                System.out.println("addDataToFile Function 2");
//                System.out.println("TOTAL_RECORDS: " + TOTAL_RECORDS);
//                System.out.println("CURRENT_NUM_OF_RECORDS: " + CURRENT_NUM_OF_RECORDS);
//                System.out.println("Difference: "+ (TOTAL_RECORDS - CURRENT_NUM_OF_RECORDS));
//                System.out.println("MAX_RECORD_LIMIT: "+ MAX_RECORD_LIMIT);

                if((TOTAL_RECORDS - CURRENT_NUM_OF_RECORDS) < MAX_RECORD_LIMIT)
                {
    //                    System.out.println("In if statment: :)");

                    if(isFirstLimitedPull)
                    {
                        nextFileCounter = CURRENT_NUM_OF_RECORDS;
                        setJsonFileName(minDate,maxDate,CURRENT_NUM_OF_RECORDS + 100);
                        isFirstLimitedPull = false;
                    }

                    setRecordLimitParameter(CURRENT_NUM_OF_RECORDS);
                    setNextRecordsLimitParameter(nextFileCounter);


                    System.out.println("openFDADataLink 3: " + getOpenFDADataLink());

                    getRawData(getOpenFDADataLink());

                    writeDataToFile(true);

                    nextFileCounter = nextFileCounter + 1;
                    CURRENT_NUM_OF_RECORDS = CURRENT_NUM_OF_RECORDS + 1;


                }else
                {
                    setRecordLimitParameter(MAX_RECORD_LIMIT);
                    setNextRecordsLimitParameter(nextFileCounter);                    
                    setJsonFileName(minDate,maxDate,nextFileCounter);                

                    System.out.println("openFDADataLink 1: " + getOpenFDADataLink());                    

                    getRawData(getOpenFDADataLink());


                    writeDataToFile(false);

                    nextFileCounter = nextFileCounter + resultsNode.size();

                    CURRENT_NUM_OF_RECORDS = CURRENT_NUM_OF_RECORDS + resultsNode.size();
                }
            }
                   
    }
    
    protected void checkRecordLimit() throws IOException
    {

        getDateLimit(MIN_DATE,MAX_DATE);        
        addDataToFile();
         
        
        while(isThereNewDates)
        {
            
            if(isThereNewDates)
             {
                setSearchParameter(tempDate,MAX_DATE);
                setNextRecordsLimitParameter(NEXT_RECORD_LIMIT);
                setCurrentNumOfRecords(0);

             }
        
            getRawData(getOpenFDADataLink());
            getNumOfRecords();

            if(TOTAL_RECORDS < MAX_NEXT_RECORD_LIMIT)
            {
                isThereNewDates = false;
                minDate = tempDate;
                maxDate = MAX_DATE;
                addDataToFile();
                
            }
        }
        
    }
    
    protected void writeDataToFile(boolean isAppend) throws IOException
    {
        JsonNode rootNode = mapperObj.readTree(dataOutput);            
        resultsNode = rootNode.path("results");


        File dirPathObj = new File(jsonDirPath);
        File filePathObj = new File(jsonFileName);

        if (!dirPathObj.exists()) 
        {
            isDirCreated = dirPathObj.mkdirs();
            System.out.println("Directory successfully created");
            
            isFileCreated = filePathObj.createNewFile();
            System.out.println("File successfully created: " + jsonFileName);

        }

        if (isDirCreated && isFileCreated){
            System.out.println("Writng to File: " + jsonFileName);
            FileWriter writer;
            
            if(isAppend)
            {
                writer = new FileWriter(filePathObj,true);
            }else{
                writer = new FileWriter(filePathObj);
            }
            
            writer.write(resultsNode.toString());
            writer.flush();
            writer.close();
        }
        else{
            System.out.println("Failed to create directory");
        }
        
    }    
    
}
