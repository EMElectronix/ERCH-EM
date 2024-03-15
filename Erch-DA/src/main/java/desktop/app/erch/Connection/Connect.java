package desktop.app.erch.Connection;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bConn;


public class Connect {

      Logger log = LogManager.getLogger(Connect.class);
      String errorMessage = "Error";

      public  String ecuSN ;
      public  String vehSN ;

       public String vehEN ;

       public String connectedPort;

      public boolean Connection(SerialPort selectedPort){

          if(selectedPort!=null){
              try{
                  boolean portOpened = selectedPort.openPort();
                  if(portOpened){
                      Receiver conn = new Receiver();
                      String data = conn.receiveFrame(selectedPort,"Connection","a01",36,bConn(),log);

                      if(data!=null) {

                          ecuSN = data.substring(2, 10);
                          vehSN = data.substring(10, 18);
                          vehEN = data.substring(18, 26);

                          log.info("ECUsn : {}" , ecuSN);
                          log.info("VEHsn : {}" , vehSN);
                          log.info("VEHen : {}" , vehEN);

                          return true;
                      }
                      else{
                          log.fatal("Connection Establish Failed");
                          return false;
                      }


                  }else{
                      log.error("Failed to Open Comport");
                      sof(errorMessage, "Failed to open COM port ❗", false);
                      return false;
                  }

              }catch (Exception connectionException){
                  log.error("Error opening COM port: {}" , connectionException.getMessage());
                  sof(errorMessage, "Error opening COM port ❗", false);
                  return false;
              }

          } else {

              sof(errorMessage, "Please select a COM port ❗", false);
              return false;

          }


     }

     public String getEcuSN(){
          return this.ecuSN;
     }

     public String getVehSN(){
          return this.vehSN;
     }

    public String getVehEN(){
        return this.vehEN;
    }

    public String getConnectedPort(){
        return this.connectedPort;
    }


}
