module desktop.app.erch {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires eu.hansolo.medusa;


    opens desktop.app.erch to javafx.fxml;
    exports desktop.app.erch;
}