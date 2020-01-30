package com.tomaszkyc.app;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.ui.exception.UIExceptionHandler;
import com.tomaszkyc.app.ui.view.root.RootViewController;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.concurrent.Service;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

public class MainApp extends Application {

    private static TranslationService translationService;

    private static final Logger log = LogManager.getLogger(MainApp.class);


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public AnchorPane getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(AnchorPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    private Stage primaryStage;
    private AnchorPane rootLayout;

    private HostServices hostServices;

    /**
     * sets new language shortcut
     * @param languageShortcut new language shortcut to set
     */
    public void setApplicationLanguage(String languageShortcut) {
        ServiceFactory.getPropertiesCacheService().setProperty("actualLanguageCode", languageShortcut);
    }


    @Override
    public void start(Stage primaryStage) {
        translationService = ServiceFactory.getTranslationService();
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle( translationService.getTranslation("app.title") );
        this.hostServices = getHostServices();
        initRootLayout();
    }

    /**
     * method opens given URL with hostServices object
     * @param url url to be opened in default web browsers
     */
    public void openUrl(String url) {
        this.hostServices.showDocument( url );
    }

    /**
     * method is responsible for creating main window and controller for application
     */
    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/view/root/RootView.fxml"));
            rootLayout = loader.load();
            RootViewController rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);
            primaryStage.setResizable(false);
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            handleMacDockIconAdding();
            addIconToStage( primaryStage );
            primaryStage.show();
        } catch (IOException e) {
            UIExceptionHandler.handleException(e);
        }
    }


    public static void main(String[] args)  {
        try{
            log.debug("Actual encoding: " + System.getProperty("file.encoding", ""));
        }
        catch(Exception exception) {
            log.debug("There was an error on getting encoding info");
        }

        launch(args);
    }

    /**
     * method add icon to given stage
     * @param stage stage where icon will be added
     */
    private void addIconToStage(Stage stage) {
        stage.getIcons().add( new Image( MainApp.class.getResource("/images/icon.png").toString() ) );
    }

    /**
     * method is responsible for adding icon to application for macOS
     */
    private void handleMacDockIconAdding() {

        if (SystemUtils.IS_OS_MAC) {
            try
            {
                Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
                URL url = MainApp.class.getResource("/images/icon.png");
                java.awt.Image image = Toolkit.getDefaultToolkit().getImage(url);
                Method getApplicationMethod = applicationClass.getMethod("getApplication");
                Method setDockIconMethod = applicationClass.getMethod("setDockIconImage", java.awt.Image.class);
                Object macOSXApplication = getApplicationMethod.invoke(null);
                setDockIconMethod.invoke(macOSXApplication, image);
            }
            catch(Exception exception)
            {
                log.error("There was an error on adding macOS icon", exception);
            }
        }


    }

}