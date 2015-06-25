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
    
    @JsonProperty("Date")
    String Date;
    //date
    @JsonProperty("Location")
    String Location;
//location
    
    @JsonProperty("SearchField")
    String SearchField;
    
    @JsonProperty("Count")
    int Count;
}
