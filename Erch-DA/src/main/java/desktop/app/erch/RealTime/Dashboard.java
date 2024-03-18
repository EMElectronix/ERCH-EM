package desktop.app.erch.RealTime;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Connection.Connect;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.LcdDesign;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.DataProcess.*;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bRealtimeStart;
import static desktop.app.erch.Helper.Frames.bRealtimeStop;

public class Dashboard {

    Roundgauge rpmGauge,vsGauge,aapGauge,exapGauge,eopGauge,ch7Gauge,ch8Gauge,eotGauge,aatGauge;
    LCD tcS, cfS, aS, baV;
    VBox ebS, cvS, fsS, hsS;

    Gauge altitude;
    Label[] countInp = new Label[4];

    ByteArrayOutputStream realtimeBuffer = new ByteArrayOutputStream();
    AtomicInteger rCounter = new AtomicInteger(-1);
    VBox aP,cP,bL,hA;
    Button startStop;

    String off = "/desktop/app/erch/Images/Off_buttons.png";
    String on = "/desktop/app/erch/Images/On_buttons.png";

    String  pressed = "/desktop/app/erch/Images/Pressed.png";
    String released = "/desktop/app/erch/Images/Released.png";

    String start = "/desktop/app/erch/Images/Start.png";
    String stop = "/desktop/app/erch/Images/Stop.png";

    String temp = "\"0\",\"\",\"40\",\"\",\"80\",\"\",\"120\",\"\",\"160\",\"\",\"200\"";

    Logger log = LogManager.getLogger(Dashboard.class);

    String failed = "Failed to open COM port.";

    String errorMessage = "Error";


