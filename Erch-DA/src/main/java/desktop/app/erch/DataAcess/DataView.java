package desktop.app.erch.DataAcess;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Connection.Comport;
import desktop.app.erch.Helper.Common;
import desktop.app.erch.Helper.DateRangePicker;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static desktop.app.erch.Connection.Comport.serialPort;
import static desktop.app.erch.Connection.Connect.*;
import static desktop.app.erch.DataAcess.Upload.clearDatabase;
import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Display.dalert;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bdataread;
import static desktop.app.erch.Helper.Query.*;

public class DataView {
    public static Stage logStage;

    Logger log = LogManager.getLogger(DataView.class);
    public static TableView<Common.DataEntry> tableView = new TableView<>();

    public static AtomicInteger dataCounter = new AtomicInteger(-1);


    TableColumn<Common.DataEntry, String> column;

    private ComboBox<String> serialComboBox;
    private ComboBox<String> vehicleComboBox;

    private DatePicker startDate = new DatePicker();
    private DatePicker endDate = new DatePicker();

    private Label vehModelno = new Label("Vehicle Model No.");
    private Label ecuSerialno = new Label("Erch Serial No.");


    static List<String> columnNames = getColumnNames();

    Scene dataScene;

    public void displayDatalog() throws SQLException{

        logStage = new Stage();
        logStage.setTitle("Access ERCH Data");
        Label header = new Label("ERCH Data Table Viewer");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        logStage.getIcons().add(erchIcon);

        Label[] label = new Label[5];
        label[0] = new Label("Start Date:");
        label[1] = new Label("End Date:  ");
        label[2] = new Label("Connected to ");
        label[3] = new Label("\uD83D\uDD17 Ecu Serial No. :");
        label[4] = new Label("\uD83D\uDD17 Veh Model No.  :");
        for(int i=0;i<5;i++){
            label[i].setStyle("-fx-font-weight: bold;");
        }


        TextField[] field = new TextField[2];

        for(int i =0; i<2 ;i++){
            field[i] = new TextField();
            field[i].setPrefWidth(80);
            field[i].setEditable(false);
            field[i].setStyle("-fx-font-weight: bold;");
            field[i].setFocusTraversable(false);

        }

        HBox h3 = new HBox(10,label[3],field[0]);
        HBox h4 = new HBox(10,label[4],field[1]);


        VBox v2 = new VBox(10,h4,h3);

        if(Comport.isEcuConnected){
            field[0].setText(getEcuSN());
            field[1].setText(getVehMN());
            v2.setVisible(true);
        }
        else{
            field[0].setVisible(false);
            field[1].setVisible(false);

            v2.setVisible(false);
        }


        DateRangePicker.setupDateListeners(startDate, endDate);

        // Set prompt text for start date
        startDate.setPromptText("Select Start Date");

        // Set prompt text for end date
       endDate.setPromptText("Select End Date");
        date(startDate, endDate);

        HBox h1 = new HBox(10,label[0],startDate);
        HBox h2 = new HBox(10,label[1],endDate);

        VBox v1 = new VBox(5,h1,h2);

        HBox dateBox = new HBox(10, v1,v2);

        tableView.setEditable(false);
        //clear the tableview everytime when access Erch Data is opened
        tableView.getItems().clear();


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

        Button uploadButton = createButton("UPLOAD \uD83E\uDC81", "uploadButton");
        Tooltip uploadTooltip = new Tooltip("Upload data from ERCH ECU ➜ PC");
        uploadTooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(uploadButton, uploadTooltip);
        buttonBox.setMargin(uploadButton, new Insets(0, 0, 0, 15));


        Button exportButton = createButton("Export \uD83D\uDCC4", "exportButton");
        Tooltip exportTooltip = new Tooltip("Save as xlsx/csv");
        exportTooltip.setShowDelay(Duration.millis(100));
        buttonBox.setMargin(exportButton, new Insets(0, 50, 0, 0));
        Tooltip.install(exportButton, exportTooltip);



        uploadButton.setOnAction(e -> {
            dataScene.getStylesheets().clear();
            dataScene.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/table-view.css").toExternalForm());
            tableView.getItems().clear();
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

            }else {
                log.warn("Connection not Established");
                sof("Disconnected", "Connection not Established", false);
            }

        });

        // Fetch distinct vehicle numbers from the database
        ObservableList<String> vehicleNumbers = FXCollections.observableArrayList();
        try (PreparedStatement stmt = database().prepareStatement(fetchModelQuery())) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicleNumbers.add(rs.getString("Vmodel No."));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        vehicleComboBox = new ComboBox<>(vehicleNumbers);
        serialComboBox = new ComboBox<>(); // Initialize serialComboBox here

