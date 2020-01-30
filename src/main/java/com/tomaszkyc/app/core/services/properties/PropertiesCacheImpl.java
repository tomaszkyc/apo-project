package com.tomaszkyc.app.core.services.properties;

import com.tomaszkyc.app.MainApp;
import com.tomaszkyc.app.core.validator.ImageHeightValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class PropertiesCacheImpl implements PropertiesCache {

    private static final Logger log = LogManager.getLogger(ImageHeightValidator.class.getName());

    private final Properties configProp = new Properties();

    private PropertiesCacheImpl()
    {

        try {
            //Private constructor to restrict new instances
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("app.properties");

            configProp.load(in);
            log.debug("Properties file loaded correctly");
        } catch (Exception e) {
            log.error("There was an error during properties loading: " + e.getMessage());
        }
    }


    private static class LazyHolder
    {
        private static final PropertiesCache INSTANCE = new PropertiesCacheImpl();
    }

    public static PropertiesCache getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key){
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames(){
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key){
        return configProp.containsKey(key);
    }

    @Override
    public void setProperty(String key, String value) {
        configProp.setProperty(key, value);
    }
}
