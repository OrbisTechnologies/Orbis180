/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 

package com.orbis.orbis180.dataStructures;

/**
 *
 * @author cblount
 */

package com.orbis.orbis180.dataStructures;
import org.codehaus.jackson.annotate.JsonProperty;

/***
 * This Class is used for specifying a Query that has been run
 * @author cblount
 */
public class WileyQuery {
    
    //date
    @JsonProperty("Date")
    public String date;
    
    //location
    @JsonProperty("Location")
    public String location;

    //searchField
    @JsonProperty("SearchField")
    public String searchField;
    
    //used for outgoing messages about 
    @JsonProperty("Count")
    public Integer count;
    
    //time it took the server to respond
    @JsonProperty("ResponseTime")
    public Integer responseTime;
}
