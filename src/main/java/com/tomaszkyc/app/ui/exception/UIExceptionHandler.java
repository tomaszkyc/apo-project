package com.tomaszkyc.app.ui.exception;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UIExceptionHandler {

    private static final Logger log = LogManager.getLogger(UIExceptionHandler.class);

    private static final TranslationService translationService = ServiceFactory.getTranslationService();

    public static void handleException(Exception exception) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle( translationService.getTranslation("app.exception-window.title") );
        alert.setHeaderText( translationService.getTranslation("app.exception-window.header-text") );

        String contentText = String.format( "%s%s", translationService.getTranslation("app.exception-window.context-text")
                                                 , exception.getMessage() );

        alert.setContentText( contentText );
        alert.showAndWait();

    }

}