        serialComboBox.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #005288;-fx-font-size: 10; ");
        vehicleComboBox.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #005288;-fx-font-size: 10; ");

        ecuSerialno.setStyle("-fx-text-fill: #FFFFFF;-fx-font-size: 10; ");
        vehModelno.setStyle("-fx-text-fill: #FFFFFF;-fx-font-size: 10; ");

        serialComboBox.setVisible(false); // Initially hidden
        serialComboBox.setPromptText("ECU Serial No.");

        vehicleComboBox.setVisible(false); // Initially hidden
        vehicleComboBox.setPromptText("Veh Model No.");

        ecuSerialno.setVisible(false);
        vehModelno.setVisible(false);

        Button retrieveButton = createButton("Retrieve Data", "retrieveButton");
        retrieveButton.setVisible(false); // Initially hidden

        Button downloadButton = createDownloadButton(retrieveButton);
        Tooltip downloadTooltip = new Tooltip("Load and view saved Data");
        downloadTooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(downloadButton, downloadTooltip);

        retrieveButton.setOnAction(e -> {
            dataScene.getStylesheets().clear();
            dataScene.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/table-viewd.css").toExternalForm());
            String selectedSerial = serialComboBox.getValue();
            String selectedVeh    = vehicleComboBox.getValue();

            LocalDate startDateValue = startDate.getValue();
            LocalDate endDateValue = endDate.getValue();
            log.info("start : {}  end : {}",startDateValue,endDateValue);

            if(selectedSerial != null){
                if (startDateValue != null && endDateValue != null) {
                    Upload data = new Upload();
                    int recordsDownloaded = data.fetchAllData(selectedSerial, selectedVeh,false,startDateValue,endDateValue );
                    if (recordsDownloaded > 0) {
                        sof("DOWNLOAD", "Data Successfully Loaded from "
                                + startDateValue.format(dateformatter)
                                + " to " + endDateValue.format(dateformatter) +
                                "\nNumber of Records Downloaded: " + recordsDownloaded, true);
                    }

                }else {
                    dalert("DOWNLOAD ",
                            "You have not selected start and end dates,\n Do you wish to Download All Data?",
                            selectedSerial, selectedVeh);
                }
            }else{
                Tooltip tooltip = new Tooltip("Please select Serial No.");
                tooltip.setX(serialComboBox.localToScreen(0, 0).getX());
                tooltip.setY(serialComboBox.localToScreen(0, 0).getY() + serialComboBox.getHeight());
                tooltip.show(logStage.getScene().getWindow());

                // Hide the tooltip after a certain duration if needed
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(event -> tooltip.hide());
                pause.play();
            }


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

        dataScene.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/table-view.css").toExternalForm());

        // Maximize the custom stage
        logStage.setScene(dataScene);
        logStage.setMaximized(true);

        // Show the custom stage
        logStage.showAndWait();


}


    private Button createDownloadButton(Button retrieveButton) {
        Button downloadButton = createButton("DOWNLOAD \uD83E\uDC83", "downloadButton");

        // Set up event handler for vehicleComboBox
        vehicleComboBox.setOnAction(event -> {
            String selectedVehicle = vehicleComboBox.getValue();
            if (selectedVehicle != null) {
                serialComboBox.setItems(populateSerialComboBox(selectedVehicle));
            }
        });

        downloadButton.setOnAction(event -> {
            dataScene.getStylesheets().clear();
            dataScene.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/table-viewd.css").toExternalForm());
            downloadButton.setText("");
            ecuSerialno.setVisible(true);
            vehModelno.setVisible(true);
            vehicleComboBox.setVisible(true);
            serialComboBox.setVisible(true);
            retrieveButton.setVisible(true);
        });

        VBox[] vBox = new VBox[5];

        for (int i=0;i<5;i++){
            vBox[i] = new VBox(3);
            vBox[i].setAlignment(Pos.CENTER);
        }

        vBox[0].getChildren().addAll(vehModelno,vehicleComboBox);
        vBox[1].getChildren().addAll(ecuSerialno,serialComboBox);

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(vBox[0], vBox[1], retrieveButton);


        // Add the VBox to the StackPane when downloadButton is clicked
        downloadButton.setOnMouseClicked(e -> {
            downloadButton.setGraphic(hBox); // Set the VBox as the graphic of the button
        });

        Tooltip tooltip1 = new Tooltip("Select Vehicle Model No.");
        tooltip1.setShowDelay(Duration.millis(100)); // Adjust the delay as needed
        Tooltip.install(vehicleComboBox, tooltip1);

        Tooltip tooltip2 = new Tooltip("Select ECU Serial No.");
        tooltip2.setShowDelay(Duration.millis(100)); // Adjust the delay as needed
        Tooltip.install(serialComboBox, tooltip2);

        return downloadButton;
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
        );
    }


}
