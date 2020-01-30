package com.tomaszkyc.app.ui.model.contextmenu;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.core.utils.FileUtils;
import com.tomaszkyc.app.core.utils.ImageUtils;
import com.tomaszkyc.app.ui.model.tab.ImageTab;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;

public class ExportToExcelMenuItemBuilder implements CustomMenuItemBuilder {

    private static final Logger log = LogManager.getLogger(ExportToExcelMenuItemBuilder.class.getName());

    private String itemName;
    private ImageTab imageTab;
    private EventHandler<ActionEvent> eventHandler;

    public ExportToExcelMenuItemBuilder(ImageTab imageTab) {
        this.itemName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.export-to-csv.text");
        this.imageTab = imageTab;
        prepareActionEventHandler();
    }

    @Override
    public MenuItem build() {
        log.debug("Starting build menu item: " + itemName);
        MenuItem menuItem = new MenuItem(itemName);
        menuItem.setOnAction( eventHandler );
        log.debug("Finished build menu item: " + itemName);
        return menuItem;
    }

    @Override
    public void prepareActionEventHandler() {

        this.eventHandler = actionEvent -> {
            log.debug("Export to excel clicked");

            //generate window title
            String windowTitle = String.format("%s - %s", this.imageTab.getImageName(),
                    ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.export-to-csv.window.title"));

            //check if iamge isn't grayscale
            if (this.imageTab.getBufferedImage().getColorModel().getNumComponents() > 1) {

                //convert to grayscale
                BufferedImage imageToConvert = this.imageTab.getBufferedImage();
                this.imageTab.setBufferedImage( ImageUtils.rgbToGrayscale( imageToConvert ) );
            }

            //show save dialog
            FileUtils.saveAsCSVDialog(this.imageTab.getTabPane().getScene().getRoot(), this.imageTab.getBufferedImage());

        };
    }
}
