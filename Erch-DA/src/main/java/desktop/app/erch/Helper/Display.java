package desktop.app.erch.Helper;

import desktop.app.erch.DataAcess.Upload;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.time.LocalDate;

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


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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


    public static void dalert(String title, String content, String ser, String veh) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Set custom graphic based on success status
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(Display.class.getResourceAsStream("/desktop/app/erch/Images/download.png")));

        // Set a fixed size for the ImageView
        imageView.setFitWidth(70); // Adjust the width as needed
        imageView.setFitHeight(70); // Adjust the height as needed

        alert.getDialogPane().setGraphic(imageView);

        // Set icon for the title
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(erchIcon);

        ButtonType downloadAllButton = new ButtonType("Download All", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = ButtonType.CANCEL;

        alert.getButtonTypes().setAll(downloadAllButton, cancelButton);

        Button downloadAllButtonNode = (Button) alert.getDialogPane().lookupButton(downloadAllButton);
        Button cancelButtonNode = (Button) alert.getDialogPane().lookupButton(cancelButton);

        downloadAllButtonNode.setStyle("-fx-background-color: #005288; -fx-font-weight: bold; -fx-text-fill: white;");
        downloadAllButtonNode.setOnMouseEntered(e -> downloadAllButtonNode.setStyle("-fx-background-color: #0070C0; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
        downloadAllButtonNode.setOnMouseExited(e -> downloadAllButtonNode.setStyle("-fx-background-color: #005288; -fx-font-weight: bold; -fx-text-fill: white; "));


        cancelButtonNode.setStyle("-fx-background-color: #8B090A; -fx-font-weight: bold; -fx-text-fill: white;"); // Red color for failure
        cancelButtonNode.setOnMouseEntered(e -> cancelButtonNode.setStyle("-fx-background-color: #C70D0F; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")); // Darker red on hover
        cancelButtonNode.setOnMouseExited(e ->cancelButtonNode.setStyle("-fx-background-color: #8B090A; -fx-font-weight: bold; -fx-text-fill: white; ")); // Original red on exit


        alert.setResultConverter(buttonType -> {
            if (buttonType == downloadAllButton) {
                // Handle the "Download All" button click
                Upload data = new Upload();
                int recordsDownloaded = data.fetchAllData(ser,veh,true,null, null);

                if (recordsDownloaded > 0) {
                    sof("DOWNLOAD ALL", "ALL "+recordsDownloaded+" Data Records Successfully Loaded.", true);
                }
            }
            // Return null to indicate that the button click is not the closing action
            return null;
        });

        alert.showAndWait();
    }




}
