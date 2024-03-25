package desktop.app.erch.DataAcess;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Common;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static desktop.app.erch.Connection.Comport.serialPort;
import static desktop.app.erch.Connection.Connect.*;
import static desktop.app.erch.DataAcess.DataView.*;
import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.DataProcess.processAndShowData;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Query.*;

public class Upload {

    Dialog loading = new Dialog();
    ByteArrayOutputStream datalogBuffer = new ByteArrayOutputStream();

    Logger log = LogManager.getLogger(Upload.class);

    Timer datalogTimer = new Timer();

    int totalDeletedCount = 0;

    AtomicInteger deletedRecords = new AtomicInteger(0);

    String[] column_Names;

    ObservableList<DataEntry> dataEntries = FXCollections.observableArrayList();



    public void startUploadTask(SerialPort selectedPort){
         /*
         startUploadTask is used to receive frames from ECU,
         it stops receiving when EOF is found
         */

        Task<Void> backgroundTask = new Task<>() {
            @Override
            protected Void call() {
                while (true) {
                    byte[] responsePart = new byte[1];
                    int numBytesRead = serialPort.readBytes(responsePart, 1);


                    if (numBytesRead != 1) {
                        sof("Connection Failed", "Error reading the response.", false);
                        break; // Exit the loop
                    }

                    datalogBuffer.write(responsePart[0]);

                    byte[] datalogResponse = datalogBuffer.toByteArray();

                    // Reset the timer each time a response is received
                    datalogTimer.cancel();
                    datalogTimer = new Timer(); // Initialize a new timer
                    datalogTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                closeLoadingDialog();
                                sof("UPLOAD Failed", "Error Uploading Records ❗", false);
                                sof("Connection Issue", "Only "+ (dataCounter.get()-totalDeletedCount)+" Records Uploaded," +"\nNo Records received for a while. Upload has stopped." +"\nPlease check port Connection!", false);
                            });
                            selectedPort.closePort();
                        }
                    }, 5000); // 5 seconds timeout

                    if  (containsSequence(datalogResponse, new byte[]{0x0D,0x0A,0x0D,'E', 'O', 'F'},log)) {
                        log.info("End of Frame Detected");
                        datalogTimer.cancel();
                        datalogBuffer.reset();
                        errorDatabase();
                        selectedPort.closePort();
                        log.info("Records Received : {} , Deleted {} , Total {} ",dataCounter.get(),totalDeletedCount,dataCounter.get()-totalDeletedCount);
                        Platform.runLater(() -> {
                            closeLoadingDialog();
                            sof("UPLOAD", dataCounter.get()-totalDeletedCount + " Records Uploaded Successfully", true);
                            dataCounter.set(-1);
                            deletedRecords.set(0);
                            totalDeletedCount=0;
                        });

                        break;

                    }
                    else if(containsSequence(datalogResponse, new byte[]{'a', '0', '9'},log)){
                        log.info("Datalog Received Response :");
                        for (byte b : datalogResponse) {
                            System.out.printf("%02X ", b);
                        }
                        System.out.println(" \n ");

                        Platform.runLater(() -> {
                            insertDataToDatabaseAndTableView(datalogResponse);
                            uLoading(dataCounter.get()-totalDeletedCount + " Records Uploading, \n Please wait...");
                        });
                        datalogBuffer.reset();
                        dataCounter.addAndGet(1);

                        log.info("Buffer is cleared --- Response Length: {}  --- Datacount: {}", datalogResponse.length , dataCounter );

                    }

                }

                return null;
            }
        };

        // Start the background task in a new thread
        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.setDaemon(true); // Set the thread as a daemon thread to allow program exit
        backgroundThread.start();

    }


    private void insertDataToDatabaseAndTableView(byte[] response) {

        String[] datalogValues = processAndShowData(response,false,log);
        insertDataToDatabase(datalogValues);
        fetchAllData();
        tableView.refresh();

    }


    public void insertDataToDatabase(String[] dataValues) {

        try (PreparedStatement statement = database().prepareStatement(uploadQuery())) {
            // Set values for each column
            statement.setString(1, dataValues[0]);  // "Date"
            statement.setString(2, dataValues[1]);  // "Time"
            statement.setString(3, dataValues[2]);  // "Cylinder Head Temperature7(°C)"
            statement.setString(4, dataValues[3]);  // "Cylinder Head Temperature8(°C)"
            statement.setString(5, dataValues[4]);  // "Engine Oil Temperature(°C)"
            statement.setString(6, dataValues[5]);  // "Ambient Air Temperature(°C)"
            statement.setString(7, dataValues[6]);  // "Ambient Air Pressure(bar)"
            statement.setString(8, dataValues[7]);  // "Exhaust Air Pressure(bar)"
            statement.setString(9, dataValues[8]);  // "Engine Oil Pressure(bar)"
            statement.setString(10, dataValues[9]);  // "Battery Voltage(V)"
            statement.setString(11, dataValues[10]);  // "Engine Speed(RPM)"
            statement.setString(12, dataValues[11]);  // "Turbocharger Speed(RPM)"
            statement.setString(13, dataValues[12]);  // "Cooling Fan Speed(RPM)"
            statement.setString(14, dataValues[13]);  // "Vehicle Speed(RPM)"
            statement.setString(15, dataValues[14]);  // "Alternator Speed(RPM)"
            statement.setString(16, dataValues[15]);  // "Mean Sea Level Altitude(Mtr)"
            statement.setString(17, dataValues[16]);  // "Exhaust Brake Status"
            statement.setString(18, dataValues[17]);  // "Cooling Valve Status"
            statement.setString(19, dataValues[18]);  // "Fuel Sol Valve Status"
            statement.setString(20, dataValues[19]);  // "Heater Sol Status"
            statement.setString(21, dataValues[20]);  // "Engine Started Count"
            statement.setString(22, dataValues[21]);  // "Engine Overspeed Count"
            statement.setString(23, dataValues[22]);  // "Engine Overheat Count"
            statement.setString(24, dataValues[23]);  // "Vehicle Overspeed Count"
            statement.setString(25, getVehMN());      // "Vehicle Model No."
            statement.setString(26, getVehEN());      // "Vehicle Engine No."
            statement.setString(27, getEcuSN());      // "Erch Ecu No."

            // Execute the insert query
            statement.executeUpdate();

            log.info("Inserted {} Records into Database", dataCounter);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int fetchAllData() {
        errorDatabase();

        totalDeletedCount += deletedRecords.get();


        column_Names = getColumnNames().toArray(new String[0]);


        dataEntries.clear();


        DataEntry.slNoCounter = 1; // Reset the slNoCounter
        int recordsDownloaded = 0;


        try (Connection connection = DriverManager.getConnection(getDatabaseURL())) {

            try (PreparedStatement statement = connection.prepareStatement(fetchAllQuery())) {
//                statement.setString(1, getVehMN());
//                statement.setString(2, getVehEN());
//                statement.setString(3, getEcuSN());

                statement.setString(1, "VMS15680");
                statement.setString(2, "98765432");
                statement.setString(3, "12345A88");

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                    String[] rowData = new String[column_Names.length];
                    for (int i = 1; i <= column_Names.length; i++) {

                        if (resultSet.getMetaData().getColumnLabel(i).equalsIgnoreCase("Date")) {
                            LocalDate date = LocalDate.parse(resultSet.getString(i));
                            rowData[i - 1] = date.format(dateformatter);
                        } else {
                            rowData[i - 1] = resultSet.getString(i);
                        }

                    }
                    Common.DataEntry entry = new Common.DataEntry(rowData);
                    dataEntries.add(entry);
                    recordsDownloaded++;
                }
                tableView.setItems(dataEntries); // Update the TableView with fetched data

            }
        }}catch (SQLException e) {
            e.printStackTrace();
        }


        return recordsDownloaded;
    }


    public void errorDatabase() {
        try (PreparedStatement statement = database().prepareStatement(errorQuery())) {
            deletedRecords.set(statement.executeUpdate());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void uLoading(String content) {

        loading.setTitle("UPLOAD");
        loading.setHeaderText(null);

        // Set the owner only if it hasn't been set before
        if (loading.getOwner() == null) {
            loading.initOwner(logStage);
            Stage loadingStage = (Stage) loading.getDialogPane().getScene().getWindow();
            loadingStage.getIcons().add(erchIcon);
        }
        // Create a VBox to arrange the ProgressIndicator and Label vertically
        VBox vbox = new VBox(10); // 10 is the spacing between the children
        vbox.setPadding(new Insets(20));

        // Create a ProgressIndicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        // Adjust the duration of the animation
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setRotate(360); // Ensure the indicator rotates continuously
        progressIndicator.setStyle("-fx-progress-color: green;");

        vbox.setPrefWidth(300);

        // Create a Label for text
        Label label = new Label(content); // Use the provided content

        // Center align the content within the VBox
        vbox.setAlignment(Pos.CENTER);

        // Add the ProgressIndicator and Label to the VBox
        vbox.getChildren().addAll(progressIndicator, label);

        // Set the VBox as the content of the dialog
        loading.getDialogPane().setContent(vbox);

        // Show the dialog
        loading.show();

    }


    private void closeLoadingDialog() {
        Platform.runLater(() -> {
            loading.setResult(ButtonType.CLOSE);
            loading.close();
        });
    }

}
