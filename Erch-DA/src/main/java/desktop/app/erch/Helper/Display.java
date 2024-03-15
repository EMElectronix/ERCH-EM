package desktop.app.erch.Helper;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import static desktop.app.erch.Helper.Common.erchIcon;

public class Display {



    public static void sof(String title, String content, boolean isSuccess) {

        /*
         sof represents Success or Failure

         args    : title      → Title of dialog
                   content    → Content inside the dialog box
                   isSuccess  → Success or Failure is Known
         shows   : dialog with Success or Failure message
         */


        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Set custom graphic based on success status
        ImageView imageView = new ImageView();
        if (isSuccess) {
            imageView.setImage(new Image(Display.class.getResourceAsStream("/desktop/app/erch/Images/Tick.png")));
        } else {
            imageView.setImage(new Image(Display.class.getResourceAsStream("/desktop/app/erch/Images/alert.png")));
        }

        // Set a fixed size for the ImageView
        imageView.setFitWidth(70); // Adjust the width as needed
        imageView.setFitHeight(70); // Adjust the height as needed

        alert.getDialogPane().setGraphic(imageView);

        // Set icon for the title
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(erchIcon);

        // Load CSS file
        Scene scene = stage.getScene();
        scene.getStylesheets().add(Display.class.getResource("/desktop/app/erch/css/Comport.css").toExternalForm());

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);

        if (isSuccess) {
            okButton.setStyle("-fx-background-color: #008819; -fx-font-weight: bold; -fx-text-fill: white;"); // Green color for success
            okButton.setOnMouseEntered(e -> okButton.setStyle("-fx-background-color: #00c524; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")); // Darker green on hover
            okButton.setOnMouseExited(e -> okButton.setStyle("-fx-background-color: #008819; -fx-font-weight: bold; -fx-text-fill: white; ")); // Original green on exit
        } else {
            okButton.setStyle("-fx-background-color: #8B090A; -fx-font-weight: bold; -fx-text-fill: white;"); // Red color for failure
            okButton.setOnMouseEntered(e -> okButton.setStyle("-fx-background-color: #C70D0F; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")); // Darker red on hover
            okButton.setOnMouseExited(e -> okButton.setStyle("-fx-background-color: #8B090A; -fx-font-weight: bold; -fx-text-fill: white; ")); // Original red on exit
        }


        alert.showAndWait();
    }




}
