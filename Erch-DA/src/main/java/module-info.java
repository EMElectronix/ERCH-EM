module desktop.app.erch {
    requires javafx.controls;
    requires javafx.fxml;


    opens desktop.app.erch to javafx.fxml;
    exports desktop.app.erch;
}