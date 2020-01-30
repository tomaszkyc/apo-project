package com.tomaszkyc.app.core.validator;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;

public class ImageWidthValidator implements ImageValidator {

    private static final TranslationService translationService = ServiceFactory.getTranslationService();
    private static final Logger log = LogManager.getLogger(ImageWidthValidator.class.getName());


    public ImageWidthValidator() {

    }

    @Override
    public void validate(BufferedImage selectedForCompareImage, BufferedImage compareWithSelectedImage) throws Exception {

        log.debug("Image width validation started");

        int widthOfSelectedImage = selectedForCompareImage.getWidth();
        int widthOfCompareWithSelectedImage = compareWithSelectedImage.getWidth();

        //if width of two image is not equal
        if ( widthOfSelectedImage != widthOfCompareWithSelectedImage ) {

            String messageFormat = translationService.getTranslation("app.core.validator.image-width-validator.exception.text");
            String exceptionMessage = String.format(messageFormat,
                    String.valueOf(widthOfSelectedImage),
                    String.valueOf(widthOfCompareWithSelectedImage));

            log.error(exceptionMessage);
            throw new Exception( exceptionMessage );

        }

        log.debug("Image width validation finished successfully");

    }
}
