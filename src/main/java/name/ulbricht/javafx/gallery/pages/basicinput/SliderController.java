package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.shape.Circle;

/**
 * Demo page for {@link Slider}: basic usage, tick marks with snapping and a vertical slider.
 */
public class SliderController {

    @FXML
    private Slider simpleSlider;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableSlider;
    @FXML
    private Slider tickSlider;
    @FXML
    private Label tickOutput;
    @FXML
    private CheckBox showTickMarks;
    @FXML
    private CheckBox showTickLabels;
    @FXML
    private CheckBox snapToTicks;
    @FXML
    private Slider verticalSlider;
    @FXML
    private Circle circle;
    @FXML
    private Label verticalOutput;
    @FXML
    private CheckBox verticalTicks;

    @FXML
    private void initialize() {
        simpleSlider.disableProperty().bind(disableSlider.selectedProperty());
        simpleOutput.textProperty().bind(simpleSlider.valueProperty().asString("Value: %.1f"));

        tickSlider.showTickMarksProperty().bind(showTickMarks.selectedProperty());
        tickSlider.showTickLabelsProperty().bind(showTickLabels.selectedProperty());
        tickSlider.snapToTicksProperty().bind(snapToTicks.selectedProperty());
        tickOutput.textProperty().bind(tickSlider.valueProperty().asString("Value: %.1f"));

        verticalSlider.showTickMarksProperty().bind(verticalTicks.selectedProperty());
        circle.radiusProperty().bind(verticalSlider.valueProperty());
        verticalOutput.textProperty().bind(verticalSlider.valueProperty().asString("Radius: %.0f px"));
    }
}
