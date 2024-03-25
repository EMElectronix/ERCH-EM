package desktop.app.erch.Helper;

import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static desktop.app.erch.Helper.Display.sof;

public class Common {
    static String errorMessage = "Error";
    public static boolean containsSequence(byte[] response, byte[] sequence , Logger log) {
        /*
        containsSequence checks for a particular sequence of bytes
        args    : response    → the whole data
                  sequence    → particular sequence of bytes
        returns : returns true only if sequence found,
                  returns false if sequence is not found
         */

        for (int i = 0; i <= response.length - sequence.length; i++) {
            boolean found = true;
            for (int j = 0; j < sequence.length; j++) {
                if (response[i + j] != sequence[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                log.info("Sequence found");
                return true; // Sequence found
            }
        }
        return false; // Sequence not found
    }
   public static final Image erchIcon = new Image(Objects.requireNonNull(Common.class.getResourceAsStream("/desktop/app/erch/Images/app_logo.jpg")));


    public static void setBackground(Image image, BorderPane layout) {
        /*
        setBackground sets image for indexLayout
        args    : Image    → Specifies the Image to be displayed on Background
         */

        BackgroundSize backgroundSize = new BackgroundSize(
                BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);

        BackgroundImage bImg = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        Background bGround = new Background(bImg);

        layout.setBackground(bGround);
    }

    public static void setComportParameters(SerialPort serialPort){
        serialPort.setComPortParameters(9600, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 1000, 0);
    }

    public static String removeLeadingZeros(String input) {
        // Remove leading zeros from the input
        return input.replaceFirst("^0+", "");
    }

    public static String getDatabaseURL() {
        String currentWorkingDirectory = System.getProperty("user.dir");
        String forwardSlashedPath = currentWorkingDirectory.replace("\\", "/");
        String finalPath = "jdbc:sqlite:" + forwardSlashedPath + "/Database/erch.db";
        return finalPath;
    }

    public static String padWithZeros(String input, int desiredLength) {
        // If the input length is less than the desired length, pad with zeros
        while (input.length() < desiredLength) {
            input = "0" + input;
        }
        return input;
    }


    public static Connection database(){
        Connection dbConn;
        try {
            dbConn = DriverManager.getConnection(getDatabaseURL());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dbConn;
    }


    public static void error(Exception exception,Logger log){
        log.error("Error opening COM port: {}", exception.getMessage());
        sof(errorMessage, "Error opening COM port ❗", false);
    }

    public static void failed(Logger log){
        log.error("Failed to Open Comport");
        sof(errorMessage, "Failed to open COM port ❗", false);

    }



    public static void fatal(String param,Logger log){
        String failed = param+" Failed❗";
        log.fatal(failed);
        sof(errorMessage, failed, false);
    }

    public static Tooltip fill(String message){
        Tooltip fillTooltip = new Tooltip(message);
        fillTooltip.setShowDelay(Duration.ZERO);
        return fillTooltip;
    }

    public static class DataEntry {
        public static int slNoCounter = 1;  // Counter for auto-incrementing Sl No.
        private final int slNo;  // Auto-incremented Sl No.
        private final List<StringProperty> properties;

        public DataEntry(String[] dataValues) {
            slNo = slNoCounter++;
            properties = new ArrayList<>();
            for (String value : dataValues) {
                properties.add(new SimpleStringProperty(value));
            }
        }

        public int getSlNo() {
            return slNo;
        }

        public StringProperty getProperty(int index) {
            return properties.get(index);
        }
    }


    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyy");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hhmmssa");

    public static final DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void date(){
        DatePicker start = new DatePicker();
        DatePicker end = new DatePicker();

        // Listener for start date changes
        start.valueProperty().addListener((observable, oldValue, newValue) -> {
            // If start date is selected, restrict end date to dates on or after start date
            if (newValue != null) {
                end.setDayCellFactory(picker -> new DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(date.isBefore(newValue));
                    }
                });
            }
        });


        // Listener for end date changes
        end.valueProperty().addListener((observable, oldValue, newValue) -> {
            // If end date is selected, restrict start date to dates on or before end date
            if (newValue != null) {
                start.setDayCellFactory(picker -> new DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(date.isAfter(newValue));
                    }
                });
            }
        });
    }


}
