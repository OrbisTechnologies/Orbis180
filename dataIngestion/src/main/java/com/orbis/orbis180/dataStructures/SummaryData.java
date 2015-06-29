/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.dataStructures;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author cblount
 */
@JsonAutoDetect
public class SummaryData {
     
    //date
    @JsonProperty("QueriesSince")
    public String QueriesSince;
    
    //location
    @JsonProperty("A_V_G_QueryTime")
    public String A_V_G_QueryTime;

    //searchField
    @JsonProperty("Y_T_D_Queries")
    public String Y_T_D_Queries;
    
    //used for outgoing messages about 
    @JsonProperty("QueriesPerDay")
    public Integer QueriesPerDay;
    
    //time it took the server to respond
    @JsonProperty("YearlyChangeInQueries")
    public Integer YearlyChangeInQueries;
    
    public String toJSONString(){
      return "{\"QueriesSince\": "+QueriesSince+", \"A_V_G_QueryTime\": "+A_V_G_QueryTime+", \"Y_T_D_Queries\": "+Y_T_D_Queries+", \"QueriesPerDay\": "+QueriesPerDay+", \"YearlyChangeInQueries\": "+YearlyChangeInQueries+" }";
    }
}
