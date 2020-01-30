package com.tomaszkyc.app.ui.view.window;

import com.tomaszkyc.app.MainApp;
import com.tomaszkyc.app.core.model.image.BlueDataConverter;
import com.tomaszkyc.app.core.model.image.GrayDataConverter;
import com.tomaszkyc.app.core.model.image.GreenDataConverter;
import com.tomaszkyc.app.core.model.image.RedDataConverter;
import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.ui.model.tab.ImageTab;
import com.tomaszkyc.app.ui.model.tab.NumbersTab;
import com.tomaszkyc.app.ui.model.window.TableWindow;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class CompareWindowController {

    private static final Logger log = LogManager.getLogger(CompareWindowController.class.getName());

    public static final String COMPARE_WINDOW_TAB_TEXT_FORMAT = "%s - %s";

    private final TranslationService translationService;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private Scene scene;

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu actionsMenu;

    @FXML
    private MenuItem closeWindowMenuItems;

    @FXML
    private MenuItem comparePixelValuesMenuItem;

    @FXML
    private TabPane selectedForCompareImageTabPane;

    @FXML
    private TabPane compareWithSelectedImageTabPane;

    private MainApp mainApp;

    public ImageTab getSelectedForCompareImageTab() {
        return selectedForCompareImageTab;
    }

    public void setSelectedForCompareImageTab(ImageTab selectedForCompareImageTab) {
        this.selectedForCompareImageTab = selectedForCompareImageTab;
    }

    public ImageTab getCompareWithSelectedImageTab() {
        return compareWithSelectedImageTab;
    }

    public void setCompareWithSelectedImageTab(ImageTab compareWithSelectedImageTab) {
        this.compareWithSelectedImageTab = compareWithSelectedImageTab;
    }

    private ImageTab selectedForCompareImageTab;

    private ImageTab compareWithSelectedImageTab;

    public CompareWindowController() {

        this.translationService = ServiceFactory.getTranslationService();

    }

    @FXML
    /**
     * method runs every time the window is initializing
     */
    private void initialize() {

        //add icons to window
        addIcons();

        //add translations
        addTranslations();
    }

    /**
     * method responsible for changing static translations
     */
    private void addTranslations() {

        this.fileMenu.setText(translationService.getTranslation("app.menubar.file"));
        this.actionsMenu.setText( translationService.getTranslation("app.menubar.actions") );
        this.closeWindowMenuItems.setText( translationService.getTranslation("app.menubar.file.close-window") );
        this.comparePixelValuesMenuItem.setText( translationService.getTranslation("app.menubar.actions.compare-images-by-pixel-values") );

    }

    /**
     * method prepare items inside window (diagrams, data, etc)
     * @throws Exception in case of error in fetching, creating data etc.
     */
    public void prepareWindow() throws Exception {

        prepareTabPaneFromImageTabObject(selectedForCompareImageTabPane, selectedForCompareImageTab);
        prepareTabPaneFromImageTabObject(compareWithSelectedImageTabPane, compareWithSelectedImageTab);


    }

    /**
     * method prepare tab with 2 dimensional array of pixel values and attach to given tabpane
     * @param compareTabPane tabpane which will be parent of new created Numbers tab
     * @param imageTab imagetab which we will use to extract informations about image
     * @throws Exception in case of error on building NumbersTab objects
     */
    private void prepareTabPaneFromImageTabObject( TabPane compareTabPane, ImageTab imageTab ) throws Exception {

        log.debug("Started preparing tabs for 'selected for compare' area for file: " + imageTab.getText());

        Integer[][] imageData = new GrayDataConverter()
                    .asTwoDimensionalArray( imageTab.getBufferedImage() );

        String grayChannelTabText = translationService.getTranslation("app.tabpane.numberstab.tabs.graytab.text");
        NumbersTab grayTab = new NumbersTab(imageData, String.format( COMPARE_WINDOW_TAB_TEXT_FORMAT,
                    imageTab.getText(),
                    grayChannelTabText ),
                    imageTab.getBufferedImage());
        grayTab.setFileName( imageTab.getText() );
        compareTabPane.getTabs().add( grayTab );


        log.debug("Finished preparing tabs for 'selected for compare' area");
    }



    @FXML
    /**
     * method handles closing comare window action
     */
    private void handleClose() {

        log.debug("'Close' menu item clicked");
        Stage stage = (Stage)this.scene.getWindow();
        stage.close();


    }

    /**
     * method handle comparing two images action.
     * @throws Exception in case of error to prepare comparison
     */
    @FXML
    private void handleComparingTwoImages() throws Exception {

        log.debug("'Compare two images' clicked");

        NumbersTab markedSelectedForCompareTab = (NumbersTab) selectedForCompareImageTabPane.getSelectionModel().getSelectedItem();
        NumbersTab markedCompareWithSelectedTab = (NumbersTab) compareWithSelectedImageTabPane.getSelectionModel().getSelectedItem();

        //calculate diffs between images
        Integer[][] dataAfterCompare = prepareDataFromImageCompare(markedSelectedForCompareTab, markedCompareWithSelectedTab);

        //create window with diff
        String tableTitle = translationService.getTranslation("app.tabpane.numberstab.tabs.compare-values-of-pixels-window.text");
        TableWindow tableWindow = new TableWindow(dataAfterCompare, tableTitle);



    }

    /**
     * method prepare data from image compare. Subtract the values of 'selected for compare'
     * numbers tab pixels from 'compare with selected' numbers tab pixels
     * @param selectedForCompareNumbersTab 'selected for compare' numbers tab
     * @param compareWithSelectedNumbersTab 'compare with selected' numbers tab
     * @return integer 2dimensional array of subtracted values
     * @throws Exception in case :
     *                      1) input object are null
     *                      2) there was an error on calculating data
     */
    private Integer[][] prepareDataFromImageCompare(NumbersTab selectedForCompareNumbersTab,
                                             NumbersTab compareWithSelectedNumbersTab) throws Exception {

        //check if objects aren't nullable
        Objects.requireNonNull(selectedForCompareNumbersTab, "Selected for compare numbers table is null");
        Objects.requireNonNull(compareWithSelectedNumbersTab, "Compare with selected numbers table is null");

        Integer[][] dataAfterCompare = null;

        //get width and heights
        int height = selectedForCompareNumbersTab.getImageData().length;
        int width = selectedForCompareNumbersTab.getImageData()[0].length;

        //create collection to handle data
        if ( dataAfterCompare == null ) {
            dataAfterCompare = new Integer[height][width];
        }

        //substract each pixel of 'selected for compare' image from 'compare with selected' image
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x) {

                dataAfterCompare[y][x] = selectedForCompareNumbersTab.getImageData()[y][x]
                                            - compareWithSelectedNumbersTab.getImageData()[y][x];
            }

        //return data
        return dataAfterCompare;
    }


    /**
     * method adds icons to selected application items
     */
    private void addIcons() {

        addIconToMenu(this.actionsMenu, MainApp.class.getResource("/images/action-icon.png").toString());
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




}
