package com.tomaszkyc.app.core.validator;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;

public class ImageHeightValidator implements ImageValidator {

    private static final TranslationService translationService = ServiceFactory.getTranslationService();
    private static final Logger log = LogManager.getLogger(ImageHeightValidator.class.getName());


    public ImageHeightValidator() {

    }

    @Override
    public void validate(BufferedImage selectedForCompareImage, BufferedImage compareWithSelectedImage) throws Exception {

        log.debug("Image height validation started");

        int heightOfSelectedImage = selectedForCompareImage.getHeight();
        int heightOfCompareWithSelectedImage = compareWithSelectedImage.getHeight();

        //if height of two image is not equal
        if ( heightOfSelectedImage != heightOfCompareWithSelectedImage ) {

            String messageFormat = translationService.getTranslation("app.core.validator.image-height-validator.exception.text");
            String exceptionMessage = String.format(messageFormat,
                    String.valueOf(heightOfSelectedImage),
                    String.valueOf(heightOfCompareWithSelectedImage));

            log.error(exceptionMessage);
            throw new Exception( exceptionMessage );

        }

        log.debug("Image height validation finished successfully");
    }
}