    public void displayDashboard (SerialPort selectedPort){
        /*
        displayDashboard : All Gauges, digital lcd, that represent Realtime are added
         */


        Stage dashboardStage = new Stage();
        dashboardStage.setTitle("Realtime Data Dashboard");

        dashboardStage.getIcons().add(erchIcon);

        BorderPane dashLayout = new BorderPane();

        /***••• Round-Gauge Design Starts •••***/

        // Engine RPM
        rpmGauge  = new Roundgauge(0, 320.0,280.0, Gauge.ScaleDirection.CLOCKWISE,
                0, 3000,true,true,"Engine\n Speed","",
                "\"0\",\"5\",\"10\",\"15\",\"20\",\"25\",\"30\"",
                1, "RPMx100", 350, 350);

        // Vehicle Speed
        vsGauge   = new Roundgauge(0,Gauge.ScaleDirection.CLOCKWISE,
                0,150,true,false,"Vehicle\n  Speed","",
                "\"0\",\"10\",\"20\",\"30\",\"40\",\"50\",\"60\",\"70\",\"80\",\"90\",\"100\",\"110\",\"120\",\"130\",\"140\",\"150\"",
                1,"km/hr",350,350);

        // Ambient Air Pressure
        aapGauge  = new Roundgauge(1,15.0,145.0,Gauge.ScaleDirection.COUNTER_CLOCKWISE,
                0.0F, 2.0F,false,true,".  AAP","Bar",
                "\"0\",\"\",\"0.4\",\"\",\"0.8\",\"\",\"1.2\",\"\",\"1.6\",\"\",\"2.0\"",
                0,"",250,250);

        // Exhaust Air Pressure
        exapGauge = new Roundgauge(1,15.0,230.0, Gauge.ScaleDirection.COUNTER_CLOCKWISE,
                0.0,10.0,false,true,"EXAP","Bar",
                "\"0\",\"\",\"2.0\",\"\",\"4.0\",\"\",\"6.0\",\"\",\"8.0\",\"\",\"10.0\"",
                0,"",250,250);

        // Engine Oil Pressure
        eopGauge  = new Roundgauge(1,290.0,230.0, Gauge.ScaleDirection.COUNTER_CLOCKWISE,
                0.0,10.0,false,true,". EOP","Bar",
                "\"0\",\"\",\"2.0\",\"\",\"4.0\",\"\",\"6.0\",\"\",\"8.0\",\"\",\"10.0\"",
                0,"",250,250);

        // CH7 Temperature
        ch7Gauge  = new Roundgauge(0,350.0,230.0, Gauge.ScaleDirection.CLOCKWISE,
          0,200,false,true,"CH7","°C",temp, 0,"",250,250);

        // CH8 Temperature
        ch8Gauge  = new Roundgauge(0,345.0,145.0, Gauge.ScaleDirection.CLOCKWISE,
                0,200,false,true,"CH8  .","°C", temp,
                0,"",250,250);

        // Engine Oil Temperature
        eotGauge  = new Roundgauge(0,70.0,230.0, Gauge.ScaleDirection.CLOCKWISE,
                0,200,false,true,"EOT","°C",
                temp,
                0,"",250,250);

        // Ambient Air Temperature
        aatGauge  = new Roundgauge(0,Gauge.ScaleDirection.CLOCKWISE,
                -30,70,false,true,"AAT","°C",
                "\"-30\",\"-20\",\"-10\",\"0\",\"10\",\"20\",\"30\",\"40\",\"50\",\"60\",\"70\"",
                0,"",250,250);

        /***--- Round Gauge Design Ends ---***/


        /***••• Lcd Gauge Design Starts •••***/

        // Turbocharger Speed
        tcS = new LCD("Turbocharger Speed", "RPM", "RPM",120000,
                LcdDesign.GREEN_DARKGREEN, true, 90000,190);

        // Cooling Fan Speed
        cfS = new LCD("Cooling Fan Speed", "RPM", "RPM",60000,
                LcdDesign.GREEN_DARKGREEN, true, 50000,190);

        // Alternator Speed
        aS  = new LCD("Alternator Speed", "RPM", "RPM",60000,
                LcdDesign.GREEN_DARKGREEN, true, 50000,190);

        // Battery Voltage
        baV = new LCD("Battery ", "V", "V",32,
                LcdDesign.BLACK_YELLOW, true, 30,100);

        /***--- Lcd Gauge Design Ends ---***/

        /***••• Input and Output Status Gauges Starts •••***/

                                 /* Input Status */
        // Accelerator Pedal
        aP = statusIcon(released,"Accelerator Pedal Input");
        // Clutch Pedal
        cP = statusIcon(released,"Clutch Pedal Input");
        // Brake Lever
        bL = statusIcon(released,"Brake Lever Input");
        // High Altitude Switch
        hA = statusIcon(off,"High Altitude Switch Input");

                                 /* Output Status */
        // Exhaust Brake Status
        ebS = statusIcon(off,"Exhaust Brake Status");
        // Cooling Valve Status
        cvS = statusIcon(off,"Cooling Valve Status");
        // Fuel Sol Status
        fsS = statusIcon(off,"Fuel Sol Status");
        // Heater Sol Status
        hsS = statusIcon(off,"Heater Sol Status");

        /***--- Input and Output Status Gauges Ends ---***/

        /*** Altitude Gauge ***/
        altitude = GaugeBuilder.create()
                .skinType(Gauge.SkinType.SLIM)
                .prefSize(150,150)
                .title("Altitude")
                .titleColor(Color.WHITE)
                .animated(true)
                .maxValue(5000)
                .decimals(0)
                .barColor(Color.YELLOW)
                .unit("METRES")
                .unitColor(Color.WHITE)
                .build();

        // Add Inputs to layout as per design
        HBox aPcP = new HBox(50,aP,cP);
        HBox bLhA = new HBox(50,bL,hA);
        HBox allInput = new HBox(50,aPcP,bLhA);

        // Add Outputs to layout as per design
        HBox ebsCvs = new HBox(50,ebS,cvS);
        HBox fssHss = new HBox(50,fsS,hsS);
        HBox allOutput = new HBox(500,ebsCvs,fssHss);


        startStop = statusBtn(start,"Start");
        startStop.getStyleClass().add("start-button");

        setStart(startStop,"ON");
        realtimeStart(selectedPort);


        // Set up the event handler to toggle the status
        startStop.setOnAction(event -> {
            String currentStatus = getStatus(startStop);
            String newStatus = currentStatus.equals("ON") ? "OFF" : "ON";
            setStart(startStop, newStatus);

            if(newStatus.equals("ON")){
                realtimeStart(selectedPort);
            }
            else{
                realtimeStop(selectedPort);
            }
        });

        Label[] count = new Label[4];


        count[0] = new Label("  Engine Started Count        : ");
        count[1] = new Label("  Engine Overspeed Count  : ");
        count[2] = new Label("  Engine Overheat Count    : ");
        count[3] = new Label("  Vehicle Overspeed Count : ");

        countInp[0] = new Label();
        countInp[1] = new Label();
        countInp[2] = new Label();
        countInp[3] = new Label();


        for(int i=0;i<4;i++){
            count[i].setStyle(" -fx-text-fill: #F0D317; -fx-font-weight: bold; -fx-font-size: 18;");
        }

        for(int i=0;i<4;i++){
            countInp[i].setStyle(" -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 30;");
        }

        GridPane allCount = new GridPane();
        allCount.setStyle("-fx-background-color: #000000; -fx-background-radius: 20px;-fx-border-radius: 20px;");
        allCount.add(count[0],0,0);
        allCount.add(countInp[0],1,0);
        allCount.add(count[1],0,1);
        allCount.add(countInp[1],1,1);
        allCount.add(count[2],0,2);
        allCount.add(countInp[2],1,2);
        allCount.add(count[3],0,3);
        allCount.add(countInp[3],1,3);


        Button dropdownButton = new Button("Count 🔽");
        dropdownButton.getStyleClass().add("count-button");
        dropdownButton.setPrefSize(100,50);


        dropdownButton.setOnAction(e -> {
            Popup popup = new Popup();
            popup.getContent().add(allCount);
            popup.setAutoHide(true);

            // Position the Popup below the button
            popup.setX(dropdownButton.localToScreen(0, 0).getX());
            popup.setY(dropdownButton.localToScreen(0, 0).getY() + dropdownButton.getHeight());

            popup.show(dashboardStage);
        });


        /** Pressure **/
        VBox pressureUP = new VBox(20);
        pressureUP.getChildren().addAll(exapGauge,eopGauge);

        VBox pressureDown = new VBox(-320);
        pressureDown.getChildren().addAll(aapGauge,pressureUP);
        pressureDown.setMargin(aapGauge, new Insets(-200, 0, 0, 100));

        StackPane finalpane1 = new StackPane(pressureDown,vsGauge);
        finalpane1.setMargin(pressureDown, new Insets(350, 0, 0, 210));
        finalpane1.setMargin(vsGauge, new Insets(100, 0, 100, 0));
        finalpane1.setPrefSize(570,550);
        finalpane1.setPadding(new Insets(0));

        /** Temperature **/

        VBox tempUP = new VBox(20);
        tempUP.getChildren().addAll(ch7Gauge,eotGauge);

        VBox tempDown = new VBox(-320);
        tempDown.getChildren().addAll(ch8Gauge,tempUP);
        tempDown.setMargin(ch8Gauge, new Insets(-200, 0, 0, -100));

        StackPane finalpane2 = new StackPane(tempDown,rpmGauge);
        finalpane2.setMargin(tempDown, new Insets(350, 0, 0, 60));
        finalpane2.setMargin(rpmGauge, new Insets(100, 0, 100, 173));
        finalpane2.setPrefSize(570,550);
        finalpane2.setPadding(new Insets(0));

        /** Digital Lcd **/

        VBox speeds = new VBox(15);
        speeds.getChildren().addAll(tcS,cfS,aS);
        speeds.setAlignment(Pos.CENTER);

        StackPane finalpane3 = new StackPane(speeds);
        finalpane3.setPadding(new Insets(0));

        /** Altitude **/

        VBox finalpane4 = new VBox(altitude);
        finalpane4.setPrefSize(150,150);
        finalpane4.setAlignment(Pos.BOTTOM_LEFT);

        GridPane finishedPane = new GridPane();

        finishedPane.getChildren().addAll(finalpane1,finalpane2,finalpane3,allOutput,finalpane4,baV,allInput,
                                          dropdownButton,aatGauge,startStop);  //finalpane1,finalpane2,finalpane3
        finishedPane.setMargin(finalpane1, new Insets(100, 100, 0, 910));
        finishedPane.setMargin(finalpane2, new Insets(100, 910, 0, 100));
        finishedPane.setMargin(finalpane3, new Insets(80, 670, 0, 675));
        finishedPane.setMargin(allOutput,new Insets(0,0,-800,230));
        finishedPane.setMargin(finalpane4, new Insets(500, 1350, -100, 30));
        finishedPane.setMargin(baV, new Insets(10, 0, 0, 1200));
        finishedPane.setMargin(allInput, new Insets(0, 0, 500, 450));
        finishedPane.setMargin(dropdownButton, new Insets(0, 0, 500, 200));
        finishedPane.setMargin(aatGauge, new Insets(540, 0, -540, 640));
        finishedPane.setMargin(startStop, new Insets(0, 800, 500, 10));


        dashLayout.setCenter(finishedPane);

        VBox content = new VBox();
        content.setFillWidth(true);

        Image backGround = new Image(getClass().getResourceAsStream("/desktop/app/erch/Images/Dashbg5.png"));
        setBackground(backGround,dashLayout);

        Scene dataScene = new Scene(dashLayout,800,600);
        dashLayout.getStylesheets().add(getClass().getResource("/desktop/app/erch/css/dashboard.css").toExternalForm());
        dashboardStage.setScene(dataScene);
        dashboardStage.setMaximized(true);
        dashboardStage.showAndWait();

    }




