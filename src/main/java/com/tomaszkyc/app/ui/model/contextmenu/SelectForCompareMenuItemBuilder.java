package com.tomaszkyc.app.ui.model.contextmenu;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.ui.model.tab.ImageTab;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class SelectForCompareMenuItemBuilder implements CustomMenuItemBuilder {


    private static final Logger log = LogManager.getLogger(SelectForCompareMenuItemBuilder.class.getName());

    private String itemName;
    private ImageTab imageTab;
    private EventHandler<ActionEvent> eventHandler;
    private MenuItem menuItem;

    public SelectForCompareMenuItemBuilder(ImageTab imageTab) {
        this.itemName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.select-for-compare.text");
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


            log.debug("Select for compare clicked in tab: " + this.imageTab.getText());


            //select for compare
            this.imageTab.setSelectedForCompare(true);
            this.menuItem.setDisable( true );

            addCompareWithSelectedMenuItemToOtherTabsIfNeeded(this.imageTab);
            enableCompareWithSelectedMenuItemToOtherTabs(this.imageTab);
            removeCompareWithSelectedFromActualTab(this.imageTab);
            changeSelectedForCompareFieldInOtherTabs(this.imageTab);

            log.debug("After clicked 'Select for compare' value is: " + this.imageTab.isSelectedForCompare());

        };
    }

    /**
     * method sets value to false for field which shows if tab is selected for compare
     * @param actualImageTab actual image tab
     */
    private void changeSelectedForCompareFieldInOtherTabs(ImageTab actualImageTab) {

        actualImageTab.getTabPane().getTabs().stream().map(tab -> (ImageTab)tab)
                                                .filter( tab -> !tab.equals(actualImageTab) )
                                                .forEach( tab -> tab.setSelectedForCompare(false) );

    }

    /**
     * method remove option 'Compare with selected' in actual tab
     * @param actualImageTab actual image tab
     */
    private void removeCompareWithSelectedFromActualTab(ImageTab actualImageTab) {

        //get searched menu text
        String compareWithSelectedMenuItemTitle = ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.compare-with-selected.text");
        actualImageTab.getContextMenu().getItems()
                        .removeIf( x -> x.getText().equalsIgnoreCase(compareWithSelectedMenuItemTitle) );



    }

    /**
     * method enable option 'Compare with selected' in other menu tabs
     * @param actualImageTab actual image tabs
     */
    private void enableCompareWithSelectedMenuItemToOtherTabs(ImageTab actualImageTab) {

        //get from all tabs only this which isn't the actual instance of ImageTab class
        actualImageTab.getTabPane().getTabs().stream().map(x -> (ImageTab)x)
                                    .filter( x -> !x.equals(actualImageTab) )
                                    .forEach( tab -> {

                                        //get only menu elements which names equals 'Select for compare'
                                        //and disable this context menu item
                                        tab.getContextMenu().getItems().stream()
                                                .filter( item -> item.getText().equalsIgnoreCase(itemName) )
                                                .forEach( item -> item.setDisable(false)  );

        } );


    }

    /**
     * function add "Compare with selected" menu option to other tabs
     */
    private void addCompareWithSelectedMenuItemToOtherTabsIfNeeded(ImageTab actualImageTab) {



        String compareWithSelectedMenuItemTitle = ServiceFactory.getTranslationService().getTranslation("app.tabpane.imagetab.contextmenu.menuitem.compare-with-selected.text");

        //filter all tabs which aren't actual tab
        actualImageTab.getTabPane().getTabs().stream().map(x -> (ImageTab)x)
                .filter( x -> !x.equals(actualImageTab) )
                .forEach( tab -> {
                    //check if exists searched menu tab
                    boolean itemExists = tab.getContextMenu().getItems().stream()
                                            .anyMatch(x -> x.getText().equalsIgnoreCase(compareWithSelectedMenuItemTitle));

                    if ( !itemExists ) {

                        //create new menu item
                        CompareWithSelectedMenuItemBuilder compareWithSelectedMenuItemBuilder = new CompareWithSelectedMenuItemBuilder(tab);
                        MenuItem compareWithSelected = compareWithSelectedMenuItemBuilder.build();
                        compareWithSelected.setDisable(false);
                        tab.getContextMenu().getItems().add( compareWithSelected );
                    }

                } );

    }
}
