package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

import java.util.function.BiConsumer;

/**
 * Controller of the AnchorPane demo page: toggling anchors and a resizable anchored layout.
 */
public class AnchorPaneController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label anchoredBox;
    @FXML
    private CheckBox anchorTop;
    @FXML
    private CheckBox anchorLeft;
    @FXML
    private CheckBox anchorRight;
    @FXML
    private CheckBox anchorBottom;
    @FXML
    private Label offsetLabel;
    @FXML
    private Slider offset;

    @FXML
    private AnchorPane dialogPane;
    @FXML
    private Label paneWidthLabel;
    @FXML
    private Slider paneWidth;
    @FXML
    private Label paneHeightLabel;
    @FXML
    private Slider paneHeight;

    @FXML
    private void initialize() {
        offsetLabel.textProperty().bind(Bindings.format("Offset: %.0f", offset.valueProperty()));
        anchorTop.selectedProperty().addListener((obs, oldValue, value) -> updateAnchors());
        anchorLeft.selectedProperty().addListener((obs, oldValue, value) -> updateAnchors());
        anchorRight.selectedProperty().addListener((obs, oldValue, value) -> updateAnchors());
        anchorBottom.selectedProperty().addListener((obs, oldValue, value) -> updateAnchors());
        offset.valueProperty().addListener((obs, oldValue, value) -> updateAnchors());
        updateAnchors();

        dialogPane.prefWidthProperty().bind(paneWidth.valueProperty());
        dialogPane.prefHeightProperty().bind(paneHeight.valueProperty());
        paneWidthLabel.textProperty().bind(Bindings.format("Width: %.0f", paneWidth.valueProperty()));
        paneHeightLabel.textProperty().bind(Bindings.format("Height: %.0f", paneHeight.valueProperty()));
    }

    private void updateAnchors() {
        Double value = offset.getValue();
        apply(anchorTop, AnchorPane::setTopAnchor, value);
        apply(anchorLeft, AnchorPane::setLeftAnchor, value);
        apply(anchorRight, AnchorPane::setRightAnchor, value);
        apply(anchorBottom, AnchorPane::setBottomAnchor, value);
        // a child without anchors is placed at its layout position
        if (!anchorTop.isSelected() && !anchorBottom.isSelected()) {
            anchoredBox.setLayoutY(0);
        }
        if (!anchorLeft.isSelected() && !anchorRight.isSelected()) {
            anchoredBox.setLayoutX(0);
        }
        anchorPane.requestLayout();
    }

    private void apply(CheckBox toggle, BiConsumer<Node, Double> setter, Double value) {
        setter.accept(anchoredBox, toggle.isSelected() ? value : null);
    }
}
