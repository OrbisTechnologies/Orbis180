/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orbis.orbis180.rest;

/**
 *
 * @author aparmar
 */
public class SearchQuery {
    
    private String date;
    private String location;
    private String foodDescription;
    
    
    public SearchQuery(String date,String location,String foodDescription)
    {
        this.date = date;
        this.location = location;
        this.foodDescription = foodDescription;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getFoodDescription() {
        return foodDescription;
    }
    
    
    
    
}
