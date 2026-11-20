package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Demo page for {@link ToggleButton}: a standalone toggle, a segmented {@link ToggleGroup} and independent
 * formatting toggles.
 */
public class ToggleButtonController {

    @FXML
    private ToggleButton simpleToggle;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableSimple;
    @FXML
    private ToggleButton alignLeft;
    @FXML
    private ToggleButton alignCenter;
    @FXML
    private ToggleButton alignRight;
    @FXML
    private Label alignedText;
    @FXML
    private Label groupOutput;
    @FXML
    private CheckBox requireSelection;
    @FXML
    private ToggleButton boldToggle;
    @FXML
    private ToggleButton italicToggle;
    @FXML
    private ToggleButton underlineToggle;
    @FXML
    private Label formattedText;

    @FXML
    private void initialize() {
        simpleToggle.disableProperty().bind(disableSimple.selectedProperty());
        simpleOutput.textProperty().bind(Bindings.when(simpleToggle.selectedProperty())
                .then("State: selected")
                .otherwise("State: not selected"));

        ToggleGroup group = new ToggleGroup();
        alignLeft.setToggleGroup(group);
        alignCenter.setToggleGroup(group);
        alignRight.setToggleGroup(group);
        alignLeft.setUserData(Pos.CENTER_LEFT);
        alignCenter.setUserData(Pos.CENTER);
        alignRight.setUserData(Pos.CENTER_RIGHT);
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                if (requireSelection.isSelected() && oldToggle != null) {
                    oldToggle.setSelected(true);
                } else {
                    alignedText.setAlignment(Pos.CENTER_LEFT);
                    groupOutput.setText("No button selected");
                }
                return;
            }
            alignedText.setAlignment((Pos) newToggle.getUserData());
            groupOutput.setText("Selected: " + ((ToggleButton) newToggle).getText());
        });
        group.selectToggle(alignLeft);

        InvalidationListener formatting = obs -> updateFormatting();
        boldToggle.selectedProperty().addListener(formatting);
        italicToggle.selectedProperty().addListener(formatting);
        underlineToggle.selectedProperty().addListener(formatting);
        updateFormatting();
    }

    private void updateFormatting() {
        formattedText.setFont(Font.font(Font.getDefault().getFamily(),
                boldToggle.isSelected() ? FontWeight.BOLD : FontWeight.NORMAL,
                italicToggle.isSelected() ? FontPosture.ITALIC : FontPosture.REGULAR,
                16));
        formattedText.setUnderline(underlineToggle.isSelected());
    }
}
