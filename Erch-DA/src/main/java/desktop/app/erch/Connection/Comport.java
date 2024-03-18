package desktop.app.erch.Connection;

import com.fazecast.jSerialComm.SerialPort;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static desktop.app.erch.Helper.Common.erchIcon;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Index.mainMenuBar;

public class Comport {
     Logger log = LogManager.getLogger(Comport.class);
    public static boolean isEcuConnected = false ;
    public static SerialPort serialPort = null;
    public Dialog<ButtonType> comDialog;

    public String selectedComPortName;

    static Connect cnt = new Connect();

    Disconnect discnt = new Disconnect();

    String disconnect = "Disconnected";

    public void displayComport(Stage index) {

         /*
        displayComport : Functionalities of Comport dialog is added
         */

        comDialog = new Dialog<>();
        comDialog.getDialogPane().getStylesheets().add(getClass().getResource("/desktop/app/erch/css/Comport.css").toExternalForm());

        comDialog.setTitle("ComPort");

        // Set the stage as a modal window
        comDialog.initModality(Modality.WINDOW_MODAL);
        comDialog.initOwner(index);

        // Add icon
        Stage comportStage = (Stage) comDialog.getDialogPane().getScene().getWindow();
        comportStage.getIcons().add(erchIcon);



        // Create ComboBox for COM Port selection
        ComboBox<String> comPortComboBox = new ComboBox<>();
        comPortComboBox.getItems().addAll(getComPorts());


        ComboBox<Integer> baudRateComboBox = new ComboBox<>();
        baudRateComboBox.getItems().add(9600);
        baudRateComboBox.setPromptText("9600");

        // Make the ComboBox non-editable
        baudRateComboBox.setEditable(false);

        // Make the ComboBox non-focusable
        baudRateComboBox.setFocusTraversable(false);

        // Make the ComboBox non-interactive (disable mouse events and keyboard input)
        baudRateComboBox.setMouseTransparent(true);

        // Disable the ComboBox dropdown
        baudRateComboBox.setOnShowing(Event::consume);

        // Set a default value (optional)
        baudRateComboBox.getSelectionModel().select(0);

        Button connectButton = new Button("Connect");
        Button disconnectButton = new Button("Disconnect");
        Button refreshButton = new Button( "   ");

        //Refresh Button is for checking the Comports
        refreshButton.getStyleClass().add("refresh-button");
        refreshButton.setOnAction(event -> {
            comPortComboBox.getItems().clear();
            comPortComboBox.getItems().addAll(getComPorts());
        });

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        comDialog.getDialogPane().getButtonTypes().addAll(cancelButtonType);
        Label baudRateLabel = new Label("Baud ");
        baudRateLabel.setStyle("-fx-font-size: 16;-fx-text-fill: white;");

        HBox baudRateHBox = new HBox(10, baudRateLabel, baudRateComboBox);
        HBox comPortHBox = new HBox(10, comPortComboBox, refreshButton);


        if(!isEcuConnected){
            // If Connection is not established, Connect button is displayed
            comDialog.getDialogPane().setContent(new VBox(10, comPortHBox, baudRateHBox, connectButton));
            connectButton.setVisible(true);
            disconnectButton.setVisible(false);
            comPortComboBox.setPromptText("Select COM Port  ");

        }
        else{
            // If Connection is established, Disconnect button is displayed
            comDialog.getDialogPane().setContent(new VBox(10, comPortComboBox, baudRateHBox, disconnectButton));
            comPortComboBox.setPromptText(cnt.getConnectedPort());
            connectButton.setVisible(false);
            disconnectButton.setVisible(true);


            // Make the ComboBox non-editable
            comPortComboBox.setEditable(false);

            // Make the ComboBox non-focusable
            comPortComboBox.setFocusTraversable(false);

            // Make the ComboBox non-interactive (disable mouse events and keyboard input)
            comPortComboBox.setMouseTransparent(true);

            // Disable the ComboBox dropdown
            comPortComboBox.setOnShowing(Event::consume);

            // Set a default value (optional)
            comPortComboBox.getSelectionModel().select(0);
        }

        comPortComboBox.setOnAction(event -> {
            // Handle the selected COM port
            selectedComPortName = comPortComboBox.getValue();
            log.info("Available COM Ports: {}", Arrays.toString(getComPorts()));
            log.info("Selected COM Port: {}", selectedComPortName);

            // Close the previous serial port if it's open
            if (serialPort != null && serialPort.isOpen()) {
                log.info("Closing previous COM port: {}",serialPort.getSystemPortName());
                serialPort.closePort();
            }

            // Find the corresponding SerialPort object based on the selected name
            SerialPort[] ports = SerialPort.getCommPorts();
            for (SerialPort port : ports) {
                if (port.getDescriptivePortName().equals(selectedComPortName)) {
                    serialPort = port;
                    break;
                }
            }

            log.info("Serial Port Object: {}",serialPort);
        });

        connectButton.setOnAction(e -> {
              //Initialize Connect
             boolean connected =  cnt.Connection(serialPort);
             if(connected){
                 mainMenuBar.getMenus().add(4,space());
                 mainMenuBar.getMenus().add(5,ecuSNMenu());
                 if(comDialog.isShowing()) {
                     comDialog.setResult(ButtonType.CLOSE);
                 }
                 isEcuConnected=true;
                 sof("Connection Established", "The connection has been established.", true);
                 serialPort.closePort();
             }

        });

        disconnectButton.setOnAction(e -> {
            if(isEcuConnected && serialPort!=null){
                boolean diconnected = discnt.disConnect(serialPort);
                mainMenuBar.getMenus().remove(5);
                mainMenuBar.getMenus().remove(4);

                if(comDialog.isShowing()) {
                    comDialog.setResult(ButtonType.CLOSE);
                }
                isEcuConnected=false;
                serialPort.closePort();
                if(diconnected){
                    sof(disconnect, "COMPORT disconnected Successfully", true);
                }else {
                    sof(disconnect, "Disconnected without ERCH Response", true);
                }


            }
            else{
                log.warn("Connection not Established");
                sof(disconnect, "Connection not established ", false);

            }


        });

        comDialog.showAndWait();

    }

    public String[] getComPorts() {
        /*
        getComPorts returns all the available comports which is stored in an array
         */
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] portNames = new String[ports.length];

        for (int i = 0; i < ports.length; i++) {
            portNames[i] = ports[i].getDescriptivePortName();
        }
        return portNames;
    }

    public static SerialPort getConnectedPort(){
        return serialPort;
    }


    public  Menu space() {
        /*
         space Menu is used to add space between all the other Menus and ecuSN menu
         */
        Menu spacerMenu = new Menu();
        Label spacerLabel = new Label("                                         " +
                "                                                               "); // Add spaces as needed
        spacerMenu.setGraphic(spacerLabel);
        spacerMenu.setDisable(true);
        spacerMenu.setStyle("-fx-opacity: 0;");
        return spacerMenu;
    }

    public  Menu ecuSNMenu()
    {
        /*
         ecuSN Menu is created to display the serial Number of ECU
         which we are connected to
         */

        Menu cntTo = new Menu("Connected : " + cnt.ecuSN);
        cntTo.setDisable(false);
        return cntTo;
    }



}
