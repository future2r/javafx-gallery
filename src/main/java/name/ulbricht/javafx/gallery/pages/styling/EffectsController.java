package name.ulbricht.javafx.gallery.pages.styling;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Demo page for the effects of {@code javafx.scene.effect}: every effect with its parameters and effect chains.
 */
public class EffectsController {

    private static final List<String> EFFECTS = List.of("None", "DropShadow", "InnerShadow", "GaussianBlur", "Glow",
            "Bloom", "Reflection", "ColorAdjust", "SepiaTone", "MotionBlur", "Lighting");

    @FXML
    private StackPane effectStage;
    @FXML
    private Label effectOutput;
    @FXML
    private ComboBox<String> effectChoice;
    @FXML
    private VBox effectParameters;

    @FXML
    private StackPane chainStage;
    @FXML
    private Label chainOutput;
    @FXML
    private CheckBox chainBlur;
    @FXML
    private CheckBox chainSepia;
    @FXML
    private CheckBox chainReflection;
    @FXML
    private CheckBox chainShadow;

    private Node effectTarget;
    private Node chainTarget;

    @FXML
    private void initialize() {
        effectTarget = createCard();
        placeOnStage(effectStage, effectTarget);
        effectChoice.getItems().setAll(EFFECTS);
        effectChoice.valueProperty().addListener((obs, oldEffect, newEffect) -> selectEffect(newEffect));
        effectChoice.setValue("DropShadow");

        chainTarget = createCard();
        placeOnStage(chainStage, chainTarget);
        for (CheckBox box : List.of(chainBlur, chainSepia, chainReflection, chainShadow)) {
            box.selectedProperty().addListener((obs, o, n) -> updateChain());
        }
        updateChain();
    }

    /** The sample content the effects are applied to. */
    private static Node createCard() {
        Rectangle background = new Rectangle(240, 140);
        background.setArcWidth(28);
        background.setArcHeight(28);
        background.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#5fb0f0")), new Stop(1, Color.web("#1f5fa8"))));

        Text text = new Text("JavaFX");
        text.setFont(Font.font("System", FontWeight.BOLD, 44));
        text.setFill(Color.WHITE);

        Circle badge = new Circle(16, Color.web("#ffd54f"));
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(14));

