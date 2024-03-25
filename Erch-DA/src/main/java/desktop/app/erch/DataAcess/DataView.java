package desktop.app.erch.DataAcess;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Connection.Comport;
import desktop.app.erch.Helper.Common;
import desktop.app.erch.Helper.DateRangePicker;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static desktop.app.erch.Connection.Comport.serialPort;
import static desktop.app.erch.Connection.Connect.*;
import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Display.dalert;
import static desktop.app.erch.Helper.Frames.bdataread;
import static desktop.app.erch.Helper.Query.*;

public class DataView {
    public static Stage logStage;

    Logger log = LogManager.getLogger(DataView.class);
    public static TableView<Common.DataEntry> tableView = new TableView<>();

    public static AtomicInteger dataCounter = new AtomicInteger(-1);


    TableColumn<Common.DataEntry, String> column;


    static boolean isUpload = false;

    static List<String> columnNames = getColumnNames();

    Scene dataScene;

    public void displayDatalog() throws SQLException{

        logStage = new Stage();
        logStage.setTitle("Access ERCH Data");
        Label header = new Label("ERCH Data Table Viewer");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        logStage.getIcons().add(erchIcon);


        Label startLabel = new Label("Start Date:");
        Label endLabel = new Label("End Date:");
        Label serialLabel = new Label("Connected to \n\uD83D\uDD17 ECU Serial No. :");
        serialLabel.setStyle("-fx-font-weight: bold;");

        TextField[] field = new TextField[2];

        for(int i =0; i<2 ;i++){
            field[i] = new TextField();
            field[i].setPrefWidth(80);
            field[i].setEditable(false);
            field[i].setStyle("-fx-font-weight: bold;");

        }


        if(Comport.isEcuConnected){
            field[0].setText(getEcuSN());
            field[1].setText(getVehMN());
        }
        else{
            field[0].setVisible(false);
            field[1].setVisible(false);

            serialLabel.setVisible(false);
        }

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        DateRangePicker.setupDateListeners(startDate, endDate);

        // Set prompt text for start date
        startDate.setPromptText("Select Start Date");

        // Set prompt text for end date
        endDate.setPromptText("Select End Date");


        HBox dateBox = new HBox(10, startLabel, startDate, endLabel, endDate, serialLabel);

        // Apply margin to labels
        HBox.setMargin(startLabel, new Insets(15, 0, 30, 0));
        HBox.setMargin(endLabel, new Insets(15, 0, 30, 0));
        HBox.setMargin(startDate, new Insets(11, 0, 30, 0));
        HBox.setMargin(endDate, new Insets(11, 0, 30, 0));
        HBox.setMargin(serialLabel, new Insets(15, 0, 10, 50));

//      HBox.setMargin(serTextField, new Insets(11, 0, 0, 0));

        tableView.setEditable(false);


        for (int i = 0; i < columnNames.size(); i++) {

            if (i == 0) {
                column = new TableColumn<>(columnNames.get(i));
                column.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getSlNo())));
            } else {
                column = new TableColumn<>(columnNames.get(i));
                int columnIndex = i;
                column.setCellValueFactory(cellData -> cellData.getValue().getProperty(columnIndex));
            }

            // Center align the content in the cells
            column.setCellFactory(tc -> new TableCell<Common.DataEntry, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Center align the content
                    }
                }
            });

            tableView.getColumns().add(column);

    }

        setPreferredColumnWidths(tableView);

        // Create an HBox to hold the buttons
        HBox buttonBox = new HBox(25);


        /*
         TODO : Write a function to add combobox that holds serial no. and Vehicle model no.
        */

        Button uploadButton = createButton("UPLOAD \uD83E\uDC81", "uploadButton");
        Tooltip uploadTooltip = new Tooltip("Upload data from ERCH ECU ➜ PC");
        uploadTooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(uploadButton, uploadTooltip);
        buttonBox.setMargin(uploadButton, new Insets(0, 0, 0, 15));


        Button downloadButton = createButton("DOWNLOAD \uD83E\uDC83", "downloadButton");
        Tooltip downloadTooltip = new Tooltip("Load and view saved Data");
        downloadTooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(downloadButton, downloadTooltip);


        Button exportButton = createButton("Export \uD83D\uDCC4", "exportButton");
        Tooltip exportTooltip = new Tooltip("Save as xlsx/csv");
        exportTooltip.setShowDelay(Duration.millis(100));
        buttonBox.setMargin(exportButton, new Insets(0, 50, 0, 0));
        Tooltip.install(exportButton, exportTooltip);



        uploadButton.setOnAction(e -> {
            isUpload = true;

            if(Comport.isEcuConnected) {

                SerialPort selectedPort =  Comport.getConnectedPort();

                try {
                    boolean portOpened = selectedPort.openPort();
                    if (portOpened) {

                        setComportParameters(selectedPort);

                        serialPort.writeBytes(bdataread(), bdataread().length);
                        clearDatabase();
                        Upload data = new Upload();
                        data.startUploadTask(selectedPort);
                        dataCounter.set(-1);


                    } else {
                        failed(log);
                    }

                } catch (Exception er) {
                    error(er, log);
                }

            }

        });


        downloadButton.setOnAction(e -> {
            isUpload = false;
            dalert("DOWNLOAD ", "You have not selected start and end dates,\n Do you wish to Download All Data?");

        });


        // Add the buttons to the HBox
        buttonBox.getChildren().addAll(uploadButton,downloadButton, exportButton,dateBox);

        // Create a VBox to organize the elements
        VBox selectBox = new VBox();
        // Set the VBox to grow and fill the available space for the TableView
        VBox.setVgrow(tableView, Priority.ALWAYS);
        selectBox.setFillWidth(true);
        selectBox.getChildren().add(header);
        selectBox.getChildren().add(buttonBox);

        // Add the TableView to the VBox after setting the growth constraints
        selectBox.getChildren().add(tableView);



        // Create a Scene for the custom stage
        dataScene = new Scene(selectBox, 800, 600);


        if (isUpload) {
            dataScene.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/table-viewd.css").toExternalForm());
        } else {
            dataScene.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/table-view.css").toExternalForm());
        }

        // Maximize the custom stage
        logStage.setScene(dataScene);
        logStage.setMaximized(true);

        // Show the custom stage
        logStage.showAndWait();


}


    private void clearDatabase() {
        try (PreparedStatement statement = database().prepareStatement(deleteQuery())) {
            statement.setString(1, getVehMN()); // Replace vehNo with the actual vehicle number
            statement.setString(2, getVehEN()); // Replace engNo with the actual engine number
            statement.setString(3, getEcuSN()); // Replace erchNo with the actual erch number
            statement.executeUpdate();

            try (PreparedStatement junk = database().prepareStatement(deleteQuery())) {
                String empty = "00000000";
                junk.setString(1, empty);
                junk.setString(2, empty);
                junk.setString(3, empty);
                junk.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private Button createButton(String text, String id) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.setId(id);
        return button;

    }

    public static void setPreferredColumnWidths(TableView<Common.DataEntry> tableView) {
        for (TableColumn<Common.DataEntry, ?> column : tableView.getColumns()) {
            String header = column.getText();
            int preferredWidth = getPreferredWidth(header);
            column.setPrefWidth(preferredWidth);
        }
    }

    private static int getPreferredWidth(String header) {
        switch (header) {
            case "Sl No.":
                return 50;
            case "Date":
            case "Time":
                return 100;
            default:
                return columnNames.contains(header) ? 130 : 230;
        }
    }


    static List<String> getColumnNames() {
        return Arrays.asList(
                "Sl No.",
                "Date",
                "Time",
                "  Cylinder Head \nTemperature7(°C)",
                "  Cylinder Head \nTemperature8(°C)",
                "    Engine Oil \nTemperature(°C)",
                "  Ambient Air \nTemperature(°C)",
                "  Ambient Air \nPressure(bar)",
                "  Exhaust Air \nPressure(bar)",
                "  Engine Oil \nPressure(bar)",
                "   Battery \nVoltage(V)",
                "    Engine \nSpeed(RPM)",
                "Turbocharger \n  Speed(RPM)",
                "Cooling Fan \nSpeed(RPM)",
                "    Vehicle \nSpeed(RPM)",
                "   Alternator \nSpeed(RPM)",
                "Mean Sea level \n   Altitude(Mtr)",
                "Exhaust Brake \n      Status",
                "Cooling Valve \n      Status",
                "Fuel Sol Valve \n      Status",
                "Heater Sol \n   Status",
                "Engine Started \n        Count",
                "Engine Overspeed \n           Count",
                "Engine Overheat \n           Count",
                "Vehicle Overspeed \n           Count"
                //    "RTC Battery Voltage(V)"
        );
    }


}
