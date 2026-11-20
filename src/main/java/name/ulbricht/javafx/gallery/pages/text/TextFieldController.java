package name.ulbricht.javafx.gallery.pages.text;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.UnaryOperator;

/**
 * Demo page for {@link TextField}: basic usage, prompt text and alignment, and {@link TextFormatter}s for
 * numeric and upper-case input.
 */
public class TextFieldController {

    private static final int MAX_UPPER_LENGTH = 10;

    @FXML
    private TextField simpleField;
    @FXML
    private Label liveOutput;
    @FXML
    private Label actionOutput;
    @FXML
    private CheckBox disableField;
    @FXML
    private CheckBox editableField;
    @FXML
    private TextField promptField;
    @FXML
    private TextField promptText;
    @FXML
    private ComboBox<Pos> alignment;
    @FXML
    private TextField numericField;
    @FXML
    private Label numericOutput;
    @FXML
    private TextField upperField;
    @FXML
    private Label upperOutput;

    @FXML
    private void initialize() {
        simpleField.disableProperty().bind(disableField.selectedProperty());
        simpleField.editableProperty().bind(editableField.selectedProperty());
        liveOutput.textProperty().bind(simpleField.textProperty().map(text -> "Text: \"" + text + "\""));

        promptField.promptTextProperty().bind(promptText.textProperty());
        alignment.getItems().setAll(Pos.CENTER_LEFT, Pos.CENTER, Pos.CENTER_RIGHT);
        alignment.setValue(Pos.CENTER_LEFT);
        promptField.alignmentProperty().bind(alignment.valueProperty());

        UnaryOperator<TextFormatter.Change> digitsOnly =
                change -> change.getControlNewText().matches("\\d{0,9}") ? change : null;
        TextFormatter<Integer> numberFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, digitsOnly);
        numericField.setTextFormatter(numberFormatter);
        numberFormatter.valueProperty().addListener((obs, oldValue, value) -> updateNumericOutput(value));
        updateNumericOutput(numberFormatter.getValue());

        UnaryOperator<TextFormatter.Change> upperCase = change -> {
            change.setText(change.getText().toUpperCase());
            return change.getControlNewText().length() <= MAX_UPPER_LENGTH ? change : null;
        };
        upperField.setTextFormatter(new TextFormatter<String>(upperCase));
        upperOutput.textProperty().bind(upperField.lengthProperty()
                .asString("%d of " + MAX_UPPER_LENGTH + " characters"));
    }

    @FXML
    private void onSimpleAction() {
        actionOutput.setText("onAction fired with: " + simpleField.getText());
    }

    private void updateNumericOutput(Integer value) {
        numericOutput.setText(value == null
                ? "Committed value: none"
                : "Committed value: " + value + " (doubled: " + (2L * value) + ")");
    }
}
