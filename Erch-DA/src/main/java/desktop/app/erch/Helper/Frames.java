package desktop.app.erch.Helper;

public class Frames {

    /* 'a' represents that frame is sent from ERCH ECU → PC */
    /* 'b' represents that frame is sent from PC → ERCH ECU */

    //Connect Frame from PC → ERCH ECU(b010255-CRC)
    public static byte[] bConn(){
        return new byte[]{0x62, 0x30, 0x31, 0x30, 0x32, 0x35, 0x35, (byte) 0x7A, 0x59,0x0D, 0x0A,0x0D};
    }

    //Disconnect Frame from PC → ERCH ECU(b020277-CRC)
    public static byte[] bDisConn(){
        return new byte[]{0x62,0x30,0x32,0x30,0x32,0x37,0x37, (byte)0xDB, (byte)0x9D,0x0D, 0x0A,0x0D};
    }


    //Realtime Start Frame from PC → ERCH ECU(b110211-CRC)
    public static byte[] bRealtimeStart(){
        return new byte[]{0x62, 0x31, 0x31, 0x30, 0x32, 0x31, 0x31, (byte)0xA8, 0x5B, 0x0D, 0x0A, 0x0D};
    }


    //Realtime Stop Frame from PC → ERCH ECU(b120211-CRC)
    public static byte[] bRealtimeStop(){
        return new byte[]{0x62, 0x31, 0x32, 0x30, 0x32, 0x31, 0x31, (byte)0xA8, 0x1F, 0x0D, 0x0A, 0x0D};
    }






}
