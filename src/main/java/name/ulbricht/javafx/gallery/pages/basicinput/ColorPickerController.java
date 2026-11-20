package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Locale;
import java.util.Map;

/**
 * Demo page for {@link ColorPicker}: basic usage, the alternative appearances and custom palette colors.
 */
public class ColorPickerController {

    private static final Map<String, String> STYLES = Map.of(
            "Combo box (default)", "",
            "Button", ColorPicker.STYLE_CLASS_BUTTON,
            "Split button", ColorPicker.STYLE_CLASS_SPLIT_BUTTON);

    @FXML
    private ColorPicker simplePicker;
    @FXML
    private Rectangle simpleSwatch;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disablePicker;
    @FXML
    private ColorPicker stylePicker;
    @FXML
    private Label styleOutput;
    @FXML
    private ComboBox<String> pickerStyle;
    @FXML
    private ColorPicker customPicker;
    @FXML
    private Rectangle customSwatch;

    @FXML
    private void initialize() {
        simplePicker.setValue(Color.web("#3d8fd1"));
        simplePicker.disableProperty().bind(disablePicker.selectedProperty());
        simpleSwatch.fillProperty().bind(simplePicker.valueProperty());
        simplePicker.valueProperty().addListener((obs, oldColor, color) -> simpleOutput.setText(describe(color)));
        simpleOutput.setText(describe(simplePicker.getValue()));

        stylePicker.setValue(Color.web("#e5734b"));
        stylePicker.setOnAction(e -> styleOutput.setText("onAction: " + describe(stylePicker.getValue())));
        pickerStyle.getItems().setAll("Combo box (default)", "Button", "Split button");
        pickerStyle.setValue("Combo box (default)");
        pickerStyle.valueProperty().addListener((obs, oldStyle, newStyle) -> {
            stylePicker.getStyleClass().removeAll(ColorPicker.STYLE_CLASS_BUTTON, ColorPicker.STYLE_CLASS_SPLIT_BUTTON);
            String styleClass = newStyle == null ? "" : STYLES.getOrDefault(newStyle, "");
            if (!styleClass.isEmpty()) {
                stylePicker.getStyleClass().add(styleClass);
            }
        });

        customPicker.getCustomColors().setAll(
                Color.web("#0063b1"), Color.web("#e81123"), Color.web("#ffb900"),
                Color.web("#107c10"), Color.web("#5c2d91"), Color.web("#00b7c3"));
        customPicker.setValue(Color.web("#5c2d91"));
        customSwatch.fillProperty().bind(customPicker.valueProperty());
    }

    private static String describe(Color color) {
        if (color == null) {
            return "No color";
        }
        return String.format(Locale.ROOT, "#%02X%02X%02X  (opacity %.2f)",
                Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255), color.getOpacity());
    }
}