    public void realtimeStart( SerialPort serialPort) {
        try {
            boolean portOpened = serialPort.openPort();
            if (portOpened) {
                setComportParameters(serialPort);

                serialPort.writeBytes(bRealtimeStart(), bRealtimeStart().length);

                RealtimeTask(serialPort);

            } else {
                log.error(failed);
                sof(errorMessage, failed, false);
            }
        } catch (Exception connectionException) {
            System.out.println("Error opening COM port: " + connectionException.getMessage());
            connectionException.printStackTrace(); // Print the stack trace for detailed information
            sof(errorMessage, "Error opening COM port:", false);
        }
    }

    public void realtimeStop( SerialPort serialPort ) {
        try {
            if (true) {
                setComportParameters(serialPort);

                serialPort.writeBytes(bRealtimeStop(), bRealtimeStop().length);

                serialPort.closePort();

            } else {
                log.fatal(failed);
                sof(errorMessage, failed, false);
            }
        } catch (Exception connectionException) {
            log.error("Error opening COM port: {}" , connectionException.getMessage());
            connectionException.printStackTrace(); // Print the stack trace for detailed information
            sof(errorMessage, "Error opening COM port:", false);
        }
    }

    public void RealtimeTask( SerialPort serialPort) {

        /*
        RealtimeTask is used to receive frames from ECU
         */

        // Initialize a timer for unexpected stop detection
        final Timer[] timer = {new Timer()};
        long timeoutDuration = 5000; // Set the timeout duration in milliseconds (adjust as needed)


        Task<Void> backgroundTask = new Task<>() {
            @Override
            protected Void call() {
                while (true) {
                    byte[] responsePart = new byte[1];
                    int numBytesRead = serialPort.readBytes(responsePart, 1);


                    if (numBytesRead != 1) {
                        sof("Connection Failed", "Error reading the response.", false);
                        break; // Exit the loop
                    }

                    realtimeBuffer.write(responsePart[0]);

                    byte[] realtimeResponse = realtimeBuffer.toByteArray();

                    // Reset the timer each time a response is received
                    timer[0].cancel();
                    timer[0] = new Timer();
                    timer[0].schedule(new TimerTask() {
                        @Override
                        public void run() {

                            String currentStatus = getStatus(startStop);
                            if(currentStatus.equals("ON")){
                                // Timeout action
                                Platform.runLater(() -> {
                                    sof("RealTime Failed", "Error Updating Realtime❗", false);
                                    sof("Connection Issue", "No Realtime Updates for a while. Update has stopped." +"\nPlease check port Connection!", false);
                                });
                                serialPort.closePort();
                            }}
                    }, 20000); // 20 seconds timeout

                    if  (containsSequence(realtimeResponse, new byte[]{0x0D,0x0A,0x0D})) {
                        log.info("Raltime Received Response :");
                        for (byte b : realtimeResponse) {
                            System.out.printf("%02X ", b);
                        }
                        System.out.println(" \n ");

                        String responseString = new String(realtimeResponse, StandardCharsets.UTF_8);
                        String data = responseString.substring(6, 108);

                        // Use Platform.runLater to update UI components
                        Platform.runLater(() -> processAndShowData(data.getBytes()));
                        realtimeBuffer.reset();
                        rCounter.addAndGet(1);

                        System.out.println("Buffer is cleared ---" + "Response Length: " + realtimeResponse.length + " ---Datacount: " + rCounter );
                    }

                }

                return null;
            }
        };

        // Start the background task in a new thread
        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.setDaemon(true); // Set the thread as a daemon thread to allow program exit
        backgroundThread.start();
    }



