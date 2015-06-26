package com.orbis.orbis180.storage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <code>StoreFactory</code> provides methods to instantiate the openFDA data
 * storage classes.
 *
 * @author Carlos Andino <candino@orbistechnologies.com>
 */
public class StoreFactory {
    private static IStore store;
    private static final Logger logger = LoggerFactory.getLogger(StoreFactory.class);
    
    /**
     * Returns the configured <code>IStore</code> instance to use to parse and 
     * store the contents of the openFDA food API response. 
     */
    public static IStore getFoodAPIStore(){
        
        Properties config = new Properties();
        try {
            config.load(StoreFactory.class.getResourceAsStream("/conf/config.properties"));
            String configuredStoreClass = config.getProperty("com.orbis.orbis180.storage.foods.api");
            Class storeClass = StoreFactory.class.getClassLoader().loadClass(configuredStoreClass);
            Constructor<IStore> constructor = storeClass.getConstructor( new Class[0]);
            store = constructor.newInstance();
        } catch (IOException ex) {
            logger.error("Configuration file not found.\n{}",ex.getLocalizedMessage());
        } catch (ClassNotFoundException ex) {
            logger.error("Class for openFDA Food Storage was not found.\n{}",ex.getLocalizedMessage());
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
           logger.error(ex.getLocalizedMessage());
        }
        return store;
    }
}
