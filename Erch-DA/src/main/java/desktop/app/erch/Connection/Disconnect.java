package desktop.app.erch.Connection;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bDisConn;

public class Disconnect {
    String errorMessage = "Error";
    Logger log = LogManager.getLogger(Disconnect.class);

    public boolean disConnect(SerialPort selectedPort){

        try{
            boolean portOpened = selectedPort.openPort();
            if(portOpened){
                Receiver disConn = new Receiver();
                String data = disConn.receiveFrame(selectedPort,"Disconnection","a02",12,bDisConn(),log);
                if(data.equals("88")){
                    return true;
                }else{
                    return false;
                }


            }else{
                log.error("Failed to Open Comport");
                sof(errorMessage, "Failed to open COM port ❗", false);
                return false;
            }


        }
        catch(Exception disconnectionException){
            log.error("Error opening COM port: {}" , disconnectionException.getMessage());
            sof(errorMessage, "Error opening COM port ❗", false);

        }


        return false;
    }


}
