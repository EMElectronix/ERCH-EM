module desktop.app.erch {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires eu.hansolo.medusa;


    opens desktop.app.erch to javafx.fxml;
    exports desktop.app.erch;
    exports desktop.app.erch.DataAcess;
    opens desktop.app.erch.DataAcess to javafx.fxml;
}