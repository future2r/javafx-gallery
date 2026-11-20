package name.ulbricht.javafx.gallery.pages.dialogs;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller of the Tooltip demo page: a simple tooltip, timing properties and rich tooltip content.
 */
public class TooltipController {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @FXML
    private Button timingButton;
    @FXML
    private Label timingOutput;
    @FXML
    private Label showDelayLabel;
    @FXML
    private Slider showDelay;
    @FXML
    private Label showDurationLabel;
    @FXML
    private Slider showDuration;
    @FXML
    private Label hideDelayLabel;
    @FXML
    private Slider hideDelay;

    @FXML
    private Rectangle shape;
    @FXML
    private CheckBox wrapText;
    @FXML
    private CheckBox showGraphic;
    @FXML
    private ComboBox<ContentDisplay> contentDisplay;

    @FXML
    private void initialize() {
        // Example 2
        Tooltip timingTooltip = new Tooltip("I respect the timing options.");
        timingButton.setTooltip(timingTooltip);
        timingTooltip.showDelayProperty().bind(Bindings.createObjectBinding(
                () -> Duration.millis(showDelay.getValue()), showDelay.valueProperty()));
        timingTooltip.showDurationProperty().bind(Bindings.createObjectBinding(
                () -> Duration.millis(showDuration.getValue()), showDuration.valueProperty()));
        timingTooltip.hideDelayProperty().bind(Bindings.createObjectBinding(
                () -> Duration.millis(hideDelay.getValue()), hideDelay.valueProperty()));
        showDelayLabel.textProperty().bind(showDelay.valueProperty().asString("Show delay: %.0f ms"));
        showDurationLabel.textProperty().bind(showDuration.valueProperty().asString("Show duration: %.0f ms"));
        hideDelayLabel.textProperty().bind(hideDelay.valueProperty().asString("Hide delay: %.0f ms"));
        timingTooltip.setOnShown(e -> timingOutput.setText("Shown at " + LocalTime.now().format(TIME)));
        timingTooltip.setOnHidden(e -> timingOutput.setText(timingOutput.getText()
                + " · hidden at " + LocalTime.now().format(TIME)));

        // Example 3
        Tooltip richTooltip = new Tooltip("Rectangles are one of the basic shapes of JavaFX. This tooltip is "
                + "installed with Tooltip.install(node, tooltip) because shapes have no tooltip property.");
        richTooltip.setMaxWidth(280);
        richTooltip.setShowDelay(Duration.millis(300));
        richTooltip.wrapTextProperty().bind(wrapText.selectedProperty());
        Circle dot = new Circle(14, Color.web("#3d8fd1"));
        dot.setStroke(Color.WHITE);
        richTooltip.graphicProperty().bind(Bindings.when(showGraphic.selectedProperty()).then(dot).otherwise(
                (Circle) null));
        contentDisplay.getItems().setAll(ContentDisplay.values());
        contentDisplay.setValue(ContentDisplay.LEFT);
        richTooltip.contentDisplayProperty().bind(contentDisplay.valueProperty());
        Tooltip.install(shape, richTooltip);
    }
}
