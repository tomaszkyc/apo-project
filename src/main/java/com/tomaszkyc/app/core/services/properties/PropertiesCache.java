package com.tomaszkyc.app.core.services.properties;

import java.util.Set;

public interface PropertiesCache {

    /**
     * gets property with given key
     * @param key key
     * @return property for keys
     */
    String getProperty(String key);

    /**
     * get all properties names
     * @return collection of all properties names
     */
    Set<String> getAllPropertyNames();

    /**
     * checks if property has given keys
     * @param key key to find
     * @return true if contains key false otherwise
     */
    boolean containsKey(String key);

    /**
     * adds property with given key and value
     * @param key given key
     * @param value value to set
     */
    void setProperty(String key, String value);

}
