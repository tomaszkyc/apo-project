package com.tomaszkyc.app.core.utils;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.ui.exception.UIExceptionHandler;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileUtils {

    private static final Logger log = LogManager.getLogger(FileUtils.class.getName());

    private static final TranslationService translationService = ServiceFactory.getTranslationService();

    /**
     * method show "Save as CSV" dialog for parent layout and given image data array
     * @param layoutRoot parent used for displaying context window
     * @param imageData 2-dimensional image data array
     * @return reference to file object where array was saved (saved as ';' - semicolon - delimited)
     */
    public static File saveAsCSVDialog(Parent layoutRoot, Integer[][] imageData) {

        log.debug("Starting export image data to file...");
        Window window = layoutRoot.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(LocalDate.now().toString() + " exported-image.csv");

        //set filter for CSV files
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV", "csv"));

        //show window
        File file = fileChooser.showSaveDialog(window);
        if ( file != null ) {
            //open buffer writer
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

                int height = imageData.length;
                int width = imageData[0].length;

                for(int y = 0; y < height; y++) {

                    StringBuilder sb = new StringBuilder();

                    for(int x = 0; x < width; x++) {

                        sb.append( imageData[y][x] );
                        //after each number put ; to make it semicolon separated file
                        sb.append(';');

                    }
                    //after last cell of actual row -> go to next row
                    sb.append('\n');

                    //write to buffer after each line
                    bw.write( sb.toString() );
                }


            }
            catch(Exception e) {
                String errorMessage = String.format( translationService.getTranslation("app.utils.saving-file.exception-message"), e.getMessage() );
                UIExceptionHandler.handleException(new Exception(errorMessage));
            }

        }
        else {
            log.debug("No file selected - return null");
        }

        return file;
    }

    /**
     * method show "Save as CSV" dialog for parent layout and given image data array
     * @param layoutRoot parent used for displaying context window
     * @param image image to save in file
     * @return reference to file object where array was saved (saved as ';' - semicolon - delimited)
     */
    public static File saveAsCSVDialog(Parent layoutRoot, BufferedImage image) {

        log.debug("Exported image: width: " + image.getWidth() + " height: " + image.getHeight());

        Window window = layoutRoot.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(LocalDate.now().toString() + " exported-image.csv");

        //set filter for CSV files
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV", "csv"));

        //show window
        File file = fileChooser.showSaveDialog(window);
        if(file != null) {
            BufferedWriter bw = null;
            try {
                //create buffer to save items to file
                bw = new BufferedWriter(new FileWriter(file));
                byte[] a = ImageUtils.getImageData(image);
                int width = image.getWidth();
                for (int i = 0; i < a.length; ++i) {
                    int value = a[i] & 0xFF;
                    //save single pixel value
                    bw.write(String.valueOf(value));
                    //if last pixel in row --> go to next line
                    if (i % width == width-1) {
                        bw.write('\n');
                    } else bw.write(";");
                }
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
                exception.printStackTrace();
            } finally {
                if (bw != null) try {
                    //close buffer
                    bw.close();
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * method loads image from CSV file
     * @param file reference to CSV file
     * @return BufferedImage class object
     * @throws Exception in case of problems with image loading
     */
    public static BufferedImage loadImageFromCSV(File file) throws Exception {

        BufferedImage bufferedImage = null;

        //load CSV file data to memory
        List<String> linesFromCsvFile = CSVUtils.parseCSV(file);
        //calculate width and height

        int width = getWidthFromCSVLines(linesFromCsvFile);
        int height = getHeightFromCSVLines(linesFromCsvFile);

        //get values form CSV lines
        Integer[][] pixels = asTwoDimensionalArray(linesFromCsvFile);

        //generate image
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int y=0; y<height; ++y) {
            for (int x=0; x<width; ++x) {
                //int rgb = pixels[y][x] | (pixels[y][x] << 8) | (pixels[y][x] << 16);
                Color gray = new Color(pixels[y][x], pixels[y][x], pixels[y][x]);
                bufferedImage.setRGB(x, y, gray.getRGB() );
            }
        }


        return  bufferedImage;
    }


    /**
     * method give information about height of image inside CSV file
     * Create for not repeating code in this class
     * @param csvLines collection of lines
     * @return actual height or 0 in case of no found lines in collection
     */
    private static int getHeightFromCSVLines(List<String> csvLines) {

        if (csvLines == null || csvLines.isEmpty()) return 0;

        return csvLines.size();
    }

    /**
     * method give width of image inside CSV file
     * Create for not repeating code in this class
     * @param csvLines collection of lines
     * @return actual width or 0 in case of no found lines in collection
     */
    private static int getWidthFromCSVLines(List<String> csvLines) {

        if (csvLines == null || csvLines.isEmpty()) return 0;

        //craete stream to count number of delimiters in all lines
        return csvLines.stream().map(line ->
        {
            String[] values = line.split(";", -1);

            return values.length;
        }).mapToInt(v -> v).max().orElse(0);
    }

    /**
     * method converts CSV values to 2 dimentional Integer array (array of pixels values)
     * @param csvLines lines from csv file
     * @return Array of pixel values
     */
    private static Integer[][] asTwoDimensionalArray(List<String> csvLines) {

        //in case of null input
        if ( csvLines == null || csvLines.isEmpty() ) return null;

        //get details of image
        int width = getWidthFromCSVLines(csvLines);
        int height = getHeightFromCSVLines(csvLines);

        //crate collection to handle info
        Integer[][] array = new Integer[height][width];

        for(int y = 0; y < height; y++) {

            //split values from line by delimiter
            String[] valuesFromLine = csvLines.get(y).split(";", -1);

            for(int x = 0; x < width; x++) {

                //if empty string --> set pixel value 0
                if (StringUtils.isEmpty(valuesFromLine[x])) {
                    array[y][x] = 0;
                }
                else {
                    array[y][x] = Integer.valueOf( valuesFromLine[x] );
                }


            }

        }
        return array;

    }

    /**
     * method loads image from given File object
     * @param file image file
     * @return BufferedImage object with image data
     */
    public static BufferedImage loadImage(File file) {

        try {

            //create file input stream to read file
            FileInputStream fileIs = new FileInputStream(file);

            //create BufferedImage object with ImageIO lib
            BufferedImage image = ImageIO.read(fileIs);

            log.debug("Loaded image type: " + image.getType());
            if (image.getType() != BufferedImage.TYPE_3BYTE_BGR && image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
                log.debug("Image type not: " + BufferedImage.TYPE_3BYTE_BGR);
                log.debug("Image type not: " + BufferedImage.TYPE_BYTE_GRAY);

                log.debug("Image type: " + image.getType() + ", Converting image to 3BYTE_BGR");
                image = ImageUtils.convertTo3Byte(image);
            }
            return image;

        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }

        return null;
    }

    /**
     * give file extension
     * @param file file object
     * @return optional object with extension value (if exists)
     */
    public static Optional<String> getFileExtension(File file) {
        if (file == null) {
            return Optional.of("");
        }
        String filename = file.getName();
        //find extension as string after last comma
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}