    public void processAndShowData(byte[] response) {
        /*
        processAndShowData so that each value is stored in its respective Gauge.
         */

        // Convert byte array to string
        String dataString = new String(response, StandardCharsets.UTF_8);

        // Print raw data for inspection
        System.out.println("Raw Data: " + dataString);

        // Define the lengths of each field
        int[] fieldLengths = {
                4,  //1.cylinder head temp 7
                4,  //2.cylinder head temp 8
                4,  //3.engine oil temp
                3,  //4.ambient air temp
                3,  //5.ambient air pressure
                3,  //6.exhaust air pressure
                3,  //7.engine oil pressure
                2,  //8.battery voltage
                5,  //9.engine speed
                6,  //10.turbocharger speed
                5,  //11.cooling fan speed
                3,  //12.vehicle speed
                5,  //13.AlternatorSpeed
                4,  //14.MeanSeaLevelAltitude
                1,  //15.AcceleratorPedalInput
                1,  //16.ClutchPedalInput
                1,  //17.BreakLeverInput
                1,  //18.HighAltitudeSwitchInput
                1,  //19.BrakeSolStatus
                1,  //20.CoolingSolStatus
                1,  //21.FuelSolStatus
                1,  //22.PreheaterSolStatus
                10,  //23.EngineStartedCount
                10,  //24.EngineOverspeedCount
                10,  //25.EngineOverheatCount
                10  //26.VehicleOverspeedCount
        };


        // Process the data based on the lengths
        int startIndex = 0;
        List<String> dataValues = new ArrayList<>();
        for (int i = 0; i < fieldLengths.length; i++) {
            int length = fieldLengths[i];
            int endIndex = startIndex + length;
            String fieldValue = dataString.substring(startIndex, endIndex);
            String processedValue = processRealtimeValue(i, fieldValue);
            dataValues.add(processedValue);
            startIndex = endIndex;
        }

        dataUpdate(dataValues.toArray(new String[0]));

    }

