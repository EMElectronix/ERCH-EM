package desktop.app.erch.Helper;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static desktop.app.erch.Helper.Display.sof;

public class Common {
    static String errorMessage = "Error";
    public static boolean containsSequence(byte[] response, byte[] sequence , Logger log) {
        /*
        containsSequence checks for a particular sequence of bytes
        args    : response    → the whole data
                  sequence    → particular sequence of bytes
        returns : returns true only if sequence found,
                  returns false if sequence is not found
         */

        for (int i = 0; i <= response.length - sequence.length; i++) {
            boolean found = true;
            for (int j = 0; j < sequence.length; j++) {
                if (response[i + j] != sequence[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                log.info("Sequence found");
                return true; // Sequence found
            }
        }
        return false; // Sequence not found
    }
   public static final Image erchIcon = new Image(Objects.requireNonNull(Common.class.getResourceAsStream("/desktop/app/erch/Images/app_logo.jpg")));


    public static void setBackground(Image image, BorderPane layout) {
        /*
        setBackground sets image for indexLayout
        args    : Image    → Specifies the Image to be displayed on Background
         */

        BackgroundSize backgroundSize = new BackgroundSize(
                BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);

        BackgroundImage bImg = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        Background bGround = new Background(bImg);

        layout.setBackground(bGround);
    }

    public static void setComportParameters(SerialPort serialPort){
        serialPort.setComPortParameters(9600, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 1000, 0);
    }

    public static String removeLeadingZeros(String input) {
        // Remove leading zeros from the input
        return input.replaceFirst("^0+", "");
    }

    public static String padWithZeros(String input, int desiredLength) {
        // If the input length is less than the desired length, pad with zeros
        while (input.length() < desiredLength) {
            input = "0" + input;
        }
        return input;
    }


    public static void error(Exception exception,Logger log){
        log.error("Error opening COM port: {}", exception.getMessage());
        sof(errorMessage, "Error opening COM port ❗", false);
    }

    public static void failed(Logger log){
        log.error("Failed to Open Comport");
        sof(errorMessage, "Failed to open COM port ❗", false);

    }

    public static void fatal(String param,Logger log){
        String failed = param+" Failed❗";
        log.fatal(failed);
        sof(errorMessage, failed, false);
    }



    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyy");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hhmmssa");
}
