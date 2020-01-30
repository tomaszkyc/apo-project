package com.tomaszkyc.app.ui.model.contextmenu;

import com.tomaszkyc.app.core.model.image.*;
import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.ui.model.tab.NumbersTab;
import com.tomaszkyc.app.ui.view.window.CompareWindowController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChooseImageChannelMenuItemBuilder {

    private static final Logger log = LogManager.getLogger(ChooseImageChannelMenuItemBuilder.class.getName());

    private String itemName;
    private NumbersTab numbersTab;
    private EventHandler<ActionEvent> eventHandler;
    private Menu menu;

    public ChooseImageChannelMenuItemBuilder(NumbersTab numbersTab) {
        this.itemName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.numberstab.tabs.contextmenu.change-image-channel");
        this.numbersTab = numbersTab;
    }


    public Menu build() {

        menu = new Menu(itemName);
        prepareMenuItems();
        return menu;

    }


    private void prepareMenuItems() {

        //get available channels list
        List<Channel> availableChannels = getAvailableChannels();

        //build menu items
        List<MenuItem> menuItems = buildMenuItems(availableChannels);

        //add to menu and build
        this.menu.getItems().addAll( menuItems );
    }

    private List<MenuItem> buildMenuItems(List<Channel> availableChannels) {

        return availableChannels.stream()
                .map( channel -> buildSingleItem(channel))
                .collect(Collectors.toList());


    }

    private MenuItem buildSingleItem(Channel channel) {

        MenuItem menuItem = new MenuItem();
        String optionName = null;
        EventHandler<ActionEvent> event = null;
        ImageDataConverter imageDataConverter = null;
        if ( channel.equals( Channel.GRAY ) ) {

            optionName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.numberstab.tabs.graytab.text");
            imageDataConverter = new GrayDataConverter();
        }
        else if ( channel.equals( Channel.RED ) ) {

            optionName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.numberstab.tabs.redtab.text");
            imageDataConverter = new RedDataConverter();
        }
        else if ( channel.equals( Channel.GREEN ) ) {

            optionName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.numberstab.tabs.greentab.text");
            imageDataConverter = new GreenDataConverter();
        }
        else if ( channel.equals( Channel.BLUE ) ) {

            optionName = ServiceFactory.getTranslationService().getTranslation("app.tabpane.numberstab.tabs.bluetab.text");
            imageDataConverter = new BlueDataConverter();
        }

        event = prepareEventForMenuItem(imageDataConverter, this.numbersTab);


        menuItem.setOnAction(event);
        menuItem.setText(optionName);
        return menuItem;
    }

    private EventHandler<ActionEvent> prepareEventForMenuItem(ImageDataConverter imageDataConverter, NumbersTab tab) {

        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                //generate new data
                Integer[][] newImageData = imageDataConverter.asTwoDimensionalArray(tab.getImage());

                try {
                    //update data in tableview
                    tab.setImageData(newImageData);
                    tab.updateTableData(newImageData);

                    //update tab title
                    String newTableTitle = generateActualTableName(imageDataConverter.getChannel(), numbersTab.getFileName());
                    numbersTab.setText(newTableTitle);

                    //invoke again generation of image channels option
                    numbersTab.prepareContextMenuIfNeeded();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
        };


    }

    private String generateActualTableName(Channel channel, String fileName) {

        String channelLabel = ServiceFactory.getTranslationService().getTranslation(String.format("app.tabpane.numberstab.tabs.%stab.text", channel.name().toLowerCase()));
        return String.format(CompareWindowController.COMPARE_WINDOW_TAB_TEXT_FORMAT,
                fileName,
                channelLabel);

    }

    private List<Channel> getAvailableChannels() {

        String actualTabName = numbersTab.getText();
        return Stream.of(Channel.values())
                .filter( channel -> !actualTabName.toLowerCase().contains( channel.getName() ) )
                .collect(Collectors.toList());
    }
}
