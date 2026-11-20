package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.util.List;

/**
 * Demo page for {@link CheckBox}: two-state, three-state and a "select all" parent check box.
 */
public class CheckBoxController {

    @FXML
    private CheckBox twoState;
    @FXML
    private Label twoStateOutput;
    @FXML
    private CheckBox disableTwoState;
    @FXML
    private CheckBox threeState;
    @FXML
    private Label threeStateOutput;
    @FXML
    private CheckBox allowIndeterminate;
    @FXML
    private CheckBox selectAll;
    @FXML
    private CheckBox option1;
    @FXML
    private CheckBox option2;
    @FXML
    private CheckBox option3;
    @FXML
    private Label selectAllOutput;

    private List<CheckBox> options;

    @FXML
    private void initialize() {
        twoState.disableProperty().bind(disableTwoState.selectedProperty());
        twoState.selectedProperty().addListener((obs, oldValue, selected) ->
                twoStateOutput.setText(selected ? "You checked the box." : "You unchecked the box."));
        twoStateOutput.setText("Click the CheckBox.");

        threeState.allowIndeterminateProperty().bind(allowIndeterminate.selectedProperty());
        InvalidationListener threeStateListener = obs -> threeStateOutput.setText("State: " + stateOf(threeState));
        threeState.selectedProperty().addListener(threeStateListener);
        threeState.indeterminateProperty().addListener(threeStateListener);
        threeStateListener.invalidated(null);

        options = List.of(option1, option2, option3);
        // selectAll does not allow the indeterminate state itself: a click cycles only checked/unchecked,
        // the indeterminate state is set programmatically.
        selectAll.setOnAction(e -> {
            boolean selected = selectAll.isSelected();
            options.forEach(option -> option.setSelected(selected));
        });
        options.forEach(option -> option.selectedProperty().addListener(obs -> updateSelectAll()));
        updateSelectAll();
    }

    private void updateSelectAll() {
        long count = options.stream().filter(CheckBox::isSelected).count();
        selectAll.setIndeterminate(count > 0 && count < options.size());
        selectAll.setSelected(count == options.size());
        selectAllOutput.setText(count + " of " + options.size() + " options selected");
    }

    private static String stateOf(CheckBox checkBox) {
        if (checkBox.isIndeterminate()) {
            return "indeterminate";
        }
        return checkBox.isSelected() ? "checked" : "unchecked";
    }
}
