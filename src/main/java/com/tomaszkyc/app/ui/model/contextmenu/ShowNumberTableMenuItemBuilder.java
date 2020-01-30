package com.tomaszkyc.app.ui.model.contextmenu;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.ui.model.tab.ImageTab;
import com.tomaszkyc.app.ui.model.window.TableWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShowNumberTableMenuItemBuilder implements CustomMenuItemBuilder {


    private static final Logger log = LogManager.getLogger(ExportToExcelMenuItemBuilder.class.getName());

    private String itemName;
    private ImageTab imageTab;
    private EventHandler<ActionEvent> eventHandler;

    public ShowNumberTableMenuItemBuilder(ImageTab imageTab) {
        this.itemName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.show-number-table.text");
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
            log.debug("Show number table clicked");
            //generate window title
            String windowTitle = String.format("%s - %s", this.imageTab.getImageName(),
                    ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.export-to-csv.window.title"));

            //generate window title
            TableWindow tableWindow = new TableWindow(this.imageTab.getTabPane().getScene().getWindow(),
                    this.imageTab.getBufferedImage(),
                    windowTitle);

        };
    }
}
