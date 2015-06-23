package com.orbis.orbis180.storage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.slf4j.LoggerFactory;

/**
 * Implements <code>IStore</code> to store the data from the Food openFDA API.  
 * 
 * @author Carlos Andino <candino@orbistechnologies.com>
 */
public class SesameFoodStore implements IStore{

    final static protected org.slf4j.Logger logger = LoggerFactory.getLogger("command");
    private URI reportId;
    private String baseURI;
    private ValueFactory valueFactory;
    private SesameInterface sesame;
    
    /**
     * Parses and stores the contents of the <code>jsonData</code> parameter.
     * @param jsonData 
     */
    @Override
    public void storeFromJson(String jsonData) {
        //Initialize sesame interface items
        sesame = new SesameInterface();
        //TODO: Obtain repository from configuration file
        String repositoryName = "openFDA-test";
        if(sesame.repositoryExist(repositoryName)){
            sesame.openRepository(repositoryName);
        } else {
            sesame.createRepository(repositoryName);
        }
        
        //Load ontology model
        sesame.loadFile(new File(getClass().getClassLoader().getResource("models/openFDA-model.owl").getFile()), RDFFormat.RDFXML);
        baseURI = sesame.getBaseUri();
        
        //Parse the json string
        ObjectMapper m = new ObjectMapper();
        valueFactory = sesame.getValueFactory();
        
        try {
            JsonNode apiResponse = m.readTree(jsonData);
            JsonNode results = apiResponse.path("results");
            logger.debug("Found {} results.", results.size());
            if (!results.isMissingNode()) {  //Make sure that data contains results
                for (int i = 0; i < results.size(); i++) {
                    
                    
                    //Define unique identifier for the subject
                    String recallNumber = results.get(i).get("recall_number").asText();
                    reportId = valueFactory.createURI(baseURI + "/Report/" + recallNumber);
                    sesame.storeTriple(reportId, RDF.TYPE, valueFactory.createURI(baseURI + "#EnforcementReport"));
                    
                    //Parse and store contents from json
                    storeProperty("recallNumber", recallNumber);
                    String reasonForRecall = results.get(i).get("reason_for_recall").asText();
                    storeProperty("reasonForRecall", reasonForRecall);
                    String distributionPattern = results.get(i).get("distribution_pattern").asText();
                    storeProperty("distributionPattern", distributionPattern);
                    String quantity = results.get(i).get("product_quantity").asText();
                    storeProperty("productQuantity", quantity);
                    String recallDate = results.get(i).get("recall_initiation_date").asText();
                    storeDate("hasRecallInitiationDate", recallDate);
                    String city = results.get(i).get("city").asText();
                    String state = results.get(i).get("state").asText();
                    String country = results.get(i).get("country").asText();
                    storeLocation("hasLocation", city, state, country);
                    String eventId = results.get(i).get("event_id").asText();
                    storeProperty("eventId", eventId);
                    String reportDate = results.get(i).get("report_date").asText();
                    storeDate("hasReportDate", reportDate);
                    String voluntary = results.get(i).get("voluntary_mandated").asText();
                    storeProperty("voluntaryMandated", voluntary);
                    String codeInfo = results.get(i).get("code_info").asText();
                    storeProperty("codeInfo", codeInfo);
                    String notification = results.get(i).get("initial_firm_notification").asText();
                    storeProperty("initialFirmNotification", notification);
                    //TODO:Find unique identifier for food elements.  Currently the only difference is in the description, 
                    //which may be too long in some cases. Once determined, the Food class can be instantiated individually 
                    //and the description added to it.
                    String productDescription = results.get(i).get("product_description").asText();
                    storeProperty("#productDescription", notification);
                    
                    //Storing status as a relationship
                    String status = results.get(i).get("status").asText();
                    URI statusUri = valueFactory.createURI(baseURI + "#" + status.replace(" ", ""));
                    sesame.storeTriple(statusUri, RDFS.SUBCLASSOF, valueFactory.createURI(baseURI + "#Status"));
                    sesame.storeTriple(reportId, valueFactory.createURI(baseURI + "#hasStatus"),statusUri);
                    
                    //Store product type as a relationship
                    String productType = results.get(i).get("product_type").asText();
                    //Currently only 'Food' is in the ontology model.  The following lines will create a new URI for the product type
                    //and will make it a subclass of Product.  This will cover for any missing products in the model
                    URI productTypeUri = valueFactory.createURI(baseURI + "#" + productType.replace(" ", ""));
                    sesame.storeTriple(productTypeUri, RDFS.SUBCLASSOF, valueFactory.createURI(baseURI + "#Product"));
                    sesame.storeTriple(reportId, valueFactory.createURI(baseURI + "#hasProductType"),productTypeUri);
                    
                    //Store recalling firm as a relationship
                    String recallingFirm = results.get(i).get("recalling_firm").asText();
                    URI firmUri = valueFactory.createURI(baseURI + "/Organization/" + recallingFirm.toLowerCase().replace(" ", "_").replace(".", ""));
                    sesame.storeTriple(firmUri, RDF.TYPE, valueFactory.createURI(baseURI + "#Organization"));
                    sesame.storeTriple(firmUri, valueFactory.createURI(baseURI + "#organizationName"), 
                            valueFactory.createLiteral(recallingFirm));
                    sesame.storeTriple(reportId, valueFactory.createURI(baseURI + "#hasRecallingFirm"), firmUri);
                    
                    //Store Classification type as a relationship
                    String classification = results.get(i).get("classification").asText();
                    //Currently only 'Class I - III' are in the ontology model.  The following lines will create a new URI for the classification
                    //and will make it a subclass of Classification.  This will cover for any missing classifications in the model
                    URI classificationTypeUri = valueFactory.createURI(baseURI + "#" + classification.replace(" ", ""));
                    sesame.storeTriple(classificationTypeUri, RDFS.SUBCLASSOF, valueFactory.createURI(baseURI + "#Classification"));
                    sesame.storeTriple(reportId, valueFactory.createURI(baseURI + "#hasClassification"), classificationTypeUri);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(SesameFoodStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void storeProperty(String propertyName, String propertyValue){
        URI predicate = valueFactory.createURI(baseURI + "#" + propertyName);
        Literal object = valueFactory.createLiteral(propertyValue);
        sesame.storeTriple(reportId,predicate, object );
    }
    
    private void storeDate(String propertyName, String dateValue){
        try {
            URI predicate = valueFactory.createURI(baseURI + "#" + propertyName);
            
            //Presuming date has already been normalized
            URI timeUri = valueFactory.createURI(baseURI + "/Time/" + dateValue);
            URI monthUri = valueFactory.createURI(baseURI + "#month");
            URI dayUri = valueFactory.createURI(baseURI + "#day");
            URI yearUri = valueFactory.createURI(baseURI + "#year");
            URI timestampUri = valueFactory.createURI(baseURI + "#timestamp");
            
            //Parsing date elements
            int month = Integer.parseInt(dateValue.substring(4, 6));
            int year = Integer.parseInt(dateValue.substring(0, 4));
            int day = Integer.parseInt(dateValue.substring(6, 8));
            
            //Store timestamp in compatible Date format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date timestamp = formatter.parse(dateValue);
            
            //Building Time entity
            sesame.storeTriple(timeUri, RDF.TYPE, valueFactory.createURI(baseURI + "#Time"));
            sesame.storeTriple(timeUri, monthUri, valueFactory.createLiteral(month));
            sesame.storeTriple(timeUri, dayUri, valueFactory.createLiteral(day));
            sesame.storeTriple(timeUri, yearUri, valueFactory.createLiteral(year));
            sesame.storeTriple(timeUri, timestampUri, valueFactory.createLiteral(timestamp));
            //Associating Time entity to the 'propertyName' of the subject
            sesame.storeTriple(reportId, predicate, timeUri);
            
        } catch (ParseException ex) {
            logger.error("Date value for {} could not be parsed and will not be stored.", propertyName);
        }
    }
    
    private void storeLocation(String propertyName, String city, String state, String country){
        URI predicate = valueFactory.createURI(baseURI + "#" + propertyName);
        
        //Creating unique identifier for location as "city-state-country"
        String locationID = city.toLowerCase().replace(" ","_") + "-" + 
                state.toLowerCase().replace(" ","_") + "-" + 
                country.toLowerCase().replace(" ","_");
        
        //Generating properties URI
        URI locationUri = valueFactory.createURI(baseURI + "/Location/" + locationID);
        URI cityUri = valueFactory.createURI(baseURI + "#city");
        URI stateUri = valueFactory.createURI(baseURI + "#state");
        URI countryUri = valueFactory.createURI(baseURI + "#country");

        //Building Location entity
        sesame.storeTriple(locationUri, RDF.TYPE, valueFactory.createURI(baseURI + "#Location"));
        sesame.storeTriple(locationUri, cityUri, valueFactory.createLiteral(city));
        sesame.storeTriple(locationUri, stateUri, valueFactory.createLiteral(state));
        sesame.storeTriple(locationUri, countryUri, valueFactory.createLiteral(country));
        //Associating Location entity to the 'propertyName' of the subject
        sesame.storeTriple(reportId, predicate, locationUri);
    }
    
}
