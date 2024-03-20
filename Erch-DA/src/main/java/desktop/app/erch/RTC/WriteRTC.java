package desktop.app.erch.RTC;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.CustomTimePicker;
import desktop.app.erch.Helper.Receiver;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import static desktop.app.erch.Helper.CRC.calculateCRCHex;
import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Display.sof;


public class WriteRTC {
    Dialog<ButtonType> writeDialog;

    Logger log = LogManager.getLogger(WriteRTC.class);

    public void displayWriteRTC(SerialPort selectedPort , Stage parent){
        /***
         displayWriteRTC displays the dialog where Date and Time can be Set
         */

        writeDialog = new Dialog<>();
        writeDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                ("/desktop/app/erch/css/RTC.css")).toExternalForm());

        writeDialog.setTitle("Set Date and Time");

        // Set the stage as a modal window
        writeDialog.initModality(Modality.WINDOW_MODAL);
        writeDialog.initOwner(parent);

        // Add icon
        Stage rtcStage = (Stage) writeDialog.getDialogPane().getScene().getWindow();
        rtcStage.getIcons().add(erchIcon);

        Label headerLabel = new Label("Set Date and Time:");
        headerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 17;");
        headerLabel.setAlignment(Pos.CENTER);
        writeDialog.getDialogPane().setHeader(headerLabel);


        // Create the date picker
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        // Listener for start date changes
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // If start date is selected, restrict end date to dates on or after start date
            datePicker.setDayCellFactory(picker -> new DateCell() {

                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(date.isBefore(LocalDate.of(2000, 1, 1)));
                }
            });
        });


        // Create the custom time picker
        CustomTimePicker timePicker = new CustomTimePicker();
        timePicker.setPromptText("Hour", "Min", "Sec", "AM/PM");

        Label timeLabel = new Label(" Hours   Minutes  Seconds   AM/PM");

        writeDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node okButton = writeDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(false);

        // Set the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(datePicker, 0, 0);
        grid.add(timeLabel, 0, 1);
        grid.add(timePicker, 0, 2);


        // Set the result converter to return the entered command
        writeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                LocalDate selectedDate = datePicker.getValue();
                LocalTime selectedTime = timePicker.getSelectedTime();

                log.info("Selected Date: {} and Time : {}",selectedDate,selectedTime);

                // Check if either date or time is empty
                if (datePicker.getValue() == null || selectedTime == null) {
                    Tooltip loadTooltip = new Tooltip("Please select both date and time.");
                    loadTooltip.setX(timePicker.localToScreen(0, 0).getX());
                    loadTooltip.setY(timePicker.localToScreen(0, 0).getY() + timePicker.getHeight());
                    loadTooltip.show(writeDialog.getDialogPane().getScene().getWindow());

                    // Hide the tooltip after a certain duration if needed
                    PauseTransition pause = new PauseTransition(Duration.seconds(3)); // You can adjust the duration
                    pause.setOnFinished(event -> loadTooltip.hide());
                    pause.play();
                }
                else {

                    String formattedDate = selectedDate.format(dateFormatter);
                    String formattedTime = selectedTime.format(timeFormatter);
                    String output = formattedDate + formattedTime;
                    output = output.replace("am", "0");
                    output = output.replace("pm", "1");

                    int rtcWBytes = output.getBytes().length;
                    String concat = "b04" + rtcWBytes + output;

                    // Convert the string to bytes using the platform's default charset
                    byte[] outputBytes = concat.getBytes();

                    // Calculate the CRC for the bytes
                    String crcHex = calculateCRCHex(outputBytes);

                    String finalFrame = concat + crcHex;

                    log.info("RTC write frame : {}", finalFrame);


                    try {
                        boolean portOpened = selectedPort.openPort();
                        if(portOpened){
                            Receiver rtc = new Receiver();
                            String data = rtc.receiveFrame(selectedPort,"RTC Write","a04",
                                    12,finalFrame,log);

                            if(data.equals("OK")) {
                                if(writeDialog.isShowing()) {
                                    writeDialog.setResult(ButtonType.CLOSE);
                                }
                                selectedPort.closePort();
                                sof("Date and Time set Successfully", " Date: " + selectedDate.toString() +
                                        "\n And"+"\n Time: " + selectedTime.toString() +"\n RTC has been set Successfully",
                                        true);
                            }
                            else {
                                fatal("Write RTC",log);
                            }

                        } else {
                            failed(log);
                        }

                    } catch (Exception e) {
                        error(e,log);
                    }

                }


            }
            return null;
        });


        // Set the dialog content and show the dialog
        writeDialog.getDialogPane().setContent(grid);
        writeDialog.showAndWait();

    }


}
