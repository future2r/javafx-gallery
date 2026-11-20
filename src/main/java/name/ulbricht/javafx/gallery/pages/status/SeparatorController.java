package name.ulbricht.javafx.gallery.pages.status;

import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;

import java.util.List;

/**
 * Controller of the Separator demo page: horizontal and vertical separators and line alignment.
 */
public class SeparatorController {

    @FXML
    private Separator firstSeparator;
    @FXML
    private Separator secondSeparator;
    @FXML
    private CheckBox showSeparators;
    @FXML
    private ComboBox<VPos> valignment;

    @FXML
    private void initialize() {
        valignment.getItems().setAll(VPos.TOP, VPos.CENTER, VPos.BOTTOM);
        valignment.setValue(VPos.CENTER);
        for (Separator separator : List.of(firstSeparator, secondSeparator)) {
            separator.visibleProperty().bind(showSeparators.selectedProperty());
            separator.managedProperty().bind(showSeparators.selectedProperty());
            separator.valignmentProperty().bind(valignment.valueProperty());
        }
    }
}
