package desktop.app.erch;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Optional;

import static desktop.app.erch.Helper.Common.database;
import static desktop.app.erch.Helper.Common.erchIcon;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Query.changePasswordQuery;
import static desktop.app.erch.Helper.Query.fetchPasswordQuery;

public class Password {
    Logger log = LogManager.getLogger(Password.class);

    String failed = "Password Change Failed";
    public void displayChangePassword(){

        Dialog<ButtonType> cpDialog = new Dialog<>();

        // Add the styles.css file to the dialog scene
        cpDialog.getDialogPane().getStylesheets().add(getClass().getResource("/desktop/app/erch/css/RTC.css").toExternalForm());

        cpDialog.setTitle("Change Password");

        // Set the header text and style
        Label headerLabel = new Label("Enter old password and new password:");
        headerLabel.setStyle("-fx-text-fill: white;-fx-font-size: 17;"); // Set text color to white
        headerLabel.setAlignment(Pos.CENTER);
        cpDialog.setHeaderText(null); // Clear the default header text
        cpDialog.getDialogPane().setHeader(headerLabel);

        cpDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create GridPane for the layout
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane"); // Add the grid-pane style class
        grid.setHgap(10);
        grid.setVgap(10);

        Stage pdStage = (Stage) cpDialog.getDialogPane().getScene().getWindow();
        pdStage.getIcons().add(erchIcon);

        PasswordField oldPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        grid.add(new Label("Old Password:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.setAlignment(Pos.CENTER);

        cpDialog.getDialogPane().setContent(grid);
        cpDialog.setWidth(350);
        cpDialog.setWidth(200);

        cpDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK){
                return ButtonType.OK; // We return the button type here
            }
            return ButtonType.CANCEL;
        });

        Optional<ButtonType> result = cpDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();


            // Validate that the new password is at least 4 characters long
            if (newPassword.length() < 4) {
                sof(failed, "New password must be at least 4 characters long.",false);
                return;
            }

            // Retrieve old password from the database
            String storedOldPassword = getPasswordFromDatabase(); // Implement this method

            // Add logic to check old password and change the password
            if (oldPassword.equals(storedOldPassword)) {
                if(newPassword.equals(confirmPassword)){
                    // Update password in the database
                    boolean passwordChanged = updatePasswordInDatabase(newPassword); // Implement this method
                    if(passwordChanged){
                        sof("Password Changed", "Your password has been changed.",true);
                    }else{
                        sof(failed, "Password Update Failed!",false);
                    }
                }else{
                    sof(failed, "New password and confirm password do not match.",false);
                }

            } else {
                sof(failed, "Old password is incorrect.",false);
            }
        }


    }


    public boolean displayEnterPassword() {

        Dialog<String> pwdDialog = new Dialog<>();
        pwdDialog.setTitle("Password Protection");
        pwdDialog.setHeaderText("Enter Password");

        pwdDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        Stage pwdStage = (Stage) pwdDialog.getDialogPane().getScene().getWindow();
        pwdStage.getIcons().add(erchIcon);

        pwdDialog.getDialogPane().getStylesheets().add(Password.class.getResource("/desktop/app/erch/css/RTC.css").toExternalForm());

        Label passwordLabel = new Label("Password :  ");
        PasswordField passwordField = new PasswordField();

        GridPane grid = new GridPane();
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        pwdDialog.getDialogPane().setContent(grid);

        // Request focus on the password field by default
        passwordField.requestFocus();

        // Convert the result to a password string when the OK button is clicked
        pwdDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });

        // Show the dialog and wait for the user's response
        Optional<String> result = pwdDialog.showAndWait();
        if (result.isPresent()) {
            // Handle the entered password
            String enteredPassword = result.get();
            String expectedPassword = getPasswordFromDatabase();
            log.info(" Expected Password {} ", expectedPassword);
            if (!enteredPassword.equals(expectedPassword)) {
                sof("Password", "Password Entered in incorrect\nPlease Enter Correct Password", false);
                return false;
            } else {
                return true;
            }
        }
        return false;

    }





    String getPasswordFromDatabase() {
        String password = "";
        try {
            // Create a statement
            Statement statement = database().createStatement();
            // Execute the query to retrieve the password
            ResultSet resultSet = statement.executeQuery(fetchPasswordQuery());
            // If there is a result, retrieve the password
            if (resultSet.next()) {
                password = resultSet.getString("password");
                log.info("Password retrieved from database: {}" , password);
            } else {
                log.info("No password found in the database." );
            }
            // Close the result set
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Failed to fetch Password");
        }
        return password;
    }

    boolean updatePasswordInDatabase(String newPassword) {
        try {
            PreparedStatement preparedStatement = database().prepareStatement(changePasswordQuery());
            // Set the new password parameter
            preparedStatement.setString(1, newPassword);
            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            // Check if the update was successful
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Change Password failed while updating in database");
        }
        return false; // Return false if the update fails
    }



}
