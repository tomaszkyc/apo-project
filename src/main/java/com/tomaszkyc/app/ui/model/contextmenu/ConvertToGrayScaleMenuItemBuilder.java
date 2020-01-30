package com.tomaszkyc.app.ui.model.contextmenu;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.core.utils.FileUtils;
import com.tomaszkyc.app.core.utils.ImageUtils;
import com.tomaszkyc.app.ui.model.tab.ImageTab;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;

public class ConvertToGrayScaleMenuItemBuilder implements CustomMenuItemBuilder {
    private static final Logger log = LogManager.getLogger(ExportToExcelMenuItemBuilder.class.getName());

    private String itemName;
    private ImageTab imageTab;
    private EventHandler<ActionEvent> eventHandler;
    private MenuItem menuItem;

    public ConvertToGrayScaleMenuItemBuilder(ImageTab imageTab) {
        this.itemName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.convert-to-grayscale.text");
        this.imageTab = imageTab;
        prepareActionEventHandler();
    }

    @Override
    public MenuItem build() {
        log.debug("Starting build menu item: " + itemName);
        menuItem = new MenuItem(itemName);
        menuItem.setOnAction( eventHandler );
        enableDisableConvertToGrayscaleMenuItem();
        log.debug("Finished build menu item: " + itemName);
        return menuItem;
    }

    @Override
    public void prepareActionEventHandler() {

        this.eventHandler = actionEvent -> {
            log.debug("Convert to grayscale clicked");

            //convert
            BufferedImage convertedToGrayScaleImage = ImageUtils.rgbToGrayscale( this.imageTab.getBufferedImage() );
            this.imageTab.getImageView().setImage(SwingFXUtils.toFXImage( convertedToGrayScaleImage, null ));
            this.imageTab.setBufferedImage(convertedToGrayScaleImage);
            //disable button
            this.menuItem.setDisable(true);

        };
    }

    /**
     * method enable or disable 'Convert to grayscale' menu item
     * depends from number of channels in bufferedImage
     */
    private void enableDisableConvertToGrayscaleMenuItem() {

        int numberOfChannels = this.imageTab.getNumberOfChannels();

        //if only one channel - disable convert to grayscale
        if ( numberOfChannels == 1 ) {
            this.menuItem.setDisable(true);
        }
        else {
            this.menuItem.setDisable(false);
        }


    }
}