    private String processRealtimeValue(int fieldIndex, String fieldValue) {
        /*
        processRealtimeValue so that each value is stored in its respective field.
         */
        switch (fieldIndex) {
            case 0,1,2:
                String tempSignResult = tempSign(fieldValue);
                String processTempResult = processTemp(fieldValue);
                return tempSignResult + processTempResult;
            case 3:
                return tempSign(fieldValue)+fieldValue.substring(1,3);
            case 4:
                return String.valueOf(processPressure(fieldValue,100));
            case 5,6:
                return String.valueOf(processPressure(fieldValue,10));
            case 14,15,16,17,18,19,20,21:
                return processStatus(fieldValue);

            default:
                return fieldValue.trim(); // Default processing
        }
    }

    private VBox statusIcon(String imagePath,String statusName){

        /*
        statusIcon creates VBox with Image and statusName
         args    : imagePath    → Path of the Image
                   statusName   → Name of the status
         returns : VBox that combines both of them vertically
         */

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Image status = new Image(getClass().getResourceAsStream(imagePath));
        ImageView statusView = new ImageView(status);
        statusView.setFitWidth(70);
        statusView.setFitHeight(70);

        Label name = new Label(statusName);
        name.getStylesheets().add();
        name.setStyle("-fx-text-fill: white;-fx-font-weight: bold;"); FLAG
        vbox.getChildren().addAll(statusView,name);

        return vbox;
    }

