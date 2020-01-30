package com.tomaszkyc.app.core.services.translation;

import com.tomaszkyc.app.core.exception.BaseException;

public class NoTranslationFoundException extends BaseException {


    public NoTranslationFoundException(String translationKey) {
        super( String.format("No translation found for key: %s", translationKey) );
    }
}
