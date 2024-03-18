package desktop.app.erch.Helper;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class Common {
    public static boolean containsSequence(byte[] response, byte[] sequence) {
        for (int i = 0; i <= response.length - sequence.length; i++) {
            boolean found = true;
            for (int j = 0; j < sequence.length; j++) {
                if (response[i + j] != sequence[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                System.out.println("Sequence found :"+sequence.toString());
                return true; // Sequence found
            }
        }
        return false; // Sequence not found
    }
   public static Image erchIcon = new Image(Common.class.getResourceAsStream("/desktop/app/erch/Images/app_logo.jpg"));


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
}
