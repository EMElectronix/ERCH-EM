package desktop.app.erch.Helper;

public class Query {

    public static String connQuery(String Serial_no, String Vehicle_Model_No, String Engine_No) {
        /*
        connQuery checks Devices table in erch.db database to find the sequence of
        'Serial_no', 'Vehicle_Model_No', 'Engine_No' exists in Table, if not
        it creates New record.
         */
        return "INSERT INTO Devices (\"ERCH Serial No.\", \"Vmodel No.\", \"Engine No.\") " +
                "SELECT '" + Serial_no + "', '" + Vehicle_Model_No + "', '" + Engine_No + "' " +
                "WHERE NOT EXISTS (SELECT 1 FROM Devices " +
                "WHERE \"ERCH Serial No.\" = '" + Serial_no + "' " +
                "AND \"Vmodel No.\" = '" + Vehicle_Model_No + "' " +
                "AND \"Engine No.\" = '" + Engine_No + "')";
    }


    public static String smeQuery(String Serial_no, String Vehicle_Model_No, String Engine_No, String New_Serial_no, String New_Vehicle_Model_No, String New_Engine_No) {
        /*
        smeQuery updates 'Serial_no', 'Vehicle_Model_No', 'Engine_No' with new values in Devices table
        in erch.db database when any of 3 is changed
         */

        return "UPDATE Devices " +
                "SET \"ERCH Serial No.\" = '" + New_Serial_no + "', " +
                "\"Vmodel No.\" = '" + New_Vehicle_Model_No + "', " +
                "\"Engine No.\" = '" + New_Engine_No + "' " +
                "WHERE \"ERCH Serial No.\" = '" + Serial_no + "' " +
                "AND \"Vmodel No.\" = '" + Vehicle_Model_No + "' " +
                "AND \"Engine No.\" = '" + Engine_No + "' " +
                "AND EXISTS (SELECT 1 FROM Devices " +
                "WHERE \"ERCH Serial No.\" = '" + Serial_no + "' " +
                "AND \"Vmodel No.\" = '" + Vehicle_Model_No + "' " +
                "AND \"Engine No.\" = '" + Engine_No + "')";
    }

    public static String uploadQuery(){
        return "INSERT INTO Datalog (" +
                "\"Date\", \"Time\", \"Cylinder Head Temperature7(°C)\", \"Cylinder Head Temperature8(°C)\", " +
                "\"Engine Oil Temperature(°C)\", \"Ambient Air Temperature(°C)\", \"Ambient Air Pressure(bar)\", " +
                "\"Exhaust Air Pressure(bar)\", \"Engine Oil Pressure(bar)\", \"Battery Voltage(V)\", " +
                "\"Engine Speed(RPM)\", \"Turbocharger Speed(RPM)\", \"Cooling Fan Speed(RPM)\", " +
                "\"Vehicle Speed(RPM)\", \"Alternator Speed(RPM)\", \"Mean Sea Level Altitude(Mtr)\", " +
                "\"Exhaust Brake Status\", \"Cooling Valve Status\", \"Fuel Sol Valve Status\", " +
                "\"Heater Sol Status\", \"Engine Started Count\", \"Engine Overspeed Count\", " +
                "\"Engine Overheat Count\", \"Vehicle Overspeed Count\", \"Veh No.\", \"Eng No.\", \"Erch No.\" ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public static String errorQuery(){
        return "DELETE FROM Datalog WHERE Date = 'Error'";
    }

    public static String deleteQuery(){
        return "DELETE FROM Datalog WHERE `Veh No.` = ? AND `Eng No.` = ? AND `Erch No.` = ?";
    }

//    public static String fetchAllQuery(String veh_no, String eng_no, String erch_no){
//        return "SELECT * FROM Datalog WHERE `Veh No.` = "+veh_no+" AND `Eng No.` = "+eng_no+" AND `Erch No.` = "+erch_no+"";
//    }

    public static String fetchAllQuery() {
        return "SELECT * FROM Datalog WHERE `Veh No.` = ? AND `Eng No.` = ? AND `Erch No.` = ?";
    }




}
