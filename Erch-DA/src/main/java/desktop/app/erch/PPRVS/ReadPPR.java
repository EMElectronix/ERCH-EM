package desktop.app.erch.PPRVS;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Frames.bPprRead;
import static desktop.app.erch.Helper.Frames.bRTCread;

public class ReadPPR extends Dialog<String> {

    String data;

    public ReadPPR(SerialPort selectedPort, Stage parent, Logger log) {
        /***
         ReadPPR is used to read the pulse per revolution from various speeds from erch ECU
         */

        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened) {
                Receiver ppr = new Receiver();
                data = ppr.receiveFrame(selectedPort, "Read Pulse Per Revolution",
                        "a16", 20, bPprRead(), log);
                if (data != null) {

                    getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                            ("/desktop/app/erch/css/RTC.css")).toExternalForm());
                    setTitle("Pulse per Revolution for Various Speeds");

                    // Set the stage as a modal window
                    initModality(Modality.WINDOW_MODAL);
                    initOwner(parent);

                    String magnetic = data.substring(0, 2);
                    String alternator = data.substring(2, 4);
                    String vehicle  = data.substring(4, 6);
                    String turbocharger = data.substring(6, 8);
                    String cooling = data.substring(8, 10);

                    TextField[] field = new TextField[5];

                    for(int i=0;i<5;i++){
                        field[i] = new TextField();
                        field[i].setPrefWidth(40);
                        field[i].setEditable(false);
                    }

                    field[0].setText(magnetic);
                    field[1].setText(alternator);
                    field[2].setText(vehicle);
                    field[3].setText(turbocharger);
                    field[4].setText(cooling);

                    Label magneticLabel   = new Label("Engine RPM (Magnetic Pickup)       ");
                    Label alternatorLabel = new Label("Engine RPM (Alternator Signal)     ");
                    Label vehicleLabel    = new Label("Vehicle Speed (Magnetic Pickup)    ");
                    Label turboLabel      = new Label("Turbocharger Speed                 ");
                    Label coolingLabel    = new Label("Cooling Fan Speed                  ");


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



                    // Add labels to a GridPane
                    GridPane grid = new GridPane();

                    grid.setHgap(15);
                    grid.setVgap(10); // Adjust vertical gap
                    grid.setAlignment(Pos.CENTER);

                    // Add the grid to the dialog pane
                    getDialogPane().setContent(grid);

                    grid.add(magneticLabel, 0, 0);
                    grid.add(field[0], 1, 0);
                    grid.add(alternatorLabel, 0, 1);
                    grid.add(field[1], 1, 1);
                    grid.add(vehicleLabel, 0, 2);
                    grid.add(field[2] , 1, 2);
                    grid.add(turboLabel, 0, 3);
                    grid.add(field[3], 1, 3);
                    grid.add(coolingLabel, 0, 4);
                    grid.add(field[4], 1, 4);

                    // Add the grid to the dialog pane
                    getDialogPane().setContent(grid);
                    // Set the size of the dialog pane
                    getDialogPane().setPrefSize(450, 100);

                    // Add a close request event handler to handle "X" button click
                    Stage pprStage = (Stage) getDialogPane().getScene().getWindow();
                    pprStage.setOnCloseRequest(event -> {
                        String result = "ok";
                        close(); // Close the current dialog
                        setResult(result);
                    });


                } else {
                    fatal("Read Pulse Per Revolution",log);
                }

            }
            else {
                    failed(log);
             }
            } catch (Exception e) {
                    error(e,log);
             }


    }



}
