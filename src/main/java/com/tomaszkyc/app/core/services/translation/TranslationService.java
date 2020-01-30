package com.tomaszkyc.app.core.services.translation;

public interface TranslationService {

    /**
     * gives translation for given key
     * @param key key for translation
     * @return translation for actual local
     * @throws NoTranslationFoundException when can't find translation
     */
    String getTranslation(String key) throws NoTranslationFoundException;
}