    private void setOutput(VBox statusIcon, String status) {

        /*
        setStatus sets the status of the Output Parameters
         args    : statusIcon    → represents the status of Parameter
                   status        → used to set the status of Parameter
         */

        ImageView statusView = (ImageView) statusIcon.getChildren().get(0);

        // Modify the image path based on the status ("On" or "Off")
        String imagePath = status.equals("ON") ? on : off;

        Image newStatus = new Image(getClass().getResourceAsStream(imagePath));
        statusView.setImage(newStatus);
    }

    private void setInput(VBox statusIcon, String status) {
        /*
         setInput sets the status of Input Parameters
         args    : statusIcon    → represents the status of Parameter
                   status        → used to set the status of Parameter
         */

        ImageView statusView = (ImageView) statusIcon.getChildren().get(0);

        // Modify the image path based on the status ("On" or "Off")
        String imagePath = status.equals("ON") ? pressed : released;

        Image newStatus = new Image(getClass().getResourceAsStream(imagePath));
        statusView.setImage(newStatus);
    }

    private Button statusBtn(String imagePath, String statusName) {

        /*
         statusBtn creates Button
         args    : imagePath    → Path of the Image
                   statusName   → Name of the Parameter(output Status)
         returns : button which is used as Start and Stop Button
         */

        Button button = new Button();
        button.setAlignment(Pos.CENTER);

        Image statusImage = new Image(getClass().getResourceAsStream(imagePath));
        ImageView statusView = new ImageView(statusImage);
        statusView.setFitWidth(70);
        statusView.setFitHeight(70);

        Label nameLabel = new Label(statusName);
        nameLabel.setStyle("-fx-text-fill: white;-fx-font-weight: bold;");

        VBox add = new VBox(statusView, nameLabel);
        add.setAlignment(Pos.CENTER);
        button.setGraphic(add);
        button.setAlignment(Pos.CENTER);
        button.getStyleClass().add("start-button");

        return button;
    }

    private void setStart(Button statusButton, String status) {

         /*
         statusBtn creates Button
         args    : imagePath    → Path of the Image
                   statusName   → Name of the Parameter(output Status)
         returns : button which is used as Start and Stop Button
         */


        VBox container = (VBox) statusButton.getGraphic();
        ImageView statusView = (ImageView) container.getChildren().get(0);
        Label nameLabel = (Label) container.getChildren().get(1);

        // Modify the image path and label text based on the status ("On" or "Off")
        String imagePath = status.equals("ON") ? stop : start;
        String updatedStatusName = status.equals("ON") ? "Stop" : "Start";

        Image newStatus = new Image(getClass().getResourceAsStream(imagePath));
        statusView.setImage(newStatus);
        nameLabel.setText(updatedStatusName);

    }

