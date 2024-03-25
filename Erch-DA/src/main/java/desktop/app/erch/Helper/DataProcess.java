package desktop.app.erch.Helper;

import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataProcess {


    private static String processDate(String dateValue) {
        // Assuming dateValue is in the format dd/mm/yyyy
        String dataByte = dateValue.substring(0, 3);
        String day = dateValue.substring(3, 5);
        String month = dateValue.substring(5, 7);
        String year = dateValue.substring(7, 11);
        String error ="Error";

        if (Integer.parseInt(day) > 31 || Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1 || Integer.parseInt(day) < 1) {
            return error;  // Accept the change
        } else {
            return year + "-" + month + "-" + day;
        }

    }

    private static String processTime(String timeValue) {
        // Assuming timeValue is in the format HHmmss
        String hours = timeValue.substring(0, 2);
        String minutes = timeValue.substring(2, 4);
        String seconds = timeValue.substring(4, 6);
        String meridian = timeValue.endsWith("0") ? "AM" : "PM";
        return hours + ":" + minutes + ":" + seconds + " " + meridian;
    }



    private static double processPressure(String pressureValue,float denominator) {
        /*
        processPressure is used to process the Pressure Value
        */
        String p1 = pressureValue.substring(0, 3);
        double div = Double.parseDouble(p1) / denominator;
        return div;
    }

    private static String processTemp(String value) {
        /*
        processTemp is used to process the Temperature Value
        */
        String temp = value.substring(1, 4);
        if (temp.equals("888")) {
            return "SHORT";
        }
        else if (temp.equals("999")) {
            return "OPEN";
        }

        return temp;
    }




    private static String tempSign(String signValue){
        /*
        tempSign checks the signValue and returns whether it is Positive and Negative
        */
        return (signValue.startsWith("1"))? "-" : "+";
    }

    private static String processStatus(String statusValue) {
        /*
        processStatus checks the statusValue and returns whether it is Positive and Negative
        */
        return statusValue.equals("1") ? "ON" : "OFF";
    }

    public static String[] processAndShowData(byte[] response, boolean realTime, Logger log) {
        /*
        processAndShowData is realtime data where each value is updated in its respective Gauge.
         */

        // Convert byte array to string
        String dataString = new String(response, StandardCharsets.UTF_8);

        // Print raw data for inspection
        log.info("Raw Data: {}" , dataString);

        // Define the lengths of each field
        int[] fieldLengths = {
                11,  //0.date
                7,  //1.time
                4,  //2.cylinder head temp 7
                4,  //3.cylinder head temp 8
                4,  //4.engine oil temp
                3,  //5.ambient air temp
                3,  //6.ambient air pressure
                3,  //7.exhaust air pressure
                3,  //8.engine oil pressure
                3,  //9.battery voltage
                5,  //10.engine speed
                6,  //11.turbocharger speed
                5,  //12.cooling fan speed
                3,  //13.vehicle speed
                5,  //14.AlternatorSpeed
                4,  //15.MeanSeaLevelAltitude
                1,  //16.AcceleratorPedalInput
                1,  //17.ClutchPedalInput
                1,  //18.BreakLeverInput
                1,  //19.HighAltitudeSwitchInput
                1,  //20.BrakeSolStatus
                1,  //21.CoolingSolStatus
                1,  //22.FuelSolStatus
                1,  //23.PreheaterSolStatus
                10, //24.EngineStartedCount
                10, //25.EngineOverspeedCount
                10, //26.EngineOverheatCount
                10  //27.VehicleOverspeedCount
        };


        // Process the data based on the lengths
        int startIndex = 0;
        List<String> dataValues = new ArrayList<>();
        for (int i = 0; i < fieldLengths.length; i++) {
            if (!realTime && i >= 16 && i <= 19) {     // Skip fields 16,17,18,19 if realTime is false
                continue;
            }
            int length = fieldLengths[i];
            int endIndex = startIndex + length;
            String fieldValue = dataString.substring(startIndex, endIndex);
            String processedValue = processRealtimeValue(i, fieldValue);
            dataValues.add(processedValue);
            startIndex = endIndex;
        }

        return dataValues.toArray(new String[0]);

    }

    private static String processRealtimeValue(int fieldIndex, String fieldValue) {
        /*
        processRealtimeValue so that each value is stored in its respective field.
         */
        switch (fieldIndex) {
            case 0: return processDate(fieldValue);
            case 1: return processTime(fieldValue);
            case 2,3,4:
                String tempSignResult = tempSign(fieldValue);
                String processTempResult = processTemp(fieldValue);
                return tempSignResult + processTempResult;
            case 5:
                return tempSign(fieldValue)+fieldValue.substring(1,3);
            case 6:
                return String.valueOf(processPressure(fieldValue,100));
            case 7,8:
                return String.valueOf(processPressure(fieldValue,10));
            case 9:
                return String.valueOf(Float.parseFloat(fieldValue)/10);
            case 16,17,18,19,20,21,22,23:
                return processStatus(fieldValue);

            default:
                return fieldValue.trim(); // Default processing
        }
    }



}
