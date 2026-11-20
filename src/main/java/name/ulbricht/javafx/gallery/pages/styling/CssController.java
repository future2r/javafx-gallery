package name.ulbricht.javafx.gallery.pages.styling;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Demo page for CSS styling: inline styles, style classes from a {@code data:} URL stylesheet, a custom
 * {@link PseudoClass} and looked-up colors.
 */
public class CssController {

    private static final Map<String, String> INLINE_PRESETS = new LinkedHashMap<>();

    static {
        INLINE_PRESETS.put("Pill with gradient", """
                -fx-background-color: linear-gradient(to bottom, #61a2b1, #2a5058);
                -fx-background-radius: 20;
                -fx-text-fill: white;
                -fx-padding: 10 24;""");
        INLINE_PRESETS.put("Outlined", """
                -fx-background-color: transparent;
                -fx-border-color: #e5734b;
                -fx-border-width: 2;
                -fx-border-radius: 6;
                -fx-text-fill: #e5734b;""");
        INLINE_PRESETS.put("Large and bold", """
                -fx-font-size: 20px;
                -fx-font-weight: bold;""");
        INLINE_PRESETS.put("Rotated with shadow", """
                -fx-rotate: -6;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.45), 12, 0, 3, 3);""");
        INLINE_PRESETS.put("No inline style", "");
    }

    private static final String CLASS_STYLESHEET = """
            .demo-highlight {
                -fx-background-color: #ffd54f;
                -fx-text-fill: #202020;
                -fx-padding: 10 18;
            }
            .demo-rounded {
                -fx-background-radius: 18;
                -fx-border-radius: 18;
            }
            .demo-outlined {
                -fx-border-color: #3d8fd1;
                -fx-border-width: 3;
                -fx-padding: 10 18;
            }
            .demo-large {
                -fx-font-size: 22px;
                -fx-font-weight: bold;
            }
            """;

    private static final String PSEUDO_STYLESHEET = """
            .validated-field:invalid {
                -fx-border-color: #d9534f;
                -fx-border-width: 2;
                -fx-border-radius: 3;
            }
            .validated-field:valid {
                -fx-border-color: #56b16a;
                -fx-border-width: 2;
                -fx-border-radius: 3;
            }
            """;