        StackPane card = new StackPane(background, text, badge);
        card.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
        return card;
    }

    private static void placeOnStage(StackPane stage, Node card) {
        StackPane.setAlignment(card, Pos.TOP_CENTER);
        StackPane.setMargin(card, new Insets(40, 0, 0, 0));
        stage.getChildren().add(card);
    }

    // --- Example 1 -----------------------------------------------------------------------------------------

    private void selectEffect(String name) {
        effectParameters.getChildren().clear();
        Effect effect = name == null ? null : switch (name) {
            case "DropShadow" -> {
                DropShadow shadow = new DropShadow(20, 8, 8, Color.color(0, 0, 0, 0.5));
                slider("Radius", shadow.radiusProperty(), 0, 60);
                slider("Offset X", shadow.offsetXProperty(), -30, 30);
                slider("Offset Y", shadow.offsetYProperty(), -30, 30);
                slider("Spread", shadow.spreadProperty(), 0, 1);
                colorPicker("Color", shadow.colorProperty());
                yield shadow;
            }
            case "InnerShadow" -> {
                InnerShadow shadow = new InnerShadow(20, 6, 6, Color.color(0, 0, 0, 0.7));
                slider("Radius", shadow.radiusProperty(), 0, 60);
                slider("Choke", shadow.chokeProperty(), 0, 1);
                slider("Offset X", shadow.offsetXProperty(), -30, 30);
                slider("Offset Y", shadow.offsetYProperty(), -30, 30);
                colorPicker("Color", shadow.colorProperty());
                yield shadow;
            }
            case "GaussianBlur" -> {
                GaussianBlur blur = new GaussianBlur(10);
                slider("Radius", blur.radiusProperty(), 0, 63);
                yield blur;
            }
            case "Glow" -> {
                Glow glow = new Glow(0.8);
                slider("Level", glow.levelProperty(), 0, 1);
                yield glow;
            }
            case "Bloom" -> {
                Bloom bloom = new Bloom(0.3);
                slider("Threshold", bloom.thresholdProperty(), 0, 1);
                yield bloom;
            }
            case "Reflection" -> {
                Reflection reflection = new Reflection(4, 0.6, 0.5, 0);
                slider("Top offset", reflection.topOffsetProperty(), -20, 60);
                slider("Fraction", reflection.fractionProperty(), 0, 1);
                slider("Top opacity", reflection.topOpacityProperty(), 0, 1);
                slider("Bottom opacity", reflection.bottomOpacityProperty(), 0, 1);
                yield reflection;
            }
            case "ColorAdjust" -> {
                ColorAdjust adjust = new ColorAdjust(0.4, 0, 0, 0);
                slider("Hue", adjust.hueProperty(), -1, 1);
                slider("Saturation", adjust.saturationProperty(), -1, 1);
                slider("Brightness", adjust.brightnessProperty(), -1, 1);
                slider("Contrast", adjust.contrastProperty(), -1, 1);
                yield adjust;
            }
            case "SepiaTone" -> {
                SepiaTone sepia = new SepiaTone(0.8);
                slider("Level", sepia.levelProperty(), 0, 1);
                yield sepia;
            }
            case "MotionBlur" -> {
                MotionBlur blur = new MotionBlur(0, 20);
                slider("Angle", blur.angleProperty(), -180, 180);
                slider("Radius", blur.radiusProperty(), 0, 63);
                yield blur;
            }
            case "Lighting" -> {
                Light.Distant light = new Light.Distant(45, 45, Color.WHITE);
                Lighting lighting = new Lighting(light);
                lighting.setSurfaceScale(5);
                slider("Azimuth", light.azimuthProperty(), 0, 360);
                slider("Elevation", light.elevationProperty(), 0, 90);
                slider("Surface scale", lighting.surfaceScaleProperty(), 0, 10);
                slider("Diffuse constant", lighting.diffuseConstantProperty(), 0, 2);
                slider("Specular constant", lighting.specularConstantProperty(), 0, 2);
                slider("Specular exponent", lighting.specularExponentProperty(), 0, 40);
                yield lighting;
            }
            default -> null;
        };
        effectTarget.setEffect(effect);
        effectOutput.setText(effect == null
                ? "node.setEffect(null)"
                : "node.setEffect(new " + effect.getClass().getSimpleName() + "(…))");
    }

    private void slider(String name, DoubleProperty property, double min, double max) {
        Slider slider = new Slider(min, max, property.get());
        slider.valueProperty().bindBidirectional(property);
        Label label = new Label();
        label.getStyleClass().add("options-title");
        label.textProperty().bind(Bindings.format(Locale.ROOT, "%s: %.2f", name, slider.valueProperty()));
        effectParameters.getChildren().addAll(label, slider);
    }

    private void colorPicker(String name, ObjectProperty<Color> property) {
        ColorPicker picker = new ColorPicker(property.get());
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.valueProperty().bindBidirectional(property);
        Label label = new Label(name);
        label.getStyleClass().add("options-title");
        effectParameters.getChildren().addAll(label, picker);
    }

    // --- Example 2 -----------------------------------------------------------------------------------------

    private void updateChain() {
        Effect effect = null;
        List<String> names = new ArrayList<>();
        if (chainBlur.isSelected()) {
            effect = new GaussianBlur(4);
            names.add("GaussianBlur");
        }
        if (chainSepia.isSelected()) {
            SepiaTone sepia = new SepiaTone(0.8);
            sepia.setInput(effect);
            effect = sepia;
            names.add("SepiaTone");
        }
        if (chainReflection.isSelected()) {
            Reflection reflection = new Reflection(4, 0.6, 0.5, 0);
            reflection.setInput(effect);
            effect = reflection;
            names.add("Reflection");
        }
        if (chainShadow.isSelected()) {
            DropShadow shadow = new DropShadow(18, 6, 6, Color.color(0, 0, 0, 0.5));
            shadow.setInput(effect);
            effect = shadow;
            names.add("DropShadow");
        }
        chainTarget.setEffect(effect);
        chainOutput.setText(names.isEmpty() ? "No effect." : String.join(" → ", names));
    }
}
