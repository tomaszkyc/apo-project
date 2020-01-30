package com.tomaszkyc.app.core.model.image;


import java.awt.image.BufferedImage;

public class BlueDataConverter implements ImageDataConverter {

    public BlueDataConverter() {}


    @Override
    public Integer[][] asTwoDimensionalArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Integer[][] array = new Integer[height][width];
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x) {
                int rgb = image.getRGB(x, y);
                int blue = (rgb) & 0x000000FF;
                array[y][x] = blue;
            }
        return array;
    }


    @Override
    public Channel getChannel() {
        return Channel.BLUE;
    }

}
