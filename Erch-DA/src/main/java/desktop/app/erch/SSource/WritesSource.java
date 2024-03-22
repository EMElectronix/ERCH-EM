package desktop.app.erch.SSource;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static desktop.app.erch.Helper.CRC.calculateCRCHex;
import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bPprRead;
import static desktop.app.erch.Helper.Frames.bssRead;

public class WritesSource {

    Dialog<ButtonType> writeDialog;
    String data;
    Logger log = LogManager.getLogger(WritesSource.class);
    
    String mag = "Magnetic Pickup";
    
    String alt = "Alternator";
    
    String gps = "GPS Receiver";

    String eng;
    String vs;
    
    
    public void displayWritesSource(SerialPort selectedPort , Stage parent){
        /***
         displayWritesSource displays the dialog where Signal Source is set for Engine RPM & Vehicle Speed
         */

        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened)
            {

                Receiver ss = new Receiver();
                data = ss.receiveFrame(selectedPort, "Read Signal Source", "a20", 12, bssRead(), log);

                writeDialog = new Dialog<>();
                writeDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                        ("/desktop/app/erch/css/RTC.css")).toExternalForm());

                writeDialog.setTitle("Signal source for Engine RPM & Vehicle Speed");

                // Set the stage as a modal window
                writeDialog.initModality(Modality.WINDOW_MODAL);
                writeDialog.initOwner(parent);

                // Add icon
                Stage pprStage = (Stage) writeDialog.getDialogPane().getScene().getWindow();
                pprStage.getIcons().add(erchIcon);

                Label engLabel = new Label("Engine Speed   :");
                Label vsLabel =  new Label("Vehicle Speed  :");

                String engRPM = data.startsWith("0") ? mag : alt;
                String vsRPM  = data.endsWith("0")   ? mag : gps;

                ComboBox<String> engComboBox = new ComboBox<>();
                engComboBox.getItems().addAll(mag,alt);
                engComboBox.setValue(engRPM);

                ComboBox<String> vsComboBox = new ComboBox<>();
                vsComboBox.getItems().addAll(mag,gps);
                vsComboBox.setValue(vsRPM);

                // Add labels to a grid
                GridPane grid = new GridPane();
                grid.setHgap(15);
                grid.setVgap(10); // Adjust vertical gap
                grid.setAlignment(Pos.CENTER);

                grid.add(engLabel, 0, 0);
                grid.add(engComboBox, 1, 0);
                grid.add(vsLabel, 0, 1);
                grid.add(vsComboBox, 1, 1);

                // Define the changeButtonType
                ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);

                // Add the changeButtonType to the dialog pane
                writeDialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

                // Set the result converter to return the entered command
                writeDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == changeButtonType) {
                        String engrpm = engComboBox.getValue();
                        String vsrpm = vsComboBox.getValue();

                        if(mag.equals(engrpm)) {
                            eng = "0";
                        }else{
                            eng = "1";
                        }

                        if(mag.equals(vsrpm)) {
                            vs = "0";
                        }else{
                            vs = "1";
                        }

                        String output = eng + vs;

                        int samplingBytes = output.getBytes().length;
                        String formattedSamplingBytes = String.format("%02d", samplingBytes);
                        String concat = "b19" + formattedSamplingBytes + output;

                        // Convert the string to bytes using the platform's default charset
                        byte[] outputBytes = concat.getBytes();

                        // Calculate the CRC for the bytes
                        String crcHex = calculateCRCHex(outputBytes);

                        String finalFrame = concat + crcHex;
                        log.info("Signal Source Write frame : {}", finalFrame);

                        //calculateCRC
                        String wData = ss.receiveFrame(selectedPort,"Signal Source write","a19",
                                12,finalFrame,log);


                        if(wData.equals("OK")) {
                            selectedPort.closePort();
                            if(writeDialog.isShowing()) {
                                writeDialog.setResult(ButtonType.CLOSE);
                            }
                            sof("Signal source",
                                    "Signal source for Engine RPM & Vehicle Speed modified Successfully", true);
                        }
                        else {
                            fatal("Signal Source Write",log);
                        }
                    }

                    return null;
                });


                // Set the dialog content and show the dialog
                writeDialog.getDialogPane().setContent(grid);
                writeDialog.showAndWait();


            } else {
                failed(log);
            }

        } catch (Exception e) {
            error(e,log);
        }



    }



}
