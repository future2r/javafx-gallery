package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Demo page for {@link DatePicker}: basic usage, a day cell factory that disables dates, and custom
 * format/chronology.
 */
public class DatePickerController {

    private static final DateTimeFormatter LONG_FORMAT =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.ENGLISH);

    @FXML
    private DatePicker simplePicker;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disablePicker;
    @FXML
    private CheckBox editablePicker;
    @FXML
    private CheckBox weekNumbers;
    @FXML
    private DatePicker restrictedPicker;
    @FXML
    private Label restrictedOutput;
    @FXML
    private CheckBox blockWeekends;
    @FXML
    private DatePicker formatPicker;
    @FXML
    private Label formatOutput;
    @FXML
    private ComboBox<String> pattern;
    @FXML
    private ComboBox<Chronology> chronology;

    @FXML
    private void initialize() {
        simplePicker.disableProperty().bind(disablePicker.selectedProperty());
        simplePicker.editableProperty().bind(editablePicker.selectedProperty());
        simplePicker.showWeekNumbersProperty().bind(weekNumbers.selectedProperty());
        simplePicker.valueProperty().addListener((obs, oldDate, date) ->
                simpleOutput.setText(date == null ? "No date selected." : "You selected " + LONG_FORMAT.format(date)));

        restrictedPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    return;
                }
                boolean weekend = item.getDayOfWeek() == DayOfWeek.SATURDAY
                        || item.getDayOfWeek() == DayOfWeek.SUNDAY;
                setDisable(item.isBefore(LocalDate.now()) || (weekend && blockWeekends.isSelected()));
            }
        });
        restrictedPicker.valueProperty().addListener((obs, oldDate, date) ->
                restrictedOutput.setText(date == null ? "" : "Appointment on " + LONG_FORMAT.format(date)));
        restrictedOutput.setText("Past dates cannot be selected.");

        pattern.getItems().setAll("yyyy-MM-dd", "dd.MM.yyyy", "MM/dd/yyyy", "d MMMM yyyy", "EEE, d MMM yy");
        pattern.valueProperty().addListener((obs, oldPattern, newPattern) -> applyPattern(newPattern));
        pattern.setValue("yyyy-MM-dd");

        chronology.getItems().setAll(IsoChronology.INSTANCE, JapaneseChronology.INSTANCE,
                ThaiBuddhistChronology.INSTANCE, MinguoChronology.INSTANCE, HijrahChronology.INSTANCE);
        chronology.setValue(IsoChronology.INSTANCE);
        chronology.valueProperty().addListener((obs, oldChronology, newChronology) -> {
            if (newChronology != null) {
                formatPicker.setChronology(newChronology);
                boolean iso = newChronology == IsoChronology.INSTANCE;
                pattern.setDisable(!iso);
                if (iso) {
                    applyPattern(pattern.getValue());
                } else {
                    // the pattern converter formats ISO dates only, so use the chronology-aware default converter
                    formatPicker.setConverter(null);
                }
            }
        });

        formatPicker.setValue(LocalDate.now());
        formatPicker.valueProperty().addListener((obs, oldDate, date) -> updateFormatOutput());
        updateFormatOutput();
    }

    private void applyPattern(String newPattern) {
        if (newPattern == null) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(newPattern, Locale.ENGLISH);
        formatPicker.setPromptText(newPattern);
        formatPicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : formatter.format(date);
            }

            @Override
            public LocalDate fromString(String text) {
                if (text == null || text.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(text.trim(), formatter);
                } catch (DateTimeParseException e) {
                    return formatPicker.getValue();
                }
            }
        });
        updateFormatOutput();
    }

    private void updateFormatOutput() {
        LocalDate date = formatPicker.getValue();
        formatOutput.setText(date == null ? "No date" : "ISO value: " + date);
    }
}
