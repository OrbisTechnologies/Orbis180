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
     
    
    /**
     * Count of queries since a specified date
     */
    @JsonProperty("QueriesSince")
    public String QueriesSince;
    
    
    /**
     * Average server response time across all queries
     */
    @JsonProperty("A_V_G_QueryTime")
    public String A_V_G_QueryTime;

    /**
     * count of queries so fat this year
     */
    @JsonProperty("Y_T_D_Queries")
    public String Y_T_D_Queries;
    
    /**
     * average number of queries per day
     */
    @JsonProperty("QueriesPerDay")
    public Integer QueriesPerDay;
    
    /**
    * Year over year change in queries
    */ 
    @JsonProperty("YearlyChangeInQueries")
    public Integer YearlyChangeInQueries;
    
    /**
     * Produces a json string (manual serialization is a workaround for an issue in Tomcat)
     * @return 
     */
    public String toJSONString(){
      return "{\"QueriesSince\": "+QueriesSince+", \"A_V_G_QueryTime\": "+A_V_G_QueryTime+", \"Y_T_D_Queries\": "+Y_T_D_Queries+", \"QueriesPerDay\": "+QueriesPerDay+", \"YearlyChangeInQueries\": "+YearlyChangeInQueries+" }";
    }
}
