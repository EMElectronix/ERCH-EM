package desktop.app.erch.Sampling;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Connection.Comport;
import desktop.app.erch.Helper.Receiver;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import static desktop.app.erch.Helper.Frames.bSamplingRead;

public class WriteSampling {


        Dialog<ButtonType> writeDialog;

        String srWrite = "Write Sampling Rate";

        Logger log = LogManager.getLogger(WriteSampling.class);

        public void displayWriteSampling(SerialPort selectedPort , Stage parent){
                /***
                 displayWriteSampling displays the dialog where Sampling Rate can be Set
                 */

            try {
                boolean portOpened = selectedPort.openPort();
                if (portOpened)
                {

                writeDialog = new Dialog<>();
                writeDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                        ("/desktop/app/erch/css/RTC.css")).toExternalForm());

                writeDialog.setTitle("Change Sampling Rate");

                // Set the stage as a modal window
                writeDialog.initModality(Modality.WINDOW_MODAL);
                writeDialog.initOwner(parent);

                // Add icon
                Stage srStage = (Stage) writeDialog.getDialogPane().getScene().getWindow();
                srStage.getIcons().add(erchIcon);

                Label headerLabel = new Label("Change Sampling Rate");
                headerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 17;");
                headerLabel.setAlignment(Pos.CENTER);
                writeDialog.getDialogPane().setHeader(headerLabel);


                Label NCLabel = new Label("During Normal Condition     : ");
                Label ACLabel = new Label("During Abnormal Condition : ");

                Tooltip fillTooltip = new Tooltip("Please Fill");
                fillTooltip.setShowDelay(Duration.ZERO);

                Spinner<Integer>[] spinner = new Spinner[2];

                Label[] sec = new Label[2];

                for(int i=0;i<2;i++){
                        sec[i] = new Label("sec");
                        spinner[i] = new Spinner<>();
                        spinner[i].setPrefWidth(70);
                        spinner[i].setEditable(true);
                         int I = i;
                        spinner[i].getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0,
                            change -> {
                                String newText = change.getControlNewText();
                                if (newText.matches("\\d*") && newText.length() <= (3-I) && newText.length() > 0) {
                                    return change;
                                }
                                else {
                                    Tooltip.install(spinner[I], fillTooltip);
                                    return null;
                                }
                            }));

                }

                spinner[0].setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999));
                spinner[1].setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99));


                // Add labels to a GridPane
                GridPane grid = new GridPane();
                grid.setHgap(15);
                grid.setVgap(10); // Adjust vertical gap
                grid.setAlignment(Pos.CENTER);

                grid.add(NCLabel, 0, 0);
                grid.add(spinner[0], 1, 0);
                grid.add(sec[0], 2, 0);
                grid.add(ACLabel, 0, 1);
                grid.add(spinner[1], 1, 1);
                grid.add(sec[1], 2, 1);


                      Receiver sr = new Receiver();

                      String rData = sr.receiveFrame(selectedPort, srWrite, "a18", 15,bSamplingRead(), log);


                          log.info("data : {}",rData);

                             String normal = removeLeadingZeros(rData.substring(0, 3));
                             String abnormal = removeLeadingZeros(rData.substring(3, 5));

                             spinner[0].getValueFactory().setValue(Integer.parseInt(normal));
                             spinner[1].getValueFactory().setValue(Integer.parseInt(abnormal));

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
                                 String newNormal = padWithZeros(spinner[0].getEditor().getText(), 3);
                                 String newAbnormal = padWithZeros(spinner[1].getEditor().getText(), 2);

                                 String output = newNormal + newAbnormal;

                                 int samplingBytes = output.getBytes().length;
                                 String formattedSamplingBytes = String.format("%02d", samplingBytes);
                                 String concat = "b17" + formattedSamplingBytes + output;

                                 // Convert the string to bytes using the platform's default charset
                                 byte[] outputBytes = concat.getBytes();

                                 // Calculate the CRC for the bytes
                                 String crcHex = calculateCRCHex(outputBytes);

                                 String finalFrame = concat + crcHex;
                                 log.info("Sampling Write frame : {}", finalFrame);

                                 //calculateCRC
                                 String wData = sr.receiveFrame(selectedPort,"Write Sampling Rate","a17",
                                         12,finalFrame,log);

                                 if(wData.equals("OK")) {
                                     selectedPort.closePort();
                                     if(writeDialog.isShowing()) {
                                         writeDialog.setResult(ButtonType.CLOSE);
                                     }
                                     sof("Sampling Rate", "NORMAL CONDITION : "+ newNormal + "sec " +
                                             "\nAbnormal Condition : "+ newAbnormal +"sec \n changed Successfully", true);
                                 }
                                 else {
                                     fatal("Write Sampling Rate",log);
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
