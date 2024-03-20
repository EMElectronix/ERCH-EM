package desktop.app.erch.PPRVS;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import desktop.app.erch.Sampling.WriteSampling;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static desktop.app.erch.Helper.CRC.calculateCRCHex;
import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bPprRead;

public class WritePPR {

    Dialog<ButtonType> writeDialog;

    String pprWrite = "Write Pulse per Revolution";

    String data;
    Logger log = LogManager.getLogger(WritePPR.class);


    public void displayWritePPR(SerialPort selectedPort , Stage parent){
        /***
         displayWritePPR displays the dialog where Pulse per Revolution can be Set for various Speeds
         */
        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened)
            {

                writeDialog = new Dialog<>();
                writeDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                        ("/desktop/app/erch/css/RTC.css")).toExternalForm());

                writeDialog.setTitle("Pulse per Revolution for Various Speeds");

                // Set the stage as a modal window
                writeDialog.initModality(Modality.WINDOW_MODAL);
                writeDialog.initOwner(parent);

                // Add icon
                Stage pprStage = (Stage) writeDialog.getDialogPane().getScene().getWindow();
                pprStage.getIcons().add(erchIcon);

                Label magneticLabel   = new Label("Engine RPM (Magnetic Pickup)       ");
                Label alternatorLabel = new Label("Engine RPM (Alternator Signal)     ");
                Label vehicleLabel    = new Label("Vehicle Speed (Magnetic Pickup)    ");
                Label turboLabel      = new Label("Turbocharger Speed                 ");
                Label coolingLabel    = new Label("Cooling Fan Speed                  ");

                Tooltip fillTooltip = new Tooltip("Please Fill");
                fillTooltip.setShowDelay(Duration.ZERO);

                Spinner<Integer>[] spinner = new Spinner[5];

                for(int i=0;i<5;i++){
                    spinner[i] = new Spinner<>();
                    spinner[i].setPrefWidth(70);
                    spinner[i].setEditable(true);
                    spinner[i].setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99));
                    int I = i;
                    spinner[i].getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0,
                            change -> {
                                String newText = change.getControlNewText();
                                if (newText.matches("\\d*") && newText.length() <= 2 && newText.length() > 0) {
                                    return change;
                                }
                                else {
                                    Tooltip.install(spinner[I], fillTooltip);
                                    return null;
                                }
                            }));

                }


                Receiver ppr = new Receiver();
                data = ppr.receiveFrame(selectedPort, "Read Pulse Per Revolution",
                        "a16", 20, bPprRead(), log);

                String magnetic = data.substring(0, 2);
                String alternator = data.substring(2, 4);
                String vehicle  = data.substring(4, 6);
                String turbocharger = data.substring(6, 8);
                String cooling = data.substring(8, 10);


                spinner[0].getValueFactory().setValue(Integer.parseInt(magnetic));
                spinner[1].getValueFactory().setValue(Integer.parseInt(alternator));
                spinner[2].getValueFactory().setValue(Integer.parseInt(vehicle));
                spinner[3].getValueFactory().setValue(Integer.parseInt(turbocharger));
                spinner[4].getValueFactory().setValue(Integer.parseInt(cooling));


                // Add labels to a GridPane
                GridPane gridPane = new GridPane();

                gridPane.setHgap(15);
                gridPane.setVgap(10); // Adjust vertical gap
                gridPane.setAlignment(Pos.CENTER);

                gridPane.add(magneticLabel, 0, 0);
                gridPane.add(spinner[0], 1, 0);
                gridPane.add(alternatorLabel, 0, 1);
                gridPane.add(spinner[1], 1, 1);
                gridPane.add(vehicleLabel, 0, 2);
                gridPane.add(spinner[2], 1, 2);
                gridPane.add(turboLabel, 0, 3);
                gridPane.add(spinner[3], 1, 3);
                gridPane.add(coolingLabel, 0, 4);
                gridPane.add(spinner[4], 1, 4);

                // Define the changeButtonType
                ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);

                // Add the changeButtonType to the dialog pane
                writeDialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

                // Create BooleanBinding to represent whether the OK button should be disabled
                BooleanBinding isNotFilled = Bindings.createBooleanBinding(
                        () -> spinner[0].getEditor().getText().isEmpty() || spinner[1].getEditor().getText().isEmpty(),
                        spinner[0].getEditor().textProperty(), spinner[1].getEditor().textProperty()
                );

                // Bind to disable property of the OK button to the isNotFilled property
                writeDialog.getDialogPane().lookupButton(changeButtonType).disableProperty().bind(isNotFilled);

                // Set the result converter to return the entered command
                writeDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == changeButtonType) {
                        String mag = padWithZeros(spinner[0].getEditor().getText(), 2);
                        String alt = padWithZeros(spinner[1].getEditor().getText(), 2);
                        String veh = padWithZeros(spinner[2].getEditor().getText(), 2);
                        String trb = padWithZeros(spinner[3].getEditor().getText(), 2);
                        String clg = padWithZeros(spinner[4].getEditor().getText(), 2);


                        String output = mag + alt + veh + trb + clg;

                        int samplingBytes = output.getBytes().length;
                        String formattedSamplingBytes = String.format("%02d", samplingBytes);
                        String concat = "b15" + formattedSamplingBytes + output;

                        // Convert the string to bytes using the platform's default charset
                        byte[] outputBytes = concat.getBytes();

                        // Calculate the CRC for the bytes
                        String crcHex = calculateCRCHex(outputBytes);

                        String finalFrame = concat + crcHex;
                        log.info("Sampling Write frame : {}", finalFrame);

                        //calculateCRC
                        String wData = ppr.receiveFrame(selectedPort,"Write Pulse Per Revolution","a15",
                                12,finalFrame,log);

                        if(wData.equals("OK")) {
                            selectedPort.closePort();
                            if(writeDialog.isShowing()) {
                                writeDialog.setResult(ButtonType.CLOSE);
                            }
                            sof("Pulse Per Response", "Various Speeds Modified Successfully", true);
                        }
                        else {
                            fatal("Write Sampling Rate",log);
                        }
                    }

                    return null;
                });


                // Set the dialog content and show the dialog
                writeDialog.getDialogPane().setContent(gridPane);
                writeDialog.showAndWait();


            } else {
            failed(log);
            }

            } catch (Exception e) {
                error(e,log);
            }








    }




}
