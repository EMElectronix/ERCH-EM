package desktop.app.erch.Helper;


import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import java.time.LocalDate;


public class DateRangePicker {

    public static void setupDateListeners(DatePicker startDate, DatePicker endDate) {
        startDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                endDate.setDayCellFactory(createDateCellFactory(newValue, null));
            }
        });

        endDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                startDate.setDayCellFactory(createDateCellFactory(null, newValue));
            }
        });
    }

    private static Callback<DatePicker, DateCell> createDateCellFactory(LocalDate startDate, LocalDate endDate) {
        return picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (startDate != null && endDate == null) {
                    setDisable(date.isBefore(startDate));
                } else if (endDate != null && startDate == null) {
                    setDisable(date.isAfter(endDate));
                }
            }
        };
    }

    public static StringConverter<LocalDate> getDateConverter() {
        return new LocalDateStringConverter();
    }
}
