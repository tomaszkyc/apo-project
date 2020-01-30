package com.tomaszkyc.app.ui.model.contextmenu;

import com.tomaszkyc.app.MainApp;
import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.core.validator.ImageHeightValidator;
import com.tomaszkyc.app.core.validator.ImageNotNullValidator;
import com.tomaszkyc.app.core.validator.ImageWidthValidator;
import com.tomaszkyc.app.ui.exception.UIExceptionHandler;
import com.tomaszkyc.app.ui.model.tab.ImageTab;
import com.tomaszkyc.app.ui.view.root.RootViewController;
import com.tomaszkyc.app.ui.view.window.CompareWindowController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class CompareWithSelectedMenuItemBuilder implements CustomMenuItemBuilder {

    private static final Logger log = LogManager.getLogger(CompareWithSelectedMenuItemBuilder.class.getName());

    private String itemName;
    private ImageTab imageTab;
    private EventHandler<ActionEvent> eventHandler;
    private MenuItem menuItem;

    public CompareWithSelectedMenuItemBuilder(ImageTab imageTab) {
        this.itemName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.compare-with-selected.text");
        this.imageTab = imageTab;
        this.menuItem = new MenuItem(itemName);
        prepareActionEventHandler();
    }

    @Override
    public MenuItem build() {
        log.debug("Starting build menu item: " + itemName);
        menuItem.setOnAction( eventHandler );

        if ( this.imageTab.isSelectedForCompare() ) {
            this.menuItem.setDisable( true );
        }

        log.debug("Finished build menu item: " + itemName);
        return this.menuItem;
    }

    @Override
    public void prepareActionEventHandler() {

        this.eventHandler = actionEvent -> {

            log.debug("Compare with selected clicked in tab: " + this.imageTab.getText());
            handleCreatingCompareWindow();

        };
    }


    private void handleCreatingCompareWindow() {

        try {
            Stage stage = new Stage();
            AnchorPane rootLayout;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/view/window/CompareWindow.fxml"));
            rootLayout = loader.load();
            CompareWindowController compareWindowController = loader.getController();

            //find tab which is selected for compare
            ImageTab selectedForCompare =  getSelectForCompareTab(this.imageTab);

            //add tabs to compare
            compareWindowController.setSelectedForCompareImageTab( selectedForCompare );
            compareWindowController.setCompareWithSelectedImageTab( this.imageTab );

            //validate images for compare
            validateImagesForCompareWindow(selectedForCompare.getBufferedImage(), this.imageTab.getBufferedImage());


            //prepare window
            compareWindowController.prepareWindow();

            stage.setResizable(false);
            //add window title
            String windowTitle = ServiceFactory.getTranslationService().getTranslation("app.windows.compare-window.title");
            stage.setTitle(windowTitle);

            //add icon to window
            addIconToStage(stage);

            Scene scene = new Scene(rootLayout);

            //add scene to controller object
            compareWindowController.setScene(scene);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            UIExceptionHandler.handleException(e);
        }


    }

    /**
     * method validates custom images data to check if they can be compared
     * @param selectedForCompareImage 'selected for compare' image object
     * @param compareWithSelectedImage 'compare with selected' image object
     * @throws Exception when images can't be compared because of validation result error
     */
    private void validateImagesForCompareWindow(BufferedImage selectedForCompareImage, BufferedImage compareWithSelectedImage) throws Exception {

        //first validate if images are not null
        ImageNotNullValidator imageNotNullValidator = new ImageNotNullValidator();
        imageNotNullValidator.validate( selectedForCompareImage, compareWithSelectedImage );

        //check width
        ImageWidthValidator imageWidthValidator = new ImageWidthValidator();
        imageWidthValidator.validate( selectedForCompareImage, compareWithSelectedImage );

        //check height
        ImageHeightValidator imageHeightValidator = new ImageHeightValidator();
        imageHeightValidator.validate( selectedForCompareImage, compareWithSelectedImage );

    }

    /**
     * method find 'selected for compare' image tab from all tabs attached to window
     * @param actualImageTab actual image tab
     * @return 'selected for compare' image tab
     */
    private ImageTab getSelectForCompareTab(ImageTab actualImageTab) {

        Optional<ImageTab> searchedTab = actualImageTab.getTabPane().getTabs()
                                            .stream().map(tab -> (ImageTab)tab)
                                            .filter(tab -> !tab.equals(actualImageTab))
                                            .filter(tab -> tab.isSelectedForCompare()).findAny();
        return searchedTab.get();

    }

    /**
     * method add icon to given stage
     * @param stage stage where icon will be added
     */
    private void addIconToStage(Stage stage) {
        stage.getIcons().add( new Image( MainApp.class.getResource("/images/icon.png").toString() ) );
    }

}
