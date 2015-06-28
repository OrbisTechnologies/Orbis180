package com.orbis.orbis180.data;

/**
 * <code>Location</code> stores the data of a location entity as defined in the
 * openFDA ontology model.
 *
 * @author Carlos Andino <candino@orbistechnologies.com>
 */
public class Location {
    
    private String city;
    private String state;
    private String coutry;
    private double latitude;
    private double longitude;
    private String id;
    
    public Location(){
        
    }
    
    public Location(String id){
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCoutry() {
        return coutry;
    }

    public void setCoutry(String coutry) {
        this.coutry = coutry;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
}
