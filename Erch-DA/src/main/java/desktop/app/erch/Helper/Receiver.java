package desktop.app.erch.Helper;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import org.apache.logging.log4j.Logger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static desktop.app.erch.Helper.CRC.calculateCRC;
import static desktop.app.erch.Helper.Common.setComportParameters;
import static desktop.app.erch.Helper.Display.sof;

public class Receiver {

    public String receiveFrame(SerialPort selectedPort,String operation, String funCode, int expectedLength,byte[] sendFrame,Logger log){

            log.info("COM port opened successfully: {}" , selectedPort.getSystemPortName());

            setComportParameters(selectedPort);

            //Send the Connect frame
            selectedPort.writeBytes(sendFrame, sendFrame.length);

            int responseLength = expectedLength;
            byte[] connectResponse = new byte[responseLength];


            // Create a new timer for each connection attempt
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Timeout action
                    Platform.runLater(() -> sof(operation+" Failed",
                            "Timeout waiting for response!", false));

                    selectedPort.closePort();

                }
            }, 5000); // 5 seconds timeout

            for (int i = 0; i < responseLength; i++) {
                byte[] responsePart = new byte[1];
                int numBytesRead = selectedPort.readBytes(responsePart, 1);

                // Create a final variable for use in lambda expression
                final int index = i;

                if (numBytesRead != 1) {
                    // Handle the case where not all bytes are read (e.g., timeout or error)
                    Platform.runLater(() -> log.error("Error reading response byte - {}",index));
                    break; // Exit the loop
                }

                connectResponse[i] = responsePart[0];
            }
            timer.cancel(); // Cancel the timer

            //Print the full Received Response
            log.info("\n{} Received Response :",operation);
            for (byte b : connectResponse) {
                System.out.printf(String.format("%02X ", b));
            }
            System.out.println(" \n ");


            // Compare Received CRC with Calculated CRC
            int receivedCRC = (connectResponse[connectResponse.length - 4] & 0xFF) |
                    ((connectResponse[connectResponse.length - 5] & 0xFF) << 8);
            int calculatedCRC = calculateCRC(Arrays.copyOf(connectResponse,
                    connectResponse.length - 5));

            log.info("Rcrc : {}  Ccrc : {}",receivedCRC,calculatedCRC);
            String responseString = new String(connectResponse, StandardCharsets.UTF_8);
            String fC = responseString.substring(0,3);
            String data = responseString.substring(5,responseString.length()-5);
            log.info("DATA : {}",data);

            // Verify and return the data
            if (receivedCRC == calculatedCRC && funCode.equals(fC)){
                log.info("Serial Communication of {} Successful",operation);
                return data;

            }else{
                log.error("{} Failed",operation);
                return null;
            }

    }

}
