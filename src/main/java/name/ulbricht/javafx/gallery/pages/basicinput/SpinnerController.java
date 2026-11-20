package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Demo page for {@link Spinner}: integer, double and list value factories and an editable spinner with the
 * different arrow style classes.
 */
public class SpinnerController {

    private static final String DEFAULT_STYLE = "(default)";
    private static final List<String> ARROW_STYLES = List.of(
            Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL,
            Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL,
            Spinner.STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL,
            Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL,
            Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

    @FXML
    private Spinner<Integer> intSpinner;
    @FXML
    private Label intOutput;
    @FXML
    private CheckBox disableInt;
    @FXML
    private CheckBox wrapInt;
    @FXML
    private Spinner<Double> doubleSpinner;
    @FXML
    private Label doubleOutput;
    @FXML
    private Spinner<String> listSpinner;
    @FXML
    private Label listOutput;
    @FXML
    private Spinner<Integer> editableSpinner;
    @FXML
    private Label editableOutput;
    @FXML
    private CheckBox editable;
    @FXML
    private ComboBox<String> arrowStyle;

    @FXML
    private void initialize() {
        SpinnerValueFactory.IntegerSpinnerValueFactory intFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50, 5);
        intFactory.wrapAroundProperty().bind(wrapInt.selectedProperty());
        intSpinner.setValueFactory(intFactory);
        intSpinner.disableProperty().bind(disableInt.selectedProperty());
        intOutput.textProperty().bind(intSpinner.valueProperty().asString("Value: %d"));

        doubleSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10, 2.5, 0.25));
        doubleOutput.textProperty().bind(doubleSpinner.valueProperty().asString("Value: %.2f"));

        List<String> days = Arrays.stream(DayOfWeek.values())
                .map(day -> day.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .toList();
        SpinnerValueFactory.ListSpinnerValueFactory<String> listFactory =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList(days));
        listFactory.setWrapAround(true);
        listSpinner.setValueFactory(listFactory);
        listOutput.textProperty().bind(listSpinner.valueProperty().asString("Selected: %s"));

        editableSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 42));
        editableSpinner.editableProperty().bind(editable.selectedProperty());
        editableOutput.textProperty().bind(editableSpinner.valueProperty().asString("Committed value: %d"));

        arrowStyle.getItems().add(DEFAULT_STYLE);
        arrowStyle.getItems().addAll(ARROW_STYLES);
        arrowStyle.setValue(DEFAULT_STYLE);
        arrowStyle.valueProperty().addListener((obs, oldStyle, newStyle) -> {
            editableSpinner.getStyleClass().removeAll(ARROW_STYLES);
            if (newStyle != null && !DEFAULT_STYLE.equals(newStyle)) {
                editableSpinner.getStyleClass().add(newStyle);
            }
        });
    }
}
