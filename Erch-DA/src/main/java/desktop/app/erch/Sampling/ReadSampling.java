package desktop.app.erch.Sampling;

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
import static desktop.app.erch.Helper.Frames.bSamplingRead;

public class ReadSampling extends Dialog<String> {

   String data;

   String title = "Read Sampling Rate";

   public ReadSampling(SerialPort selectedPort, Stage parent, Logger log){
                /***
                 ReadSampling is used to read the Sampling Rate from erch ECU
                 */

         try {
               boolean portOpened = selectedPort.openPort();
               if (portOpened)
               {
                   Receiver sr = new Receiver();
                   data = sr.receiveFrame(selectedPort, title, "a18",
                           15,bSamplingRead(), log);
                       if (data != null) {

                               getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                                       ("/desktop/app/erch/css/RTC.css")).toExternalForm());
                               setTitle(title);

                               // Set the stage as a modal window
                               initModality(Modality.WINDOW_MODAL);
                               initOwner(parent);

                               String normal = removeLeadingZeros(data.substring(0, 3));
                               String abnormal = removeLeadingZeros(data.substring(3, 5));

                               Label NCLabel = new Label("During Normal Condition     : ");
                               Label ACLabel = new Label("During Abnormal Condition : ");

                               TextField[] field = new TextField[2];
                               Label[] sec = new Label[2];

                               for(int i=0;i<2;i++){
                                       field[i] = new TextField();
                                       field[i].setPrefWidth(40);
                                       field[i].setEditable(false);
                                       sec[i] = new Label("sec");
                               }
                               field[0].setText(normal);
                               field[1].setText(abnormal);

                               // Add labels to a GridPane
                               GridPane gridPane = new GridPane();
                               gridPane.setHgap(15);
                               gridPane.setVgap(10); // Adjust vertical gap
                               gridPane.setAlignment(Pos.CENTER);


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

                               gridPane.add(NCLabel, 0, 0);
                               gridPane.add(field[0], 1, 0);
                               gridPane.add(sec[0], 2, 0);
                               gridPane.add(ACLabel, 0, 1);
                               gridPane.add(field[1], 1, 1);
                               gridPane.add(sec[1], 2, 1);

                               // Add the grid to the dialog pane
                               getDialogPane().setContent(gridPane);

                           getDialogPane().setPrefSize(450, 100);

                           // Add a close request event handler to handle "X" button click
                           Stage srStage = (Stage) getDialogPane().getScene().getWindow();
                           srStage.setOnCloseRequest(event -> {
                               selectedPort.closePort();
                               String result = "ok";
                               close(); // Close the current dialog
                               setResult(result);
                           });


                       }
                       else {
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
