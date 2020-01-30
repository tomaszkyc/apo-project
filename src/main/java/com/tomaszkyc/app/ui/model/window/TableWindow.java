package com.tomaszkyc.app.ui.model.window;

import com.tomaszkyc.app.MainApp;
import com.tomaszkyc.app.core.services.ServiceFactory;
import com.tomaszkyc.app.core.services.translation.TranslationService;
import com.tomaszkyc.app.core.utils.FileUtils;
import com.tomaszkyc.app.core.utils.ImageUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class TableWindow extends Stage {

    private static final Logger log = LogManager.getLogger(TableWindow.class.getName());

    private TranslationService translationService;

    BufferedImage image;
    TableView<Integer[]> tableView = new TableView<>();

    public TableWindow(Window owner, BufferedImage image, String title) {
        super();
        this.translationService = ServiceFactory.getTranslationService();
        this.image = image;
        if (image.getColorModel().getNumComponents() > 1) this.image = ImageUtils.rgbToGrayscale(image);
        ToolBar toolbar = new ToolBar();
        Button csvButton = new Button(translationService.getTranslation("app.tabpane.imagetab.contextmenu.menuitem.show-number-table.buttons.export-to-csv.text"));
        addExcelIcon(csvButton);
        toolbar.getItems().addAll(csvButton);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        borderPane.setTop(toolbar);
        this.setMinWidth(1000);
        this.setHeight(800);
        Scene newScene = new Scene(borderPane);

        //add icon to window
        addIconToStage(this);

        this.setScene(newScene);
        this.setTitle(title);
        this.setAlwaysOnTop(false);
        this.initOwner(owner);
        this.show();
        fillTable(tableView, image);
        csvButton.setOnAction(event -> FileUtils.saveAsCSVDialog(tableView.getScene().getRoot(), this.image));
    }

    public TableWindow(Integer[][] imageData, String title) throws Exception {
        super();
        this.translationService = ServiceFactory.getTranslationService();
        ToolBar toolbar = new ToolBar();
        Button csvButton = new Button(translationService.getTranslation("app.tabpane.imagetab.contextmenu.menuitem.show-number-table.buttons.export-to-csv.text"));
        addExcelIcon(csvButton);
        toolbar.getItems().addAll(csvButton);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        borderPane.setTop(toolbar);
        this.setMinWidth(1000);
        this.setHeight(800);
        Scene newScene = new Scene(borderPane);
        this.setScene(newScene);
        this.setTitle(title);
        this.setAlwaysOnTop(false);

        //add icon to window
        addIconToStage(this);

        this.show();
        fillTableWithData(imageData);
        csvButton.setOnAction(event -> FileUtils.saveAsCSVDialog( tableView.getScene().getRoot(), imageData ));


    }

    /**
     * method generate table with image data
     * @param imageData image data to create and render table
     * @throws Exception in case of any error in preparing data and tab
     */
    private void fillTableWithData(Integer[][] imageData) throws Exception {

        log.debug("Started filling table with data");
        ObservableList<Integer[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(imageData) );
        for (int i = 0; i <= imageData[0].length; i++) {
            final int colNo = i;
            TableColumn<Integer[], String> tc;
            if(colNo == 0) {
                tc = new TableColumn<>("#");
                tc.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(data.indexOf(p.getValue())+1)));
            }
            else {
                tc = new TableColumn<>(String.valueOf(colNo));
                tc.setCellValueFactory(p -> new SimpleStringProperty((p.getValue()[colNo-1]).toString()));
            }
            tc.setPrefWidth(35);
            tc.setSortable(false);
            tableView.getColumns().add(tc);
        }
        tableView.setItems(data);
        log.debug("Finished filling table with data");
    }

    /**
     * method fill given tableview with image data from BufferedImage class object
     * @param table table to fill with data
     * @param image image from we need to extract data about pixel values
     */
    private void fillTable(TableView<Integer[]> table, BufferedImage image) {
        Integer[][] imageArray = ImageUtils.asTwoDimensionalArray(image);
        ObservableList<Integer[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(imageArray));
        for (int i = 0; i <= imageArray[0].length; i++) {
            final int colNo = i;
            TableColumn<Integer[], String> tc;
            if(colNo == 0) {
                tc = new TableColumn<>("#");
                tc.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(data.indexOf(p.getValue())+1)));
            }
            else {
                tc = new TableColumn<>(String.valueOf(colNo));
                tc.setCellValueFactory(p -> new SimpleStringProperty((p.getValue()[colNo-1]).toString()));
            }
            tc.setPrefWidth(35);
            tc.setSortable(false);
            table.getColumns().add(tc);
        }
        table.setItems(data);
    }

    /**
     * method add Excel icon to given button
     * @param button button to add excel icon
     */
    private void addExcelIcon(Button button) {

        Image icon = new Image(MainApp.class.getResource("/images/excel-icon.png").toString(),40, 40, false, false);
        button.setGraphic( new ImageView(icon));

    }


    /**
     * method add icon to given stage
     * @param stage stage where icon will be added
     */
    private void addIconToStage(Stage stage) {
        stage.getIcons().add( new Image( MainApp.class.getResource("/images/icon.png").toString() ) );
    }
}
