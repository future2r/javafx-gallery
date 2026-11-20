package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/**
 * Demo page for {@link RadioButton}: a single group and two independent groups.
 */
public class RadioButtonController {

    @FXML
    private ToggleGroup optionGroup;
    @FXML
    private FlowPane optionPane;
    @FXML
    private RadioButton option1;
    @FXML
    private RadioButton option2;
    @FXML
    private RadioButton option3;
    @FXML
    private Label optionOutput;
    @FXML
    private CheckBox disableOptions;
    @FXML
    private ComboBox<Orientation> orientation;
    @FXML
    private ToggleGroup backgroundGroup;
    @FXML
    private ToggleGroup borderGroup;
    @FXML
    private Region colorBox;

    @FXML
    private void initialize() {
        option1.disableProperty().bind(disableOptions.selectedProperty());
        option2.disableProperty().bind(disableOptions.selectedProperty());
        option3.disableProperty().bind(disableOptions.selectedProperty());
        optionGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                optionOutput.setText("You selected: " + ((RadioButton) newToggle).getText());
            }
        });

        orientation.getItems().setAll(Orientation.values());
        orientation.setValue(Orientation.HORIZONTAL);
        optionPane.orientationProperty().bind(orientation.valueProperty());

        backgroundGroup.selectedToggleProperty().addListener(obs -> updateColorBox());
        borderGroup.selectedToggleProperty().addListener(obs -> updateColorBox());
        updateColorBox();
    }

    private void updateColorBox() {
        colorBox.setStyle("-fx-background-color: " + colorOf(backgroundGroup.getSelectedToggle(), "#3d8fd1") + ";"
                + "-fx-background-radius: 4;"
                + "-fx-border-color: " + colorOf(borderGroup.getSelectedToggle(), "transparent") + ";"
                + "-fx-border-width: 6; -fx-border-radius: 4;");
    }

    private static String colorOf(Toggle toggle, String fallback) {
        return toggle != null && toggle.getUserData() instanceof String color ? color : fallback;
    }
}
