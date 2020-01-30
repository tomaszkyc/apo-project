package com.tomaszkyc.app.core.validator;

import java.awt.image.BufferedImage;

public interface ImageValidator {

    /**
     * method invoked for custom compare of two images
     * @param selectedForCompareImage selected for compare image
     * @param compareWithSelectedImage compare with selected image
     * @throws Exception in validation failure with message as error cause
     */
    void validate(BufferedImage selectedForCompareImage, BufferedImage compareWithSelectedImage) throws Exception;
}