    private static final PseudoClass VALID = PseudoClass.getPseudoClass("valid");
    private static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}");

    private static final Color MODENA_BASE = Color.web("#ececec");
    private static final Color MODENA_ACCENT = Color.web("#0096c9");

    @FXML
    private Button inlineTarget;
    @FXML
    private Label inlineOutput;
    @FXML
    private ComboBox<String> inlinePreset;
    @FXML
    private TextArea inlineStyle;

    @FXML
    private VBox classContainer;
    @FXML
    private Label classTarget;
    @FXML
    private Label classOutput;
    @FXML
    private TextArea stylesheetText;
    @FXML
    private CheckBox classHighlight;
    @FXML
    private CheckBox classRounded;
    @FXML
    private CheckBox classOutlined;
    @FXML
    private CheckBox classLarge;

    @FXML
    private VBox pseudoContainer;
    @FXML
    private TextField emailField;
    @FXML
    private Label pseudoOutput;
    @FXML
    private CheckBox validationEnabled;

    @FXML
    private VBox lookedUpContainer;
    @FXML
    private Label lookedUpOutput;
    @FXML
    private ColorPicker baseColor;
    @FXML
    private ColorPicker accentColor;

    @FXML
    private void initialize() {
        initInlineStyles();
        initStyleClasses();
        initPseudoClass();
        initLookedUpColors();
    }

    /** Encodes a stylesheet as a {@code data:} URL that can be added to {@code getStylesheets()}. */
    private static String dataUrl(String css) {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }

    // --- Example 1 -----------------------------------------------------------------------------------------

    private void initInlineStyles() {
        inlinePreset.getItems().setAll(INLINE_PRESETS.keySet());
        inlinePreset.valueProperty().addListener((obs, o, preset) -> {
            if (preset != null) {
                inlineStyle.setText(INLINE_PRESETS.get(preset));
                onApplyInlineStyle();
            }
        });
        inlinePreset.getSelectionModel().selectFirst();
    }

    @FXML
    private void onApplyInlineStyle() {
        String style = inlineStyle.getText() == null ? "" : inlineStyle.getText().strip();
        inlineTarget.setStyle(style);
        inlineOutput.setText(style.isEmpty()
                ? "button.setStyle(\"\")"
                : "button.setStyle(\"" + style.replaceAll("\\s*\\n\\s*", " ") + "\")");
    }

    // --- Example 2 -----------------------------------------------------------------------------------------

    private void initStyleClasses() {
        stylesheetText.setText(CLASS_STYLESHEET);
        onApplyStylesheet();
        for (CheckBox box : new CheckBox[]{classHighlight, classRounded, classOutlined, classLarge}) {
            box.selectedProperty().addListener((obs, o, selected) -> {
                if (selected) {
                    classTarget.getStyleClass().add(box.getText());
                } else {
                    classTarget.getStyleClass().remove(box.getText());
                }
                showStyleClasses();
            });
            if (box.isSelected()) {
                classTarget.getStyleClass().add(box.getText());
            }
        }
        showStyleClasses();
    }

    private void showStyleClasses() {
        classOutput.setText("getStyleClass() = " + classTarget.getStyleClass());
    }

    @FXML
    private void onApplyStylesheet() {
        String css = stylesheetText.getText() == null ? "" : stylesheetText.getText();
        classContainer.getStylesheets().setAll(dataUrl(css));
    }

    // --- Example 3 -----------------------------------------------------------------------------------------

    private void initPseudoClass() {
        pseudoContainer.getStylesheets().add(dataUrl(PSEUDO_STYLESHEET));
        emailField.textProperty().addListener((obs, o, n) -> validateEmail());
        validationEnabled.selectedProperty().addListener((obs, o, n) -> validateEmail());
        validateEmail();
    }

    private void validateEmail() {
        String text = emailField.getText() == null ? "" : emailField.getText().strip();
        boolean active = validationEnabled.isSelected() && !text.isEmpty();
        boolean valid = EMAIL.matcher(text).matches();
        emailField.pseudoClassStateChanged(VALID, active && valid);
        emailField.pseudoClassStateChanged(INVALID, active && !valid);
        if (!active) {
            pseudoOutput.setText("No pseudo class active.");
        } else {
            pseudoOutput.setText("Pseudo class :" + (valid ? "valid" : "invalid") + " is active.");
        }
    }

    // --- Example 4 -----------------------------------------------------------------------------------------

    private void initLookedUpColors() {
        resetPickers();
        baseColor.setOnAction(e -> applyLookedUpColors());
        accentColor.setOnAction(e -> applyLookedUpColors());
        lookedUpOutput.setText("Pick a color to override the looked-up colors of the container.");
    }

    private void resetPickers() {
        baseColor.setValue(MODENA_BASE);
        accentColor.setValue(MODENA_ACCENT);
    }

    private void applyLookedUpColors() {
        String style = "-fx-base: " + web(baseColor.getValue()) + "; -fx-accent: " + web(accentColor.getValue())
                + "; -fx-focus-color: " + web(accentColor.getValue()) + ";";
        lookedUpContainer.setStyle(style);
        lookedUpOutput.setText("container.setStyle(\"" + style + "\")");
    }

    @FXML
    private void onResetLookedUp() {
        resetPickers();
        lookedUpContainer.setStyle("");
        lookedUpOutput.setText("container.setStyle(\"\") – the theme's colors apply again.");
    }

    private static String web(Color color) {
        return String.format(Locale.ROOT, "#%02x%02x%02x",
                Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255));
    }
}
