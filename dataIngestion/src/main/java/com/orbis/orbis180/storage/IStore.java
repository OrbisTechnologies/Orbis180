package com.orbis.orbis180.storage;

/**
 * <code>IStore</code> provides the storage contract to ingest data from the 
 * openFDA APIs.  
 * 
 * @author Carlos Andino <candino@orbistechnologies.com>
 */
public interface IStore {
    
    /**
     * Parses and stores the contents of the <code>jsonData</code> parameter.
     * @param jsonData 
     */
    public void storeFromJson(String jsonData);
    
}