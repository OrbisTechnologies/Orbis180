/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

import com.orbis.orbis180.storage.IStore;
import com.orbis.orbis180.storage.SesameFoodStore;
import com.orbis.orbis180.storage.StoreFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author aparmar
 */
public class OpenFDAClient {
    
    private static int NEXT_RECORD_LIMIT;
    private static int MAX_RECORD_LIMIT;
    private static int MAX_NEXT_RECORD_LIMIT; 
    private static int RECORD_WRITED_TO_FILE;
    
    private static String OPEN_FDA_FOOD_URL;
    private static String URL_API_KEY;
    
    private static String JSON_DIR_PATH;
    
    private int minDateVal;
    private int maxDateVal;    
    
    private int currNumOfRecords = 0;   
    private int totalRecords = 0; 
    
    private int tempDate = 20150101;
    private int minDate;
    private int maxDate;  
    
    final static protected Logger logger = LoggerFactory.getLogger(OpenFDAClient.class);

    private String searchParameter= "&search=report_date:[" + minDateVal +"+TO+"+ maxDateVal +"]";

    private String recordLimitParameter= "&limit=" + MAX_RECORD_LIMIT;
    private String nextRecordsLimitParameter = "&skip=" + NEXT_RECORD_LIMIT;     
    
    private String jsonFileName =  JSON_DIR_PATH + "/openFDAData_Next_"+ minDateVal + "_To_" + maxDateVal + "_" + RECORD_WRITED_TO_FILE + ".json";
    
    private boolean isDirCreated = false;
    private boolean isFileCreated = false;
    private boolean isPullDataToFile = true;
    
    private static String dataOutput;
    private static ObjectMapper mapperObj;
    private static JsonNode resultsNode;
    private static boolean isThereNewDates = false;
    private static boolean initialDateCheck = false;
    private static boolean isThereRecords = true;
    
    
    public OpenFDAClient() {
        initialize();
    }
    
    private void initialize() {
		Properties config = new Properties();
		try {
			config.load(getClass().getResourceAsStream("/conf/config.properties"));
			
			NEXT_RECORD_LIMIT = Integer.parseInt(config.getProperty("com.orbis.orbis180.rest.openFDAClient.nextRecordLimit"));
                        MAX_RECORD_LIMIT = Integer.parseInt(config.getProperty("com.orbis.orbis180.rest.openFDAClient.maxRecordLimit"));
			MAX_NEXT_RECORD_LIMIT = Integer.parseInt(config.getProperty("com.orbis.orbis180.rest.openFDAClient.maxNextRecordLimit"));
			RECORD_WRITED_TO_FILE = Integer.parseInt(config.getProperty("com.orbis.orbis180.rest.openFDAClient.recordWritedToFile"));
                        
                        OPEN_FDA_FOOD_URL = config.getProperty("com.orbis.orbis180.rest.openFDAClient.openFDAFoodUrl");
                        URL_API_KEY = config.getProperty("com.orbis.orbis180.rest.openFDAClient.urlAPIKey");
                        
                        JSON_DIR_PATH = config.getProperty("com.orbis.orbis180.rest.openFDAClient.jsonDirPath");
                        
                        minDateVal= Integer.parseInt(config.getProperty("com.orbis.orbis180.rest.openFDAClient.minDateVal"));
                        maxDateVal= Integer.parseInt(config.getProperty("com.orbis.orbis180.rest.openFDAClient.maxDateVal"));
                        
                        setSearchParameter(minDateVal,maxDateVal);                        
                        setRecordLimitParameter(MAX_RECORD_LIMIT);
                        setNextRecordsLimitParameter(NEXT_RECORD_LIMIT);  
                        
		} catch (Exception e) {
                     e.printStackTrace();
			
		} 
	}
    
