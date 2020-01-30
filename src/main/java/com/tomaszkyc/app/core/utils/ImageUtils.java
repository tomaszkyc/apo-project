package com.tomaszkyc.app.core.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageUtils {

    private static final double rPerc = 0.2;
    private static final double gPerc = 0.6;
    private static final double bPerc = 0.2;

    public static BufferedImage convertTo3Byte(BufferedImage image) {
        BufferedImage ret = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        int rgb;
        for (int x = 0; x < ret.getWidth(); ++x)
            for (int y = 0; y < ret.getHeight(); ++y) {
                rgb = image.getRGB(x, y);
                ret.setRGB(x, y, rgb);
            }
        return ret;
    }

    public static BufferedImage rgbToGrayscale(BufferedImage bufferedImage){
        int channels = bufferedImage.getColorModel().getNumComponents();
        if (channels==1) return bufferedImage;

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        final byte[] a = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        final byte[] gray = ((DataBufferByte) grayscaleImage.getRaster().getDataBuffer()).getData();
        for (int p = 0; p < width * height * channels; p += channels) {
            double r = a[p+2] & 0xFF;
            double g = a[p+1] & 0xFF;
            double b = a[p] & 0xFF;
            gray[p/channels] = (byte) Math.round((r * rPerc) + (g * gPerc) + (b * bPerc));

        }
        return grayscaleImage;
    }


    public static byte[] getImageData(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    public static Integer[][] asTwoDimensionalArray(BufferedImage image) {
        if (image.getColorModel().getNumComponents() > 1) image = rgbToGrayscale(image);
        int width = image.getWidth();
        int height = image.getHeight();
        Integer[][] array = new Integer[height][width];
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                array[y][x] = getPixel(image, x, y)[0];
        return array;
    }

    public static int xyToI(int x, int y, int width, int channels) {
        return y * width * channels + (x * channels);
    }

    public static int getImageChannels(BufferedImage image) {
        return image.getColorModel().getNumComponents();
    }

    public static int[] getPixel(BufferedImage image, int x, int y) {
        byte[] data = getImageData(image);
        int channels = getImageChannels(image);
        int i = xyToI(x, y, image.getWidth(), channels);
        int[] ret = new int[channels];
        for (int c = 0; c < channels; ++c) ret[c] = data[i + c] & 0xFF;
        return ret;
    }


}
