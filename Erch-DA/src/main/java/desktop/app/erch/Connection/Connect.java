package desktop.app.erch.Connection;

import com.fazecast.jSerialComm.SerialPort;
import desktop.app.erch.Helper.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static desktop.app.erch.Helper.Common.error;
import static desktop.app.erch.Helper.Common.failed;
import static desktop.app.erch.Helper.Display.sof;
import static desktop.app.erch.Helper.Frames.bConn;


public class Connect {

      Logger log = LogManager.getLogger(Connect.class);
      String errorMessage = "Error";

      public static String ecuSN ;
      public static String vehMN ;

       public static String vehEN ;

       public static String connectedPort;

      public boolean Connection(SerialPort selectedPort){

          /***
           Connection displays the dialog where Connection can be established
           */

          if(selectedPort!=null){
              try{
                  boolean portOpened = selectedPort.openPort();
                  if(portOpened){
                      Receiver conn = new Receiver();
                      String data = conn.receiveFrame(selectedPort,"Connection","a01",36,bConn(),log);

                      if(data!=null) {

                          ecuSN = data.substring(2, 10);
                          vehMN = data.substring(10, 18);
                          vehEN = data.substring(18, 26);

                          log.info("ECUsn : {}" , ecuSN);
                          log.info("VEHmn : {}" , vehMN);
                          log.info("VEHen : {}" , vehEN);

                          return true;
                      }
                      else{
                          log.fatal("Connection Establish Failed");
                          return false;
                      }


                  }else{
                      failed(log);
                      return false;
                  }

              }catch (Exception e){
                  error(e,log);
                  return false;
              }

          } else {

              sof(errorMessage, "Please select a COM port ❗", false);
              return false;

          }


     }

     public static String getEcuSN(){
          return ecuSN;
     }

     public static String getVehMN(){
          return vehMN;
     }

    public static String getVehEN(){
        return vehEN;
    }

    public static String getConnectedPort(){
        return connectedPort;
    }


}
