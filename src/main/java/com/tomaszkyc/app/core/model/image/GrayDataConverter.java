package com.tomaszkyc.app.core.model.image;


import com.tomaszkyc.app.core.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class GrayDataConverter implements ImageDataConverter {

    public GrayDataConverter() {

    }


    @Override
    public Integer[][] asTwoDimensionalArray(BufferedImage image) {
        if (image.getColorModel().getNumComponents() > 1) image = ImageUtils.rgbToGrayscale(image);
        int width = image.getWidth();
        int height = image.getHeight();
        Integer[][] array = new Integer[height][width];
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                array[y][x] = ImageUtils.getPixel(image, x, y)[0];
        return array;
    }

    @Override
    public Channel getChannel() {
        return Channel.GRAY;
    }
}
