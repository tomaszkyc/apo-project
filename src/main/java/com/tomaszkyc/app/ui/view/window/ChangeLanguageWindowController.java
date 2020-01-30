package com.tomaszkyc.app.ui.view.window;

import com.tomaszkyc.app.MainApp;
import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChangeLanguageWindowController {

    private static final Logger log = LogManager.getLogger(ChangeLanguageWindowController.class.getName());
    private TranslationService translationService;

    @FXML
    private Text changeLanguageText;

    @FXML
    private RadioButton polishRadioButton;

    @FXML
    private RadioButton englishRadioButton;

    @FXML
    private AnchorPane anchorPane;

    private ToggleGroup toggleGroup;

    @FXML
    private Button applyButton;

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    private MainApp mainApp;

    public ChangeLanguageWindowController() {
        this.translationService = ServiceFactory.getTranslationService();
    }

    @FXML
    /**
     * method runs every time the window is initializing
     */
    private void initialize() {

        //add translations
        addTranslations();


        //toggle buttons
        toggleRadioButtons();

    }

    /**
     * method toggles two radio buttons
     */
    private void toggleRadioButtons() {

        this.toggleGroup = new ToggleGroup();
        this.polishRadioButton.setToggleGroup(toggleGroup);
        this.englishRadioButton.setToggleGroup(toggleGroup);


    }

    /**
     * method handle language change
     */
    @FXML
    private void handleChangeLanguage() {

        String selectedLanguage = null;

        if ( this.polishRadioButton.isSelected() ) {
            selectedLanguage = "pl";
        }
        else if ( this.englishRadioButton.isSelected() ) {
            selectedLanguage = "en";
        }

        log.debug("Selected language: " + selectedLanguage);
        String actualAppLanguage = ServiceFactory.getPropertiesCacheService().getProperty("actualLanguageCode");
        if ( !selectedLanguage.equals(actualAppLanguage) ) {

            //close window
            Stage changeLanguageWindow = (Stage)this.applyButton.getScene().getWindow();
            changeLanguageWindow.close();

            log.debug("Language changed from: " + actualAppLanguage + " to: " + selectedLanguage);

            //close main windows
            this.mainApp.getPrimaryStage().close();
            String finalSelectedLanguage = selectedLanguage;
            Platform.runLater(() -> {
                MainApp mainApp = new MainApp();
                mainApp.setApplicationLanguage(finalSelectedLanguage);
                mainApp.start(new Stage());
            });

        }
        else {
            //close window otherwise
            Stage changeLanguageWindow = (Stage)this.applyButton.getScene().getWindow();
            changeLanguageWindow.close();
        }
    }

    /**
     * adds translation to static elements of window
     */
    private void addTranslations() {

        this.changeLanguageText.setText( translationService.getTranslation("app.menubar.help.change-language") );
        this.polishRadioButton.setText( translationService.getTranslation("app.menubar.help.change-language.polish") );
        this.englishRadioButton.setText( translationService.getTranslation("app.menubar.help.change-language.english") );
        this.applyButton.setText( translationService.getTranslation("app.menubar.help.change-language.apply-changes") );

    }

}
