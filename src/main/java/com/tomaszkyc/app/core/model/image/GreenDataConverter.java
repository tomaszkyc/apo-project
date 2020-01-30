package com.tomaszkyc.app.core.model.image;


import java.awt.*;
import java.awt.image.BufferedImage;

public class GreenDataConverter implements ImageDataConverter {

    public GreenDataConverter() {

    }

    @Override
    public Integer[][] asTwoDimensionalArray(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();
        Integer[][] array = new Integer[height][width];
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x) {
                int rgb = image.getRGB(x, y);
                int green = (rgb >>8 ) & 0x000000FF;
                array[y][x] = green;
            }

        return array;
    }

    @Override
    public Channel getChannel() {
        return Channel.GREEN;
    }


}
