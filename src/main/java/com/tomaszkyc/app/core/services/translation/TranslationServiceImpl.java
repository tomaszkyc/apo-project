package com.tomaszkyc.app.core.services.translation;

import com.tomaszkyc.app.MainApp;
import com.tomaszkyc.app.core.services.ServiceFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationServiceImpl implements TranslationService, Serializable {

    private static final long serialVersionUID = 1L;

    private ResourceBundle translations;

    public static TranslationService getInstance() {
        return new TranslationServiceImpl();
    }

    private TranslationServiceImpl() {

        String languageShortcut = ServiceFactory.getPropertiesCacheService().getProperty("actualLanguageCode");
        if (StringUtils.isEmpty(languageShortcut) ) {
            this.translations = ResourceBundle.getBundle("MessagesBundle", new Locale("pl", "PL"));
        }
        else if ( languageShortcut.equalsIgnoreCase("en") ) {
            this.translations = ResourceBundle.getBundle("MessagesBundle", new Locale("en", "US"));
        }
        else if ( languageShortcut.equalsIgnoreCase("pl") ) {
            this.translations = ResourceBundle.getBundle("MessagesBundle", new Locale("pl", "PL"));
        }
    }


    @Override
    public String getTranslation(String key) {
        String translation = null;
        try{
            //try to find translation for given key
            if (SystemUtils.IS_OS_WINDOWS) {
                translation = new String( translations.getString( key ).getBytes("ISO-8859-1"), "UTF-8" );
            }
            else {
                translation = translations.getString( key );
            }


        }
        catch( Exception exception ) {
            //in case translation can't be found
            throw new NoTranslationFoundException(key);
        }
        //return translation if found
        return translation;
    }

}
