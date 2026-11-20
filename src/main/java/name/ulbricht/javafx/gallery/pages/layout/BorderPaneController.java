package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * Controller of the BorderPane demo page: toggling the five regions and a typical application layout.
 */
public class BorderPaneController {

    @FXML
    private BorderPane regionsPane;
    @FXML
    private Label topBox;
    @FXML
    private Label leftBox;
    @FXML
    private Label centerBox;
    @FXML
    private Label rightBox;
    @FXML
    private Label bottomBox;
    @FXML
    private CheckBox showTop;
    @FXML
    private CheckBox showLeft;
    @FXML
    private CheckBox showCenter;
    @FXML
    private CheckBox showRight;
    @FXML
    private CheckBox showBottom;
    @FXML
    private Label marginLabel;
    @FXML
    private Slider margin;

    @FXML
    private ListView<String> navigationList;
    @FXML
    private TextArea editor;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        bindRegion(showTop, regionsPane.topProperty(), topBox);
        bindRegion(showLeft, regionsPane.leftProperty(), leftBox);
        bindRegion(showCenter, regionsPane.centerProperty(), centerBox);
        bindRegion(showRight, regionsPane.rightProperty(), rightBox);
        bindRegion(showBottom, regionsPane.bottomProperty(), bottomBox);

        marginLabel.textProperty().bind(Bindings.format("Margin of all regions: %.0f", margin.valueProperty()));
        margin.valueProperty().addListener((obs, oldValue, value) -> {
            Insets insets = new Insets(value.doubleValue());
            for (Node node : List.of(topBox, leftBox, centerBox, rightBox, bottomBox)) {
                BorderPane.setMargin(node, insets);
            }
        });

        navigationList.getItems().setAll("Inbox", "Drafts", "Sent", "Archive", "Trash");
        navigationList.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> {
            if (item != null) {
                editor.setText("Content of \"" + item + "\" would be shown here.");
            }
        });
        navigationList.getSelectionModel().selectFirst();
        statusLabel.textProperty().bind(Bindings.createStringBinding(
                () -> "Characters: " + editor.getLength() + "   Folder: "
                        + navigationList.getSelectionModel().getSelectedItem(),
                editor.lengthProperty(), navigationList.getSelectionModel().selectedItemProperty()));
    }

    private static void bindRegion(CheckBox toggle, ObjectProperty<Node> region, Node node) {
        toggle.selectedProperty().addListener((obs, oldValue, show) -> region.set(show ? node : null));
    }
}
