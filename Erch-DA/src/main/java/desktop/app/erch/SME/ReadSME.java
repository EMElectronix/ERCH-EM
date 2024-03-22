package desktop.app.erch.SME;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Frames.bsmeread;

public class ReadSME extends Dialog<String> {

    String data;

    String title = "Read SME";

    public ReadSME(SerialPort selectedPort, Stage parent, Logger log) {

        /***
         ReadSME is used to read the ecu Serial No , vehicle Model No, vehicle Engine No.
         */

        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened) {
                Receiver sme = new Receiver();
                data = sme.receiveFrame(selectedPort, title, "a06", 34, bsmeread(), log);
                if (data != null) {
                    getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                            ("/desktop/app/erch/css/RTC.css")).toExternalForm());
                    setTitle("Read Vehicle Model, Engine and Serial No.");

                    // Set the stage as a modal window
                    initModality(Modality.WINDOW_MODAL);
                    initOwner(parent);

                    String serial = data.substring(0, 8);
                    String vmodel = data.substring(8, 16);
                    String engine = data.substring(16, 24);

                    // Remove leading zeros from serial and engine values
                    vmodel  = removeLeadingZeros(vmodel);
                    serial = removeLeadingZeros(serial);
                    engine = removeLeadingZeros(engine);

                    Label vmodelLabel = new Label("Vehicle Model No. : "+ vmodel);
                    Label serialLabel = new Label("ECU Serial No.        : "+ serial);
                    Label engineLabel = new Label("Engine No.              : "+ engine);


                    // Add OK button
                    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    getDialogPane().getButtonTypes().add(okButton);

                    // Add Back button
                    ButtonType backButton = new ButtonType("Back", ButtonBar.ButtonData.BACK_PREVIOUS);
                    getDialogPane().getButtonTypes().add(backButton);


                    // Set result converter for OK button
                    setResultConverter(dialogButton -> {
                        selectedPort.closePort();
                        String result;
                        if (dialogButton == okButton) {
                            result = "ok";
                            close(); // Close the current dialog
                            return result;
                        } else if (dialogButton == backButton) {
                            result = "previous";
                            close(); // Close the current dialog
                            return result;
                        }
                        return null;
                    });

                    // Add labels to a grid
                    GridPane grid = new GridPane();
                    grid.setHgap(15);
                    grid.setVgap(10); // Adjust vertical gap
                    grid.setAlignment(Pos.CENTER);

                    grid.add(vmodelLabel, 0, 0);
                    grid.add(serialLabel, 0, 1);
                    grid.add(engineLabel, 0, 2);

                    // Add the grid to the dialog pane
                    getDialogPane().setContent(grid);
                    // Set the size of the dialog pane
                    getDialogPane().setPrefSize(400, 100);

                    // Add a close request event handler to handle "X" button click
                    Stage ssStage = (Stage) getDialogPane().getScene().getWindow();
                    ssStage.setOnCloseRequest(event -> {
                        String result = "ok";
                        close(); // Close the current dialog
                        setResult(result);
                    });


                } else {
                    fatal(title, log);
                }


            } else {
                failed(log);
            }
        } catch (Exception e) {
            error(e, log);
        }

    }




}
