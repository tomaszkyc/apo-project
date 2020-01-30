package com.tomaszkyc.app.core.services;

import com.tomaszkyc.app.core.services.properties.PropertiesCache;
import com.tomaszkyc.app.core.services.properties.PropertiesCacheImpl;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.core.services.translation.TranslationServiceImpl;

public class ServiceFactory {


    /**
     * method give TranslationService instance
     * @return TranslationService instance
     */
    public static TranslationService getTranslationService() {

        return TranslationServiceImpl.getInstance();

    }

    public static PropertiesCache getPropertiesCacheService() {

        return PropertiesCacheImpl.getInstance();
    }



}
