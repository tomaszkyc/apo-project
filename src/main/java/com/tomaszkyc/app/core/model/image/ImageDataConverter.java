package com.tomaszkyc.app.core.model.image;

import java.awt.image.BufferedImage;

public interface ImageDataConverter {

    /**
     * converts BufferedImage to two dimentional integer array. Helpful when we need to show
     * each pixel values in table (example: TableView class)
     * @param image input image which should be converted to arrays
     * @return array of pixel values for image
     */
    public Integer[][] asTwoDimensionalArray(BufferedImage image);

    /**
     * get actual channel of image converted instance. Helpful when we want to log what
     * ImageDataConvert we use
     * @return
     */
    Channel getChannel();

}
