package com.tomaszkyc.app.core.utils;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    private static final Logger log = LogManager.getLogger(CSVUtils.class.getName());

    private static final TranslationService translationService = ServiceFactory.getTranslationService();

    /**
     * method helps with getting text lines from given File object
     * @param file file object
     * @return list of lines inside file
     * @throws Exception if there was an error during reading file
     *                  (access error, file deleted etc.)
     */
    public static List<String> parseCSV(File file) throws Exception {

        //create a collection for lines
        List<String> csvLines = new ArrayList<>();

        //open buffer to read file
        try (BufferedReader csvReader = new BufferedReader(new FileReader(file))){


            String row = null;
            //read until no lines in file
            while( (row = csvReader.readLine()) != null ) {
                csvLines.add(row);
            }
        }
        //in case of exception
        catch (Exception parsingCsvException) {
            log.error(parsingCsvException.getMessage(), parsingCsvException);
            String exceptionMessageFormat = translationService.getTranslation("app.utils.csv-utils.parse-csv.exception-message");
            String exceptionMessage = String.format( exceptionMessageFormat, file.getName() );
            throw new Exception( exceptionMessage );
        }

        //return readed lines
        return csvLines;

    }

}
