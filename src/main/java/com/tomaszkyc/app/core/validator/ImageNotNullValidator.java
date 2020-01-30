package com.tomaszkyc.app.core.validator;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class ImageNotNullValidator implements ImageValidator {

    private static final TranslationService translationService = ServiceFactory.getTranslationService();
    private static final Logger log = LogManager.getLogger(ImageHeightValidator.class.getName());


    public ImageNotNullValidator() {

    }

    @Override
    public void validate(BufferedImage selectedForCompareImage, BufferedImage compareWithSelectedImage) throws Exception {

        log.debug("Image not null validation started");

        Objects.requireNonNull(selectedForCompareImage, translationService.getTranslation("app.core.validator.image-not-null-validator.select-for-compare-image-null.text"));
        Objects.requireNonNull(compareWithSelectedImage, translationService.getTranslation("app.core.validator.image-not-null-validator.compare-with-selected-image-null.text"));


        log.debug("Image not null validation finished successfully");
    }
}
