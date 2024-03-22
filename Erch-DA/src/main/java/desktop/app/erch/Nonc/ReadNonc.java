package desktop.app.erch.Nonc;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Connection.Connect;
import desktop.app.erch.Helper.Receiver;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Frames.bNoncRead;

public class ReadNonc extends Dialog<String> {

    String data;

    String title = "Read Nonc";

    public ReadNonc(SerialPort selectedPort, Stage parent, Logger log) {

        /***
         ReadNonc is used to read the Nonc Configuration
         */

        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened) {
                Receiver nonc = new Receiver();
                data = nonc.receiveFrame(selectedPort, title, "a13", 14, bNoncRead(), log);
                if (data != null) {
                    getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                            ("/desktop/app/erch/css/RTC.css")).toExternalForm());
                    setTitle("Nonc Configuration");

                    // Set the stage as a modal window
                    initModality(Modality.WINDOW_MODAL);
                    initOwner(parent);

                    String aP = data.substring(0, 1);
                    String bL = data.substring(1, 2);
                    String cP = data.substring(2, 3);
                    String hA = data.substring(3, 4);


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
                    
                    
                    TextField[] field = new TextField[6];
                    for(int i=0; i<6; i++){
                        field[i] = new TextField();
                        if(i>1){
                            field[i].setPrefWidth(100); 
                        }else {
                            field[i].setPrefWidth(10);
                        }
                        field[i].setEditable(false);
                    }
                    
                    field[0].setText(sn);
                    field[1].setText(vmn);
                    field[2].setText("0".equals(aP) ? "Ground" : "24V");
                    field[3].setText("0".equals(bL) ? "Ground" : "24V");
                    field[4].setText("0".equals(cP) ? "Ground" : "24V");
                    field[5].setText("0".equals(hA) ? "Ground" : "24V");
                    
                    
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

                    grid.add(ecuLabel, 0, 0);
                    grid.add(field[0], 0, 1);
                    grid.add(vehLabel, 1, 0);
                    grid.add(field[1], 1, 1);
                    grid.add(parameter, 0, 3);
                    grid.add(Signal, 1, 3);
                    grid.add(apLabel, 0, 4);
                    grid.add(field[2], 1, 4);
                    grid.add(blLabel, 0, 5);
                    grid.add(field[3], 1, 5);
                    grid.add(cpLabel, 0, 6);
                    grid.add(field[4], 1, 6);
                    grid.add(haLabel, 0, 7);
                    grid.add(field[5], 1, 7);


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
