package desktop.app.erch.Helper;

public class DataProcess {


    public static double processPressure(String pressureValue,float denominator) {
        /*
        processPressure is used to process the Pressure Value
        */
        String p1 = pressureValue.substring(0, 3);
        double div = Double.parseDouble(p1) / denominator;
        return div;
    }

    public static String processTemp(String value) {
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


    public static String tempSign(String signValue){
        /*
        tempSign checks the signValue and returns whether it is Positive and Negative
        */
        return (signValue.startsWith("1"))? "-" : "+";
    }

    public static String processStatus(String statusValue) {
        /*
        processStatus checks the statusValue and returns whether it is Positive and Negative
        */
        return statusValue.equals("1") ? "ON" : "OFF";
    }



}
