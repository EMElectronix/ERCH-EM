package desktop.app.erch.RTC;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.Logger;

import java.util.Objects;


import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bRTCread;

public class ReadRTC extends Dialog<String> {
    String errorMessage = "Error";
    String data;

    Label dateLabel;
    private Label timeLabel;

    Label errorLabel;


    public ReadRTC(SerialPort selectedPort, Stage parent, Logger log) {
        /*
        ReadRTC is used to read the date and time from erch ECU
         */

        try {
            boolean portOpened = selectedPort.openPort();
            if (portOpened) {
                Receiver conn = new Receiver();
                data = conn.receiveFrame(selectedPort, "RTC Read", "a03", 23, bRTCread(), log);
                if (data != null) {

                    getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource
                            ("/desktop/app/erch/css/RTC.css")).toExternalForm());
                    setTitle("Read Date and Time");

                    // Set the stage as a modal window
                    initModality(Modality.WINDOW_MODAL);
                    initOwner(parent);

                    String day = data.substring(0, 2);
                    String month = data.substring(2, 4);
                    String year = data.substring(4, 6);

                    String hours = data.substring(6, 8);
                    String minutes = data.substring(8, 10);
                    String seconds = data.substring(10, 12);
                    String meridian = data.endsWith("0") ? "AM" : "PM";

                    // Add OK button
                    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    getDialogPane().getButtonTypes().add(okButton);

                    // Add Back button
                    ButtonType backButton = new ButtonType("Back", ButtonBar.ButtonData.BACK_PREVIOUS);
                    getDialogPane().getButtonTypes().add(backButton);



                    // Set result converter for OK button
                    setResultConverter(dialogButton -> {
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



                    // Create an analog clock
                    AnalogClock analogClock = new AnalogClock(hours, minutes, seconds, meridian);

                    // Create labels to display date and time
                    dateLabel = new Label("\uD83D\uDCC6   Date: " + day + "-" + month + "-20" + year);

                    errorLabel = new Label("Date and Time \nUnavailable");


                    // Add labels to a GridPane
                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(15);
                    gridPane.setVgap(10); // Adjust vertical gap
                    gridPane.setAlignment(Pos.CENTER);


                    if (timeLabel!=null) {
                        // Add the labels to the grid
                        gridPane.add(dateLabel, 0, 0);
                        gridPane.add(analogClock, 0, 1);  // Analog clock in the second row, first column
                    } else {
                        gridPane.add(errorLabel, 0, 0);
                    }

                    // Add the grid to the dialog pane
                    getDialogPane().setContent(gridPane);
                    // Set the size of the dialog pane
                    getDialogPane().setPrefSize(300, 100);

                    // Add a close request event handler to handle "X" button click
                    Stage rtcStage = (Stage) getDialogPane().getScene().getWindow();
                    rtcStage.setOnCloseRequest(event -> {
                        String result = "ok";
                        close(); // Close the current dialog
                        setResult(result);
                    });


                } else {
                    log.fatal("Read RTC Date and Time Failed");
                    sof(errorMessage, "Read RTC Date and Time Failed❗", false);

                }


            } else {
                log.error("Failed to Open Comport");
                sof(errorMessage, "Failed to open COM port ❗", false);

            }

        } catch (Exception connectionException) {
            log.error("Error opening COM port: {}", connectionException.getMessage());
            sof(errorMessage, "Error opening COM port ❗", false);

        }


    }


        private class AnalogClock extends Group {
        /*
         AnalogClock class creates clock analog clock
         */
            private Line hourHand;
            private Line minuteHand;
            private Circle clockFace;

            public AnalogClock(String hours, String minutes, String seconds, String meridian) {
                // Set up the clock face
                clockFace = new Circle(8, 15, 10);
                clockFace.setFill(Color.WHITE);
                clockFace.setStroke(Color.WHITE);

                // Set up the hour hand
                hourHand = new Line();
                hourHand.setStrokeWidth(2);
                hourHand.setStroke(Color.BLACK);

                // Set up the minute hand
                minuteHand = new Line();
                minuteHand.setStrokeWidth(2);
                minuteHand.setStroke(Color.BLACK);

                // Update clock hands based on the provided time
                updateClockHands(hours, minutes);

                // Create a timeline for clock animation
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(1), event -> updateClockHands(hours, minutes))
                );
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();

                timeLabel = new Label("      Time: " + hours + ":" + minutes + ":" + seconds + " " + meridian);

                // Add clock components to the group
                getChildren().addAll(clockFace, hourHand, minuteHand, timeLabel);

            }

            private void updateClockHands(String hours, String minutes) {
                double hourAngle = 90 - (Integer.parseInt(hours) % 12) * 30 - Integer.parseInt(minutes) * 0.5;
                double minuteAngle = 90 - Integer.parseInt(minutes) * 6;

                // Adjust the length of the hour and minute hands
                double hourHandLength = 4; // Set the desired length for the hour hand
                double minuteHandLength = 8; // Set the desired length for the minute hand

                hourHand.setStartX(8);
                hourHand.setStartY(15);
                hourHand.setEndX(8 + hourHandLength * Math.cos(Math.toRadians(hourAngle)));
                hourHand.setEndY(15 - hourHandLength * Math.sin(Math.toRadians(hourAngle)));

                minuteHand.setStartX(8);
                minuteHand.setStartY(15);
                minuteHand.setEndX(8 + minuteHandLength * Math.cos(Math.toRadians(minuteAngle)));
                minuteHand.setEndY(15 - minuteHandLength * Math.sin(Math.toRadians(minuteAngle)));
            }

        }


    }
