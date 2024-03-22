package desktop.app.erch.Nonc;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Connection.Connect;
import desktop.app.erch.Helper.Receiver;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static desktop.app.erch.Helper.CRC.calculateCRCHex;
import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bNoncRead;

public class WriteNonc {

    Dialog<ButtonType> writeDialog;
    String data;
    Logger log = LogManager.getLogger(WriteNonc.class);

    String title = "Write Nonc";



    public void displayWriteNonc(SerialPort selectedPort , Stage parent){
        /***
         displayWriteNonc displays the dialog where Nonc Configuration can be set
         */

        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened)
            {

                Receiver nonc = new Receiver();
                data = nonc.receiveFrame(selectedPort, "Read Nonc", "a13", 14, bNoncRead(), log);

                writeDialog = new Dialog<>();
                writeDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                        ("/desktop/app/erch/css/RTC.css")).toExternalForm());

                writeDialog.setTitle("Nonc Configuration");

                // Set the stage as a modal window
                writeDialog.initModality(Modality.WINDOW_MODAL);
                writeDialog.initOwner(parent);

                // Add icon
                Stage noncStage = (Stage) writeDialog.getDialogPane().getScene().getWindow();
                noncStage.getIcons().add(erchIcon);

                Connect ecu = new Connect();
                String sn = ecu.getEcuSN();
                String vmn = ecu.getVehMN();
                log.info(sn,vmn);

                Label apLabel =   new Label("• Accelerator Pedal   :");
                Label blLabel =   new Label("• Brake Lever             :");
                Label cpLabel =   new Label("• Clutch Pedal            :");
                Label haLabel =   new Label("• High Altitude          :");

                Label vehLabel =  new Label("Vehicle Model");
                Label ecuLabel =  new Label("ERCH ECU Serial No.");


                Label parameter =  new Label();
                Text para= new Text("  Parameter");
                para.setStyle("-fx-font-size: 25;-fx-fill: white;");
                parameter.setGraphic(new Group(para));

                Label Signal =     new Label();
                Text sig = new Text("Signal Level");
                sig.setStyle("-fx-font-size: 25;-fx-fill: white;");
                Signal.setGraphic(new Group(sig));


                TextField[] field = new TextField[4];
                ComboBox<String>[] combo = new ComboBox[4];
                for(int i=0; i<4; i++){
                    field[i] = new TextField();
                    if(i>1){
                        field[i].setPrefWidth(100);
                    }else {
                        field[i].setPrefWidth(10);
                    }
                    field[i].setEditable(false);

                    combo[i] = new ComboBox<>();
                    combo[i].getItems().addAll("Ground","24V");
                }

                field[0].setText(sn);
                field[1].setText(vmn);

                String[] rOut =  new String[4];

                for (int j=0 ; j<4 ; j++){
                    rOut[j] = data.substring(j,j+1);
                    combo[j].setValue("0".equals(rOut[j]) ? "Ground" : "24V");
                }


                // Add labels to a GridPane
                GridPane grid = new GridPane();

                grid.setHgap(15);
                grid.setVgap(10); // Adjust vertical gap
                grid.setAlignment(Pos.CENTER);

                grid.add(ecuLabel, 0, 0);
                grid.add(field[0], 0, 1);
                grid.add(vehLabel, 1, 0);
                grid.add(field[1], 1, 1);
                grid.add(parameter, 0, 3);
                grid.add(Signal, 1, 3);
                grid.add(apLabel, 0, 4);
                grid.add(combo[0], 1, 4);
                grid.add(blLabel, 0, 5);
                grid.add(combo[1], 1, 5);
                grid.add(cpLabel, 0, 6);
                grid.add(combo[2], 1, 6);
                grid.add(haLabel, 0, 7);
                grid.add(combo[3], 1, 7);


                // Define the changeButtonType
                ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);

                // Add the changeButtonType to the dialog pane
                writeDialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);
                // Define the default button type
                ButtonType defaultButtonType = new ButtonType("Default", ButtonBar.ButtonData.LEFT);

                // Add the default button type to the dialog pane
                writeDialog.getDialogPane().getButtonTypes().add(defaultButtonType);

                // Get the default button node
                Node defaultButton = writeDialog.getDialogPane().lookupButton(defaultButtonType);

                // Add an event handler to the default button
                defaultButton.addEventFilter(ActionEvent.ACTION, event -> {
                    // Set every ComboBox value to "Ground"
                    combo[0].setValue("Ground");
                    combo[1].setValue("Ground");
                    combo[2].setValue("Ground");
                    combo[3].setValue("Ground");

                    // Consume the event to prevent the dialog from closing
                    event.consume();
                });


                // Set the result converter to return the entered command
                writeDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == changeButtonType) {

                        String[] signal = new String[4];
                        String[] out = new String[4];

                        for (int i=0; i<4 ; i++){
                            signal[i] = combo[i].getValue();
                            out[i] = ("Ground".equals(signal[i]) ? "0" : "1");
                        }

                        String output = out[0] + out[1] + out[2] + out[3] ;

                        int samplingBytes = output.getBytes().length;
                        String formattedSamplingBytes = String.format("%02d", samplingBytes);
                        String concat = "b14" + formattedSamplingBytes + output;

                        // Convert the string to bytes using the platform's default charset
                        byte[] outputBytes = concat.getBytes();

                        // Calculate the CRC for the bytes
                        String crcHex = calculateCRCHex(outputBytes);

                        String finalFrame = concat + crcHex;
                        log.info("Nonc Write frame : {}", finalFrame);

                        //calculateCRC
                        String wData = nonc.receiveFrame(selectedPort,"Write Nonc","a14",
                                12,finalFrame,log);


                        if(wData.equals("OK")) {
                            selectedPort.closePort();
                            if(writeDialog.isShowing()) {
                                writeDialog.setResult(ButtonType.CLOSE);
                            }
                            sof("NO / NC Configuration", "NO / NC Configuration modified Successfully", true);
                        }
                        else {
                            fatal(title,log);
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