    protected void addDataToFile() throws IOException
    {    
        int nextFileCounter = MAX_RECORD_LIMIT;
        boolean isFirstLimitedPull = true;
        
        mapperObj = new ObjectMapper();
                
        getRawData(getOpenFDADataLink());
        
        if(totalRecords == 0)
        {
            getNumOfRecords();
        }
                
            while((currNumOfRecords < totalRecords) && ((totalRecords - currNumOfRecords) > MAX_RECORD_LIMIT))
            {   

                if((totalRecords - currNumOfRecords) < MAX_RECORD_LIMIT)
                {

                    if(isFirstLimitedPull)
                    {
                        nextFileCounter = currNumOfRecords;
                        setJsonFileName(minDate,maxDate,currNumOfRecords + 100);
                        isFirstLimitedPull = false;
                    }

                    setRecordLimitParameter(currNumOfRecords);
                    setNextRecordsLimitParameter(nextFileCounter);


                    logger.debug("openFDADataLink 3: " + getOpenFDADataLink());

                    getRawData(getOpenFDADataLink());

                    writeDataToFile(true);

                    nextFileCounter = nextFileCounter + 1;
                    currNumOfRecords = currNumOfRecords + 1;


                }else
                {
                    setRecordLimitParameter(MAX_RECORD_LIMIT);
                    setNextRecordsLimitParameter(nextFileCounter);                    
                    setJsonFileName(minDate,maxDate,nextFileCounter);                

                    logger.debug("openFDADataLink 1: " + getOpenFDADataLink());                    

                    getRawData(getOpenFDADataLink());


                    writeDataToFile(false);

                    nextFileCounter = nextFileCounter + resultsNode.size();

                    currNumOfRecords = currNumOfRecords + resultsNode.size();
                }
            }
                   
    }
    
    
    
    
    protected void getNumOfRecordsBtwYears() throws IOException
    {

        getDateLimit(minDateVal,maxDateVal);        
        getOpenFDAData();
         
        
        while(isThereNewDates)
        {
            
            if(isThereNewDates)
             {
                setSearchParameter(tempDate,maxDateVal);
                setNextRecordsLimitParameter(NEXT_RECORD_LIMIT);
                setCurrentNumOfRecords(0);

             }
        
            getRawData(getOpenFDADataLink());
            getNumOfRecords();

            if(totalRecords < MAX_NEXT_RECORD_LIMIT)
            {
                isThereNewDates = false;
                minDate = tempDate;
                maxDate = maxDateVal;
                getOpenFDAData();
                
            }
        }
        
    }

    public void setCurrentNumOfRecords(int currentNumOfRecords) {
        this.currNumOfRecords = currentNumOfRecords;
    }    
    
    public void setSearchParameter(int minDateLimit, int maxDateLimit) {
        this.searchParameter = "&search=report_date:[" + minDateLimit +"+TO+"+ maxDateLimit +"]";;
    }

    public void setRecordLimitParameter(int recordNumber) {
        this.recordLimitParameter = "&limit=" + recordNumber;
    }
    
    public void setJsonFileName(int min_Date, int max_Date, int nextRecordCount) {
        this.jsonFileName = JSON_DIR_PATH + "/openFDAData_"+ min_Date + "_To_" + max_Date + "_" + nextRecordCount + ".json";
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
               logger.debug("Failed : HTTP error code : " + response.getStatus());
            }

