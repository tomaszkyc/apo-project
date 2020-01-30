package com.tomaszkyc.app.ui.model.tab;

import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.ui.model.contextmenu.*;
import com.tomaszkyc.app.ui.view.root.RootViewController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageTab extends Tab {

    private static final Logger log = LogManager.getLogger(ImageTab.class.getName());

    private Image image;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageTab)) return false;
        ImageTab imageTab = (ImageTab) o;
        return isSelectedForCompare() == imageTab.isSelectedForCompare() &&
                getImage().equals(imageTab.getImage()) &&
                getBufferedImage().equals(imageTab.getBufferedImage()) &&
                getImageView().equals(imageTab.getImageView()) &&
                getImageName().equals(imageTab.getImageName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getImage(), getBufferedImage(), getImageView(), isSelectedForCompare(), getImageName());
    }

    private BufferedImage bufferedImage;
    private ImageView imageView;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public boolean isSelectedForCompare() {
        return selectedForCompare;
    }

    public void setSelectedForCompare(boolean selectedForCompare) {
        this.selectedForCompare = selectedForCompare;
    }

    private boolean selectedForCompare = false;

    public boolean isCompareWithSelected() {
        return compareWithSelected;
    }

    public void setCompareWithSelected(boolean compareWithSelected) {
        this.compareWithSelected = compareWithSelected;
    }

    private boolean compareWithSelected = false;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    private String imageName;

    public ImageTab(BufferedImage bufferedImage, String imageName) {
        super(imageName);
        this.imageName = imageName;
        this.image = SwingFXUtils.toFXImage(bufferedImage, null);
        this.imageView = new ImageView(image);
        this.bufferedImage = bufferedImage;
        prepareImageTab();
        addCustomContextMenu();
        log.debug("Created tab with name: " + this.getText());
    }


    /**
     * method prepare tab properties and event handlers
     */
    private void prepareImageTab() {

        //for closed event
        this.setOnClosed(event -> {

            if ( this.getTabPane() != null ) {

                int actualTabsSize = this.getTabPane().getTabs().size();
                //hide if last tab in tab pane
                if ( actualTabsSize == 1 ) {
                    this.getTabPane().setVisible(false);
                }
                else {
                    this.getTabPane().setVisible(true);
                }

            }
        });

        //set image in tab
        this.setContent(imageView);

    }

    /**
     * method generate custom context menu to tab
     */
    private void addCustomContextMenu() {

        //generate context menu object
        ContextMenu contextMenu = new ContextMenu();

        //add 'show number table' feature
        ShowNumberTableMenuItemBuilder showNumberTableMenuItemBuilder = new ShowNumberTableMenuItemBuilder(this);
        MenuItem showNumberTable = showNumberTableMenuItemBuilder.build();

        //add 'export to excel' feature
        ExportToExcelMenuItemBuilder exportToExcelMenuItemBuilder = new ExportToExcelMenuItemBuilder( this);
        MenuItem exportToExcel = exportToExcelMenuItemBuilder.build();

        //add 'select for compare' feature
        SelectForCompareMenuItemBuilder selectForCompareMenuItemBuilder = new SelectForCompareMenuItemBuilder(this);
        MenuItem selectForCompareMenuItem = selectForCompareMenuItemBuilder.build();

        //add 'convert to grayscale' feature
        ConvertToGrayScaleMenuItemBuilder convertToGrayScaleMenuItemBuilder = new ConvertToGrayScaleMenuItemBuilder(this);
        MenuItem convertToGrayScaleMenuItem = convertToGrayScaleMenuItemBuilder.build();


        contextMenu.getItems().add( showNumberTable );
        contextMenu.getItems().add( exportToExcel );
        contextMenu.getItems().add( selectForCompareMenuItem );
        contextMenu.getItems().add( convertToGrayScaleMenuItem );
        this.setContextMenu(contextMenu);

    }

    /**
     * method fits imageview to tab pane width and height
     */
    public void renderImageView() {

        imageView.fitWidthProperty().bind( this.getTabPane().widthProperty() );
        imageView.fitHeightProperty().bind( this.getTabPane().heightProperty() );

    }

    /**
     * method checks if should add additional menu items for context
     */
    public void checkIfShouldAddSelectedForCompareOption() {

        if ( isAnyTabIsSelectedForCompare() ) {

            addCompareWithSelectedMenuItemToOtherTabsIfNeeded(this);

        }

    }

    /**
     * method checks if any opened tab has selected field 'selectedForCompare'
     * @return true if any tab has selected field or false otherwise
     */
    private boolean isAnyTabIsSelectedForCompare() {

        if ( this.getTabPane() == null ) return false;

        return this.getTabPane().getTabs().stream()
                            .map(p -> (ImageTab)p).
                            anyMatch( x -> x.isSelectedForCompare() );

    }

    public int getNumberOfChannels() {

        if ( bufferedImage == null ) return 0;
        return bufferedImage.getColorModel().getNumComponents();

    }

    /**
     * function add "Compare with selected" menu option to other tabs
     */
    private void addCompareWithSelectedMenuItemToOtherTabsIfNeeded(ImageTab actualImageTab) {

        //create new menu item
        CompareWithSelectedMenuItemBuilder compareWithSelectedMenuItemBuilder = new CompareWithSelectedMenuItemBuilder(actualImageTab);
        MenuItem compareWithSelected = compareWithSelectedMenuItemBuilder.build();
        compareWithSelected.setDisable(false);


        actualImageTab.getTabPane().getTabs().forEach( tab -> {

            ImageTab tempImageTab = (ImageTab)tab;

            //check if tab isn't this tab
            if ( !tempImageTab.isSelectedForCompare() ) {

                AtomicBoolean alreadyInMenu = new AtomicBoolean(false);
                String compareWithSelectedMenuItemLabel = ServiceFactory.getTranslationService()
                                                                .getTranslation("app.tabpane.imagetab.contextmenu.menuitem.compare-with-selected.text");

                tab.getContextMenu().getItems().forEach( item -> {

                    if ( item.getText().equalsIgnoreCase( compareWithSelectedMenuItemLabel ) ) {
                        alreadyInMenu.set(true);
                    }

                } );

                //if isn't added to menu --> add
                if ( !alreadyInMenu.get() ) {
                    tab.getContextMenu().getItems().add( compareWithSelected );
                }

            }

        } );
    }


}
