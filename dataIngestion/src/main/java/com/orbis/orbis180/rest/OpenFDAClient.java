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
 * @author Ankit Parmar
 * This class:
 *  - Get data from the openFDA and add data to Sesame
 *  - Get data from the openFDA and write data to file
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
    
    /**
     *create logger object
     */
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
    
    /**
     *constructor calls initialize function 
     */
    public OpenFDAClient() {
        initialize();
    }
    
    /**
     * 
     * Initialize Variables
     * 
     * */
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
                        
                        
                        validateDateFormat(config.getProperty("com.orbis.orbis180.rest.openFDAClient.maxDateVal"));
                        
                        minDateVal= Integer.parseInt(validateDateFormat(config.getProperty("com.orbis.orbis180.rest.openFDAClient.minDateVal")));
                        maxDateVal= Integer.parseInt(validateDateFormat(config.getProperty("com.orbis.orbis180.rest.openFDAClient.maxDateVal")));
                        
                        setSearchParameter(minDateVal,maxDateVal);                        
                        setRecordLimitParameter(MAX_RECORD_LIMIT);
                        setNextRecordsLimitParameter(NEXT_RECORD_LIMIT);  
                        
		} catch (Exception e) {
                     e.printStackTrace();
			
		} 
	}
    
    /**
     * 
     * get data from openFDA API and send it Sesame
     * 
     * */
    private void getOpenFDAData() {
                
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
    
    /**
     * 
     * get data from openFDA and write it to file
     * 
     * */
    private void addDataToFile() throws IOException
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


                    logger.info("openFDADataLink addDataToFile 1: " + getOpenFDADataLink());

                    getRawData(getOpenFDADataLink());

                    writeDataToFile(true);

                    nextFileCounter = nextFileCounter + 1;
                    currNumOfRecords = currNumOfRecords + 1;


                }else
                {
                    setRecordLimitParameter(MAX_RECORD_LIMIT);
                    setNextRecordsLimitParameter(nextFileCounter);                    
                    setJsonFileName(minDate,maxDate,nextFileCounter);                

                    logger.info("openFDADataLink addDataToFile 2: " + getOpenFDADataLink());                    

                    getRawData(getOpenFDADataLink());


                    writeDataToFile(false);

                    nextFileCounter = nextFileCounter + resultsNode.size();

                    currNumOfRecords = currNumOfRecords + resultsNode.size();
                }
            }
                   
    }
    
    
    /**
     * 
     * Check if total number of records between date range. If total number of 
     * records between date range are less than 5000, then set the max date.
     * @throws java.io.IOException
     *
     *  */
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
    
    /**
     * 
     * set current number of records variable
     * @param currentNumOfRecords current number of records
     * 
     * */
    private void setCurrentNumOfRecords(int currentNumOfRecords) {
        this.currNumOfRecords = currentNumOfRecords;
    }    
    
    /**
     * 
     * set date range for openFDA URL
     * @param minDateLimit lower date limit
     * @param maxDateLimit higher date limit
     * 
     * */
    
    private void setSearchParameter(int minDateLimit, int maxDateLimit) {
        this.searchParameter = "&search=report_date:[" + minDateLimit +"+TO+"+ maxDateLimit +"]";;
    }
    
    /**
     * 
     * set record limit for openFDA URL
     * @param recordNumber number of records to read
     * 
     * */
    private void setRecordLimitParameter(int recordNumber) {
        this.recordLimitParameter = "&limit=" + recordNumber;
    }
    
    /**
     * 
     * set json file name for openFDA URL
     * @param min_Date lower date limit
     * @param max_Date higher date limit
     * @param nextRecordCount next set of records
     * 
     * */
    private void setJsonFileName(int min_Date, int max_Date, int nextRecordCount) {
        this.jsonFileName = JSON_DIR_PATH + "/openFDAData_"+ min_Date + "_To_" + max_Date + "_" + nextRecordCount + ".json";
    }
    
    /**
     * 
     * set skip limit
     * @param nextRecordCount: next set of records
     * 
     * */
    private void setNextRecordsLimitParameter(int nextRecords) {
        this.nextRecordsLimitParameter = "&skip=" + nextRecords;
    }
    
    /**
     * 
     * set connection and get data from openFDA API
     * @param openFDADataLink openFDA URL
     * 
     * */
    private void getRawData(String openFDADataLink)
    {
            Client client = Client.create();

            WebResource webResource = client.resource(openFDADataLink);

            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

            if (response.getStatus() != 200) 
            {
               logger.error("Failed : HTTP error code : " + response.getStatus());
            }

            dataOutput = response.getEntity(String.class);
        
    }
    
    /**
     * 
     * get number of records
     * 
     * */
    private void getNumOfRecords() throws IOException
    {        
             
        ObjectMapper tempMapper = new ObjectMapper();

        JsonNode rootNode = tempMapper.readTree(dataOutput);            
        JsonNode numOfRecords = rootNode.path("meta").get("results").get("total");

        totalRecords = numOfRecords.asInt();
                
        
    }
    
    /**
     * 
     * get data range for records less than 5000
     * @param lowDateLimit Minimum date limit
     * @param highestDateLimit Maximum date limit
     * 
     * */
    private void getDateLimit(int lowDateLimit, int highestDateLimit) throws IOException
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
    
    /**
     * 
     *setup openFDA URL
     * 
     * */
    private String getOpenFDADataLink()
    {
        String openFDADataLink;
                          
        //Eg:- https://api.fda.gov/food/enforcement.json?api_key=<Key_Goes_Here>&search=report_date:[20040101+TO+20151231]&limit=100&skip=100"
        openFDADataLink = OPEN_FDA_FOOD_URL + "api_key=" + URL_API_KEY + searchParameter + recordLimitParameter + nextRecordsLimitParameter;
        
        logger.info("openFDADataLink getOpenFDADataLink Func: " + openFDADataLink);
        
        return openFDADataLink;
    }
    
    
    /**
     * 
     * 
     * Check the number of records between date range
     * @throws IOException 
     **/
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
    
    /**
     * 
     * create new folder and file, write data to that file
     * @param isAppend is data need to be append to the end of file
     * 
     * */
    private void writeDataToFile(boolean isAppend) throws IOException
    {
        JsonNode rootNode = mapperObj.readTree(dataOutput);            
        resultsNode = rootNode.path("results");


        File dirPathObj = new File(JSON_DIR_PATH);
        File filePathObj = new File(jsonFileName);

        if (!dirPathObj.exists()) 
        {
            isDirCreated = dirPathObj.mkdirs();
            logger.info("Directory successfully created");
            
            isFileCreated = filePathObj.createNewFile();
            logger.info("File successfully created: " + jsonFileName);

        }

        if (isDirCreated && isFileCreated){
            logger.info("Writng to File: " + jsonFileName);
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
    
    /**
     * 
     * Verify if the date format is correct
     * @param dateValue date that need be to validated
     * @return if date format is correct then return date
     * else thrown error
     *
     *   
     **/
    protected String validateDateFormat(String dateValue)
    {
       String val = "";
       
       String datePattern = "\\d{4}-\\d{2}-\\d{2}";
       boolean isDate = dateValue.matches(datePattern);
       
       logger.debug("isdate: " + isDate);
       
       if(isDate)
       {
           logger.debug("dateVal: " + dateValue); 
           val = dateValue.replaceAll("-", "").trim();
           logger.debug("dateVal: " + val);
       
           
       }else{
           logger.error("The date format must be YYYY-MM-DD");
       }
        return val;
    }
    
}
