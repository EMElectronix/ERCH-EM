package desktop.app.erch.Connection;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static desktop.app.erch.Helper.Common.*;
import static desktop.app.erch.Helper.Frames.bDisConn;

public class Disconnect {
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
                failed(log);
                return false;
            }


        }
        catch(Exception e){
            error(e,log);
        }


        return false;
    }


}
