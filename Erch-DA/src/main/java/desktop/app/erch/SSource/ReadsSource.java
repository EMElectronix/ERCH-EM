package desktop.app.erch.SSource;

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
import static desktop.app.erch.Helper.Frames.*;

public class ReadsSource extends Dialog<String> {

    String data;

    String title = "Read Signal Source";

    public ReadsSource(SerialPort selectedPort, Stage parent, Logger log){

        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened)
            {
                Receiver ss = new Receiver();
                data = ss.receiveFrame(selectedPort, title, "a20", 12, bssRead(), log);
                if (data != null) {
                    getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                            ("/desktop/app/erch/css/RTC.css")).toExternalForm());
                    setTitle("Signal Source for Eng. RPM & Vehicle Speed");

                    // Set the stage as a modal window
                    initModality(Modality.WINDOW_MODAL);
                    initOwner(parent);

                    String engRPM = data.startsWith("0") ? "Magnetic Pickup" : "Alternator";
                    String vsRPM = data.endsWith("0") ? "Magnetic Pickup" : "Alternator";

                    TextField[] field = new TextField[2];

                    for(int i=0; i<2; i++){
                        field[i] = new TextField();
                        field[i].setPrefWidth(120);
                        field[i].setEditable(false);

                    }

                    field[0].setText(engRPM);
                    field[1].setText(vsRPM);

                    Label engLabel = new Label("Engine Speed   :");
                    Label vsLabel =  new Label("Vehicle Speed  :");
                    

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
                    grid.add(engLabel, 0, 0);
                    grid.add(field[0], 1, 0);
                    grid.add(vsLabel, 0, 1);
                    grid.add(field[1], 1, 1);

                    // Add the grid to the dialog pane
                    getDialogPane().setContent(grid);
                    // Set the size of the dialog pane
                    getDialogPane().setPrefSize(450, 100);

                    // Add a close request event handler to handle "X" button click
                    Stage ssStage = (Stage) getDialogPane().getScene().getWindow();
                    ssStage.setOnCloseRequest(event -> {
                        String result = "ok";
                        close(); // Close the current dialog
                        setResult(result);
                    });
                    
                

                }else {
                        fatal(title,log);
                    }


            }else {
                failed(log);
            }
            } catch (Exception e) {
                error(e,log);
            }


            }


}
