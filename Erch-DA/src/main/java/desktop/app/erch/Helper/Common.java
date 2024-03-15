package desktop.app.erch.Helper;

import javafx.scene.image.Image;

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
}
