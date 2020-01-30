package com.tomaszkyc.app.ui.view.root;

import com.tomaszkyc.app.MainApp;
import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.core.utils.FileUtils;
import com.tomaszkyc.app.ui.exception.UIExceptionHandler;
import com.tomaszkyc.app.ui.model.tab.ImageTab;
import com.tomaszkyc.app.ui.view.window.ChangeLanguageWindowController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;

public class RootViewController implements ViewInitialization {

    private ObservableList<Tab> imageTabs;

    private static final Logger log = LogManager.getLogger(RootViewController.class.getName());

    private final TranslationService translationService;

    public MainApp getMainApp() {
        return mainApp;
    }

    private MainApp mainApp;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem changeLanguageMenuItem;

    @FXML
    private Menu helpMenu;



    @FXML
    private Menu fileMenu;

    public RootViewController() {
        this.translationService = ServiceFactory.getTranslationService();
    }

    /**
     * method add icon to window
     * @param stage
     */
    private void addIcon(Stage stage) {

        stage.getIcons().add(
                new Image( MainApp.class.getResource("/images/icon.png").toString() ));
    }


    /**
     * method handle clicking 'Change language' option
     */
    @FXML
    private void handleChangeLanguage() {

        log.debug("Change language clicked");
        try{
            Stage stage = new Stage();
            AnchorPane rootLayout;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/view/window/ChangeLanguageWindow.fxml"));
            rootLayout = loader.load();
            ChangeLanguageWindowController changeLanguageWindowController = loader.getController();
            changeLanguageWindowController.setMainApp(this.mainApp);
            stage.setResizable(false);

            //add title
            String windowTitle = translationService.getTranslation("app.menubar.help.change-language");
            stage.setTitle(windowTitle);

            //add icon to window
            addIconToStage(stage);
            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
            stage.show();

        }
        catch(Exception exception) {
            log.error(exception.getMessage(), exception);
        }




    }

    /**
     * method add icon to given stage
     * @param stage stage where icon will be added
     */
    private void addIconToStage(Stage stage) {
        stage.getIcons().add( new Image( MainApp.class.getResource("/images/icon.png").toString() ) );
    }

    @FXML
    /**
     * method handle clicking 'Exit' options
     */
    private void handleExit() {

        log.debug("Exit clicked");
        System.exit(0);

    }

    @FXML
    /**
     * method handle clicking 'About' options
     */
    private void handleAbout() {
        log.debug("About clicked");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);


        // Add icon to alert
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        addIcon( stage );
        alert.setTitle( translationService.getTranslation("app.title") );
        alert.setHeaderText( translationService.getTranslation("app.menubar.help.about.header") );

        //modification
        FlowPane flowPane = new FlowPane();
        Label lbl = new Label( translationService.getTranslation("app.menubar.help.about.content-text") );
        Hyperlink link = new Hyperlink( translationService.getTranslation("app.menubar.help.about.author-link") );
        link.setOnAction( e -> getMainApp().openUrl( link.getText() ) );


        flowPane.getChildren().addAll( lbl, link);
        flowPane.setPadding( new Insets(10, 10, 10, 10) );
        alert.getDialogPane().contentProperty().set(flowPane);


        //show message windows
        alert.showAndWait();
    }

    @FXML
    /**
     * method handle clicking 'Open image' options
     */
    private void handleOpenImage() {

        File file = null;

        try {

            FileChooser fileChooser = new FileChooser();
            // Set extension filter
            FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter(
                    "JPG (*.jpg)", "*.jpg");
            FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter(
                    "PNG (*.png)", "*.png");
            FileChooser.ExtensionFilter bmpFilter = new FileChooser.ExtensionFilter(
                    "BMP (*.bmp)", "*.bmp");
            FileChooser.ExtensionFilter tifFilter = new FileChooser.ExtensionFilter(
                    "TIF (*.tif)", "*.tif");
            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter(
                    "CSV (*.csv)", "*.csv");

            fileChooser.getExtensionFilters().addAll( jpgFilter, pngFilter, bmpFilter,
                    tifFilter, csvFilter);


            // Show open file dialog
            file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
            if ( file != null && file.exists() ) {

                String fileExtension = FileUtils.getFileExtension(file).orElse("");
                log.debug("Loaded file extension: " + fileExtension);


                //generate tab for CSV file
                if ( fileExtension.equalsIgnoreCase("csv") ) {

                    //Creating an image
                    log.debug("Choosen filepath: " + file.getAbsolutePath());
                    BufferedImage loadedImage = FileUtils.loadImageFromCSV(file);
                    //create tab with image
                    ImageTab imageTab = new ImageTab(loadedImage, file.getName());

                    //add tab to tab pane
                    imageTabs.add( imageTab );

                    //render image
                    imageTab.renderImageView();
                    imageTab.checkIfShouldAddSelectedForCompareOption();
                    tabPane.setVisible(true);

                }
                //generate tab for image file
                else {
                    //Creating an image
                    log.debug("Choosen filepath: " + file.getAbsolutePath());
                    BufferedImage loadedImage = FileUtils.loadImage(file);
                    //create tab with image
                    ImageTab imageTab = new ImageTab(loadedImage, file.getName());

                    //add tab to tab pane
                    imageTabs.add( imageTab );

                    //render image
                    imageTab.renderImageView();
                    imageTab.checkIfShouldAddSelectedForCompareOption();
                    tabPane.setVisible(true);

                }



            }
            else{
                log.debug("No file choosen");
            }
        }
        catch(Exception exception) {
            String messageFormat = translationService.getTranslation("app.menubar.open-image.exception");
            String exceptionMessage = String.format(messageFormat, file.getAbsolutePath(), exception.getMessage());
            UIExceptionHandler.handleException(new Exception(exceptionMessage));
        }


    }

    @FXML
    /**
     * method runs every time the window is initializing
     */
    private void initialize() {

        this.imageTabs = tabPane.getTabs();

        this.tabPane.setVisible(false);

        //add shortcuts to menu items
        addShortCutsToMenuItems();

        //add icons
        addIcons();

        //add translations
        addTranslations();

    }

    /**
     * method add shortcuts to menu items
     */
    private void addShortCutsToMenuItems() {

        //for open file menu item
        this.openFileMenuItem.setAccelerator( new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN) );

        //for close application menu item
        this.exitMenuItem.setAccelerator( new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN) );

    }


    @Override
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * method adds icons to selected application items
     */
    private void addIcons() {

        addIconToMenu(this.helpMenu, MainApp.class.getResource("/images/help-icon.png").toString());
        addIconToMenu(this.fileMenu, MainApp.class.getResource("/images/file-icon.png").toString());

    }

    /**
     * method adds icon to given menu
     * @param menu menu object
     * @param iconPath icon filepath
     */
    private void addIconToMenu(Menu menu, String iconPath) {

        Image icon = new Image(iconPath,20, 20, false, false);
        menu.setGraphic(new ImageView(icon));


    }

    /**
     * method translate static labels inside application
     */
    private void addTranslations() {

        this.helpMenu.setText( translationService.getTranslation("app.menubar.help") );
        this.fileMenu.setText( translationService.getTranslation("app.menubar.file") );
        this.openFileMenuItem.setText( translationService.getTranslation("app.menubar.file.openfile") );
        this.exitMenuItem.setText( translationService.getTranslation("app.menubar.file.close") );
        this.aboutMenuItem.setText( translationService.getTranslation("app.menubar.help.about") );
        this.changeLanguageMenuItem.setText( translationService.getTranslation("app.menubar.help.change-language") );
    }

}
