package desktop.app.erch.Helper;


public class CRC {

    public static int calculateCRC(byte[] data) {
        /*
         calculateCRC is used in most of the cases
         to verify the CRC after receiving the frame,

         args    : data i.e frame
         returns : CRC value in int
         */

        int crc = 0xFFFF; // Initial value

        for (byte b : data) {
            crc ^= b & 0xFF; // XOR with each byte
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001; // XOR with Modbus polynomial
                } else {
                    crc >>= 1;
                }
            }
        }

        return crc & 0xFFFF; // Ensure that only 16 bits are returned
    }

    public static String calculateCRCHex(byte[] data){

        /*
         calculateCRCHEX is used generate CRC in hexadecimal,
         usually used while sending the frame,


         args    : data i.e frame
         returns : CRC value in int
         */


        int crc = 0xFFFF; // Initial value

        for (byte b : data) {
            crc ^= b & 0xFF; // XOR with each byte
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001; // XOR with Modbus polynomial
                } else {
                    crc >>= 1;
                }
            }
        }

        // Convert the CRC to a hex string
        return String.format("%04X", crc & 0xFFFF);
    }


}