            dataOutput = response.getEntity(String.class);
        
    }
    
    protected void getNumOfRecords() throws IOException
    {        
             
        ObjectMapper tempMapper = new ObjectMapper();

        JsonNode rootNode = tempMapper.readTree(dataOutput);            
        JsonNode numOfRecords = rootNode.path("meta").get("results").get("total");

        totalRecords = numOfRecords.asInt();
                
        
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
        
        logger.debug("TOTAL_RECORDS " + totalRecords);
        logger.debug("MAX_NEXT_RECORD_LIMIT " + MAX_NEXT_RECORD_LIMIT);
        
        while(totalRecords > MAX_NEXT_RECORD_LIMIT)
        {
            setSearchParameter(minDate,maxDate);
            
            
            getRawData(getOpenFDADataLink());
        
            getNumOfRecords();
                        
            if (totalRecords > MAX_NEXT_RECORD_LIMIT )
            {
                maxDate = maxDate - CHANGE_YEAR;

            }else
            {
                logger.debug("tempDate " + tempDate);
                 tempDate = maxDate + NEXT_YEAR;
                 isThereNewDates = true;
            }
            
        }    
    }
    
    protected String getOpenFDADataLink()
    {
        String openFDADataLink;
                          
        //Eg:- https://api.fda.gov/food/enforcement.json?api_key=<Key_Goes_Here>&search=report_date:[20040101+TO+20151231]&limit=100&skip=100"
        openFDADataLink = OPEN_FDA_FOOD_URL + "api_key=" + URL_API_KEY + searchParameter + recordLimitParameter + nextRecordsLimitParameter;
        
        logger.debug("openFDADataLink 2: " + openFDADataLink);
        
        return openFDADataLink;
    }
    
    
    
    protected void checkRecordLimit() throws IOException
    {

        getDateLimit(minDateVal,maxDateVal);        
        addDataToFile();
         
        
        while(isThereNewDates)
        {
            
            if(isThereNewDates)
             {
                setSearchParameter(tempDate,maxDateVal);
                setNextRecordsLimitParameter(NEXT_RECORD_LIMIT);
                setCurrentNumOfRecords(0);

             }
        
            getRawData(getOpenFDADataLink());
            getNumOfRecords();

            if(totalRecords < MAX_NEXT_RECORD_LIMIT)
            {
                isThereNewDates = false;
                minDate = tempDate;
                maxDate = maxDateVal;
                addDataToFile();
                
            }
        }
        
    }
    
    protected void writeDataToFile(boolean isAppend) throws IOException
    {
        JsonNode rootNode = mapperObj.readTree(dataOutput);            
        resultsNode = rootNode.path("results");


        File dirPathObj = new File(JSON_DIR_PATH);
        File filePathObj = new File(jsonFileName);

        if (!dirPathObj.exists()) 
        {
            isDirCreated = dirPathObj.mkdirs();
            logger.debug("Directory successfully created");
            
            isFileCreated = filePathObj.createNewFile();
            logger.debug("File successfully created: " + jsonFileName);

        }

        if (isDirCreated && isFileCreated){
            logger.debug("Writng to File: " + jsonFileName);
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
            logger.debug("Failed to create directory");
        }
        
    }
    
    protected void getOpenFDAData() {
        
        JsonNode recall_number, reason_for_recall, status, distribution_pattern, product_quantity,
                         recall_initiation_date,state, event_id, product_type, product_description,
                         country, city, recalling_firm, report_date, epoch, 
                         voluntary_mandated, classification, code_info, id, openfda,initial_firm_notification;
        
        mapperObj = new ObjectMapper();
        
        try {
            
            getRawData(getOpenFDADataLink());
            
            if(totalRecords == 0)
            {
                getNumOfRecords();
            }
            
            int nextFileCounter = MAX_RECORD_LIMIT;
            IStore dataStorage = StoreFactory.getFoodAPIStore();
            
            while((currNumOfRecords < totalRecords) && ((totalRecords - currNumOfRecords) > MAX_RECORD_LIMIT))
            {
                
                setRecordLimitParameter(MAX_RECORD_LIMIT);
                setNextRecordsLimitParameter(nextFileCounter);
                
                getRawData(getOpenFDADataLink());

                JsonNode rootNode = mapperObj.readTree(dataOutput);            
                resultsNode = rootNode.path("results");
                
                dataStorage.storeFromJson(dataOutput);
                                
                nextFileCounter = nextFileCounter + resultsNode.size();
                currNumOfRecords = currNumOfRecords + resultsNode.size();

            }
        } catch (Exception e) {

                e.printStackTrace();

        }
    }
    
}