    private String getStatus(Button statusButton) {

        /*
         getStatus is used to get the Status of Start or Stop Button
         */

        VBox container = (VBox) statusButton.getGraphic();
        Label nameLabel = (Label) container.getChildren().get(1);
        String name = nameLabel.getText();
        String onOrOff = name.equals("Stop") ? "ON" : "OFF";
        return onOrOff;
    }


    private void updateLED(Roundgauge gauge) {
        /*
        updateLed is used to check the threshold of each Gauge
        and On/Off accordingly
         */

        double value = gauge.getRGauge().getValue();

        if (gauge == vsGauge && value > 50) {
            vsGauge.getRGauge().setLedOn(true);
        } else if (gauge == rpmGauge && value > 1000) {
            rpmGauge.getRGauge().setLedOn(true);
        }
        // Add cases for other gauges
    }




    public void dataUpdate(String[] realtimeValues) {

        /*
        dataUpdate updates all the GaugeValues
         */

        /****** Temperature Gauges Update ******/

        ch7Gauge.getRGauge().setValue(Double.parseDouble(realtimeValues[0]));
        ch8Gauge.getRGauge().setValue(Double.parseDouble(realtimeValues[1]));
        eotGauge.getRGauge().setValue(Double.parseDouble(realtimeValues[2]));
        aatGauge.getRGauge().setValue(Double.parseDouble(realtimeValues[3]));


        /****** Pressure Gauges Update ******/

        aapGauge.getRGauge().setValue(Double.parseDouble(realtimeValues[4]));
        exapGauge.getRGauge().setValue(Double.parseDouble(realtimeValues[5]));
        eopGauge.getRGauge().setValue(Double.parseDouble(realtimeValues[6]));


        /****** Battery and Speed Gauges Update ******/

        baV.getLcdGauge().setValue(Double.parseDouble(realtimeValues[7]));
        rpmGauge.getRGauge().setValue(Double.parseDouble(realtimeValues[8]));
        tcS.getLcdGauge().setValue(Double.parseDouble(realtimeValues[9]));
        cfS.getLcdGauge().setValue(Double.parseDouble(realtimeValues[10]));
        vsGauge.getRGauge().setValue(Double.parseDouble(realtimeValues[11]));
        aS.getLcdGauge().setValue(Double.parseDouble(realtimeValues[12]));
        altitude.setValue(Double.parseDouble(realtimeValues[13]));

        /****** Input Status Update ******/


        setInput(aP, realtimeValues[14]);
        setInput(cP, realtimeValues[15]);
        setInput(bL, realtimeValues[16]);
        setOutput(hA, realtimeValues[17]);

        /****** Output Status Update ******/


        setOutput(ebS, realtimeValues[18]);
        setOutput(cvS, realtimeValues[19]);
        setOutput(fsS, realtimeValues[20]);
        setOutput(hsS, realtimeValues[21]);


        /****** Counter Update ******/

        countInp[0].setText(String.valueOf(Integer.parseInt(realtimeValues[22])));
        countInp[1].setText(String.valueOf(Integer.parseInt(realtimeValues[23])));
        countInp[2].setText(String.valueOf(Integer.parseInt(realtimeValues[24])));
        countInp[3].setText(String.valueOf(Integer.parseInt(realtimeValues[25])));

        /****** Led Update ******/

        updateLED(vsGauge);
        updateLED(rpmGauge);
        updateLED(aapGauge);
        updateLED(exapGauge);
        updateLED(eopGauge);
        updateLED(ch7Gauge);
        updateLED(ch8Gauge);
        updateLED(eotGauge);
        updateLED(aatGauge);
    }



}




