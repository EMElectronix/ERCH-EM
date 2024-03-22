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

    public String receiveFrame(SerialPort selectedPort,String operation, String funCode, int expectedLength,
                               byte[] sendFrame,Logger log){

        /*
        receiveFrame sends the frame and processes the response
         */

            log.info("COM port opened successfully: {}" , selectedPort.getSystemPortName());

            setComportParameters(selectedPort);

            //Send the Connect frame
            selectedPort.writeBytes(sendFrame, sendFrame.length);

            int responseLength = expectedLength;
            byte[] erchResponse = new byte[responseLength];


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

                erchResponse[i] = responsePart[0];
            }
            timer.cancel(); // Cancel the timer

            //Print the full Received Response
            log.info("\n{} Received Response :",operation);
            for (byte b : erchResponse) {
                System.out.printf(String.format("%02X ", b));
            }
            System.out.println(" \n ");


            // Compare Received CRC with Calculated CRC
            int receivedCRC = (erchResponse[erchResponse.length - 4] & 0xFF) |
                    ((erchResponse[erchResponse.length - 5] & 0xFF) << 8);
            int calculatedCRC = calculateCRC(Arrays.copyOf(erchResponse,
                    erchResponse.length - 5));

            String responseString = new String(erchResponse, StandardCharsets.UTF_8);
            String fC = responseString.substring(0,3);

            // Rcrc - Received CRC, Ccrc - Calculated CRC
            log.info("Rcrc : {}  Ccrc : {}",receivedCRC,calculatedCRC);
            // Rfc -  Recieved Function code, Afc - Actual Fucntion Code
            log.info("Rfc  : {}  Afc : {}",fC,funCode);

            int numBytes = Integer.parseInt(responseString.substring(3,5));
            String data = responseString.substring(5,numBytes+5);
            log.info("DATA : {}",data);

            // Verify and return the data
            if (receivedCRC == calculatedCRC && funCode.equals(fC)){
                log.info("Serial Communication of {} Successful",operation);
                return data;

            }else{
                log.error("{} Failed\n CRC or Function Code mismatch",operation);
                return null;
            }

    }


    public String receiveFrame(SerialPort selectedPort,String operation, String funCode, int expectedLength,
                               String finalFrame,Logger log){

        /*
        receiveFrame processes the data and calls other receiveFrame
         */

        String cS1 = finalFrame.substring(finalFrame.length()-4, finalFrame.length()-2);
        String cS2 = finalFrame.substring(finalFrame.length()-2, finalFrame.length()-0);

        byte[] sendFrame = finalFrame.getBytes();

        // Parse hexadecimal strings to integers
        int intValueCS1 = Integer.parseInt(cS1, 16);
        int intValueCS2 = Integer.parseInt(cS2, 16);

        // Convert integers to bytes
        byte[] cs1 = {(byte) intValueCS1};
        byte[] cs2 = {(byte) intValueCS2};

        // Send the bytes before the last four
        selectedPort.writeBytes(Arrays.copyOfRange(sendFrame, 0, sendFrame.length - 4),
                sendFrame.length - 4);

        // Send the first pair
        selectedPort.writeBytes(cs1,1);
        // Send the second pair
        selectedPort.writeBytes(cs2,1);

        log.info("Sent : {} {} {}",Arrays.toString(sendFrame),
                Arrays.toString(cs1), Arrays.toString(cs2));

        return receiveFrame(selectedPort,operation,funCode,expectedLength,sendFrame,log);


    }

}
