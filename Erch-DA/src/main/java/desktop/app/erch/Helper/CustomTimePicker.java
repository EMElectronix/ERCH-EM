package desktop.app.erch.Helper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import java.time.LocalTime;

public class CustomTimePicker extends HBox {

    private final ComboBox<String> hourComboBox;
    private final ComboBox<String> minuteComboBox;
    private final ComboBox<String> secondsComboBox;
    private final ComboBox<String> amPmComboBox;

    private final ObjectProperty<LocalTime> selectedTimeProperty = new SimpleObjectProperty<>(null);

    public CustomTimePicker() {
        hourComboBox = new ComboBox<>(FXCollections.observableArrayList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));
        minuteComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
                "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
                "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
        ));
        secondsComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
                "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
                "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
        ));
        amPmComboBox = new ComboBox<>(FXCollections.observableArrayList("AM", "PM"));



        getChildren().addAll(hourComboBox, minuteComboBox,secondsComboBox, amPmComboBox);
    }

    public LocalTime getSelectedTime() {
        String hour = hourComboBox.getValue();
        String minute = minuteComboBox.getValue();
        String second = secondsComboBox.getValue();
        String amPm = amPmComboBox.getValue();


        if (hour != null && minute != null && second != null && amPm != null) {
            int hourValue = Integer.parseInt(hour);
            int minuteValue = Integer.parseInt(minute);
            int secondValue = Integer.parseInt(second);


            if (amPm.equals("PM") && hourValue < 12) {
                hourValue += 12;
            } else if (amPm.equals("AM") && hourValue == 12) {
                hourValue = 0;
            }

            return LocalTime.of(hourValue, minuteValue, secondValue);
        }

        return null;
    }

    public void setValue(LocalTime time) {
        if (time != null) {
            int hour = time.getHour();
            int minute = time.getMinute();
            int second = time.getSecond();

            // Convert hour to 12-hour format
            String amPm = (hour < 12) ? "AM" : "PM";
            if (hour == 0) {
                hour = 12; // 12 AM should be displayed as 12
            } else if (hour > 12) {
                hour -= 12;
            }

            // Set values in combo boxes
            hourComboBox.setValue(String.format("%02d", hour));
            minuteComboBox.setValue(String.format("%02d", minute));
            secondsComboBox.setValue(String.format("%02d", second));
            amPmComboBox.setValue(amPm);
        } else {
            // If the provided time is null, clear all combo box values
            hourComboBox.setValue(null);
            minuteComboBox.setValue(null);
            secondsComboBox.setValue(null);
            amPmComboBox.setValue(null);
        }
    }


    public ObjectProperty<LocalTime> selectedTimeProperty() {
        return selectedTimeProperty;
    }

    public void setPromptText(String hourPrompt, String minutePrompt,String secondsPrompt, String amPmPrompt) {
        hourComboBox.setPromptText(hourPrompt);
        minuteComboBox.setPromptText(minutePrompt);
        secondsComboBox.setPromptText(secondsPrompt);
        amPmComboBox.setPromptText(amPmPrompt);
    }




}
