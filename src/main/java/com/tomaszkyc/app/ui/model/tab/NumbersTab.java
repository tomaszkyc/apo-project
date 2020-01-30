package com.tomaszkyc.app.ui.model.tab;

import com.tomaszkyc.app.core.utils.ImageUtils;
import com.tomaszkyc.app.ui.model.contextmenu.ChooseImageChannelMenuItemBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;

public class NumbersTab extends Tab {

    private static final Logger log = LogManager.getLogger(NumbersTab.class.getName());

    private  int[][] rawImageData;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    /**
     * update table data (which is rendered by TableView) with new image data
     * @param newImageData new image data
     */
    public void updateTableData(Integer[][] newImageData) {
        this.tableData.clear();
        this.tableData.addAll(newImageData);
    }

    private ObservableList<Integer[]> tableData;

    public Integer[][] getImageData() {
        return imageData;
    }

    public void setImageData(Integer[][] imageData) {
        this.imageData = imageData;
    }

    private Integer[][] imageData = null;
    private TableView<Integer[]> tableView = null;
    private TableView<int[]> rawTableView = null;

    /**
     * method gets actual image object number of channels
     * Number of channels is calculated every time the method is invoked
     * @return number or channels in image
     */
    public int getImageNumberOfChannels() {

        return ImageUtils.getImageChannels(image);

    }

    public BufferedImage getImage() {
        return image;
    }


    public void setImage(BufferedImage image) throws Exception {

        this.image = image;
        int numberOfChannels = getImageNumberOfChannels();
        // if image not grayscale
        if (numberOfChannels != 1) {
            log.debug("Context menu needed.");
            prepareContextMenuIfNeeded();
        }
    }

    private BufferedImage image;


    public NumbersTab(Integer[][] imageData, String tabText, BufferedImage image) throws Exception {
        this.imageData = imageData;
        this.tableView = new TableView<>();
        this.setText( tabText );
        this.tableData = FXCollections.observableArrayList();
        setImage(image);
        prepareTab();

    }

    /**
     * prepare tab for showing in application
     * @throws Exception in case of any error in preparing data and tab
     */
    public void prepareTab() throws Exception {

        log.debug("Started building numbers table");

        if (this.tableView == null){
            this.tableView = new TableView<>();
        }
        this.setContent( this.tableView );
        fillTableWithData();


        log.debug("Finished building numbers table");
    }

    /**
     * method generate table with image data
     * @throws Exception in case of any error in preparing data and tab
     */
    public void fillTableWithData() throws Exception {

        log.debug("Started filling table with data");
        tableData.clear();
        tableData.addAll(imageData);
        for (int i = 0; i <= imageData[0].length; i++) {
            final int colNo = i;
            TableColumn<Integer[], String> tc;
            if(colNo == 0) {
                tc = new TableColumn<>("#");
                tc.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(tableData.indexOf(p.getValue())+1)));
            }
            else {
                tc = new TableColumn<>(String.valueOf(colNo));
                tc.setCellValueFactory(p -> new SimpleStringProperty((p.getValue()[colNo-1]).toString()));
            }
            tc.setPrefWidth(35);
            tc.setSortable(false);
            tableView.getColumns().add(tc);
        }
        tableView.setItems(tableData);
        log.debug("Finished filling table with data");
    }

    /**
     * prepare context menu for given tab (context menu with changing image channels data)
     * @throws Exception in case of error in building context menu
     */
    public void prepareContextMenuIfNeeded() throws Exception {

        //generate context menu object
        ContextMenu contextMenu = new ContextMenu();

        //build menu
        ChooseImageChannelMenuItemBuilder chooseImageChannelMenuItemBuilder = new ChooseImageChannelMenuItemBuilder(this);
        Menu subMenu = chooseImageChannelMenuItemBuilder.build();
        contextMenu.getItems().add(subMenu);

        //set context
        this.setContextMenu(contextMenu);
    }

}
