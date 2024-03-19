package desktop.app.erch;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.RTC.ReadRTC;
import desktop.app.erch.RTC.WriteRTC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static desktop.app.erch.Helper.Common.erchIcon;
import static desktop.app.erch.Index.item12;

public class ReadConf {

    Dialog<ButtonType> confDialog;

    Logger log = LogManager.getLogger(ReadConf.class);

    public void displayConfig(SerialPort selectedPort){
        confDialog = new Dialog<>();
        confDialog.setTitle("ERCH Configuration Info");

        // Add icon
        Stage confStage = (Stage) confDialog.getDialogPane().getScene().getWindow();
        confStage.getIcons().add(erchIcon);

        // Create Different buttons of different parameters
        ButtonType rtcButtonType = new ButtonType("RTC\nDate and Time", ButtonBar.ButtonData.OK_DONE);
        ButtonType sevButtonType = new ButtonType("Serial,Engine and\nVehicle Model No. ", ButtonBar.ButtonData.OK_DONE);
        ButtonType samplingButtonType = new ButtonType("Sampling\n   Rate", ButtonBar.ButtonData.OK_DONE);
        ButtonType noncButtonType = new ButtonType("NO / NC\nConfiguration\n(fault inputs)", ButtonBar.ButtonData.OK_DONE);
        ButtonType ssButtonType = new ButtonType("Signal Source for\nEngine RPM &\n Vehicle Speed", ButtonBar.ButtonData.OK_DONE);
        ButtonType pprButtonType = new ButtonType("Pulses per \nRevoluton for\n Various Speeds", ButtonBar.ButtonData.OK_DONE);
        ButtonType parlimitsButtonType = new ButtonType("   \nParameter Limits for Warning\n   ", ButtonBar.ButtonData.OK_DONE);

        confDialog.getDialogPane().getButtonTypes().addAll(rtcButtonType,sevButtonType,samplingButtonType,noncButtonType,ssButtonType,pprButtonType,parlimitsButtonType, ButtonType.CLOSE);

        Button rtcButtonNode = (Button) confDialog.getDialogPane().lookupButton(rtcButtonType);
        rtcButtonNode.setPrefWidth(130);
        Button sevButtonNode = (Button) confDialog.getDialogPane().lookupButton(sevButtonType);
        sevButtonNode.setPrefWidth(130);
        Button samplingButtonNode = (Button) confDialog.getDialogPane().lookupButton(samplingButtonType);
        samplingButtonNode.setPrefWidth(130);
        Button noncButtonNode = (Button) confDialog.getDialogPane().lookupButton(noncButtonType);
        noncButtonNode.setPrefWidth(130);
        Button ssButtonNode = (Button) confDialog.getDialogPane().lookupButton(ssButtonType);
        ssButtonNode.setPrefWidth(130);
        Button pprButtonNode = (Button) confDialog.getDialogPane().lookupButton(pprButtonType);
        pprButtonNode.setPrefWidth(130);
        Button parlimitsButtonNode = (Button) confDialog.getDialogPane().lookupButton(parlimitsButtonType);
        parlimitsButtonNode.setPrefWidth(415);

        rtcButtonNode.setStyle("-fx-background-color: #005288; -fx-font-weight: bold; -fx-text-fill: white;");
        rtcButtonNode.setOnMouseEntered(e -> rtcButtonNode.setStyle("-fx-background-color: #0070C0; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
        rtcButtonNode.setOnMouseExited(e -> rtcButtonNode.setStyle("-fx-background-color: #005288; -fx-font-weight: bold; -fx-text-fill: white; "));

        samplingButtonNode.setStyle("-fx-background-color: #005288; -fx-font-weight: bold; -fx-text-fill: white;");
        samplingButtonNode.setOnMouseEntered(e ->samplingButtonNode.setStyle("-fx-background-color: #0070C0; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
        samplingButtonNode.setOnMouseExited(e -> samplingButtonNode.setStyle("-fx-background-color: #005288; -fx-font-weight: bold; -fx-text-fill: white; "));


        sevButtonNode.setStyle("-fx-background-color:  #19798B; -fx-font-weight: bold; -fx-text-fill: white;"); // Red color for failure
        sevButtonNode.setOnMouseEntered(e -> sevButtonNode.setStyle("-fx-background-color:  #0070C0; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")); // Darker red on hover
        sevButtonNode.setOnMouseExited(e ->sevButtonNode.setStyle("-fx-background-color:  #19798B; -fx-font-weight: bold; -fx-text-fill: white; ")); // Original red on exit

        noncButtonNode.setStyle("-fx-background-color: #675645; -fx-font-weight: bold; -fx-text-fill: white;"); // Red color for failure
        noncButtonNode.setOnMouseEntered(e -> noncButtonNode.setStyle("-fx-background-color: #766657; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")); // Darker red on hover
        noncButtonNode.setOnMouseExited(e ->noncButtonNode.setStyle("-fx-background-color: #675645; -fx-font-weight: bold; -fx-text-fill: white; ")); // Original red on exit

        ssButtonNode.setStyle("-fx-background-color: #cab849; -fx-font-weight: bold; -fx-text-fill: white;"); // Red color for failure
        ssButtonNode.setOnMouseEntered(e -> ssButtonNode.setStyle("-fx-background-color: #d0c05d; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")); // Darker red on hover
        ssButtonNode.setOnMouseExited(e ->ssButtonNode.setStyle("-fx-background-color: #cab849; -fx-font-weight: bold; -fx-text-fill: white; ")); // Original red on exit

        pprButtonNode.setStyle("-fx-background-color: #675645; -fx-font-weight: bold; -fx-text-fill: white;"); // Red color for failure
        pprButtonNode.setOnMouseEntered(e -> pprButtonNode.setStyle("-fx-background-color: #766657; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")); // Darker red on hover
        pprButtonNode.setOnMouseExited(e ->pprButtonNode.setStyle("-fx-background-color: #675645; -fx-font-weight: bold; -fx-text-fill: white; ")); // Original red on exit

        parlimitsButtonNode.setStyle("-fx-background-color: #4d784e; -fx-font-weight: bold; -fx-text-fill: white;");
        parlimitsButtonNode.setOnMouseEntered(e -> parlimitsButtonNode.setStyle("-fx-background-color: #5E855F; -fx-font-weight: bold; -fx-text-fill: white; -fx-scale-x: 1.02; -fx-scale-y: 1.1;"));
        parlimitsButtonNode.setOnMouseExited(e ->parlimitsButtonNode.setStyle("-fx-background-color: #4d784e; -fx-font-weight: bold; -fx-text-fill: white; "));

        rtcButtonNode.setOnAction(e -> {
            ReadRTC rtcRead = new ReadRTC(selectedPort,confStage,log);
            Optional<String> result = rtcRead.showAndWait();

            result.ifPresent(finalOutput -> {
                if(finalOutput.equals("previous")) {
                    item12.fire();
                }else{
                    if(confDialog.isShowing()){
                        confDialog.close();
                    }
                }
            });
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.add(rtcButtonNode, 0, 0);
        grid.add(sevButtonNode, 1, 0);
        grid.add(samplingButtonNode, 2, 0);
        grid.add(noncButtonNode, 0, 1);
        grid.add(ssButtonNode, 1, 1);
        grid.add(pprButtonNode, 2, 1);
        grid.add(parlimitsButtonNode, 0, 2, 3, 1); // Span 3 columns

        Scene scene = new Scene(grid, 500, 200);
        confStage.setScene(scene);
        confDialog.show(); // Use showAndWait to wait for the dialog to be closed

    }


}
