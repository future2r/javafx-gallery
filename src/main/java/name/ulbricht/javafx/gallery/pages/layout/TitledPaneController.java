package name.ulbricht.javafx.gallery.pages.layout;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * Controller of the TitledPane demo page: expanding, collapsible, animated and header graphics.
 */
public class TitledPaneController {

    @FXML
    private TitledPane basicPane;
    @FXML
    private Label basicOutput;
    @FXML
    private CheckBox expanded;
    @FXML
    private CheckBox collapsible;
    @FXML
    private CheckBox animated;

    @FXML
    private ComboBox<String> country;

    @FXML
    private TitledPane graphicPane;
    @FXML
    private CheckBox powerSaving;
    @FXML
    private VBox powerSavingContent;
    @FXML
    private Label graphicOutput;

    @FXML
    private void initialize() {
        basicPane.expandedProperty().bindBidirectional(expanded.selectedProperty());
        basicPane.collapsibleProperty().bind(collapsible.selectedProperty());
        basicPane.animatedProperty().bind(animated.selectedProperty());
        expanded.disableProperty().bind(collapsible.selectedProperty().not());
        basicPane.expandedProperty().addListener((obs, oldValue, isExpanded) ->
                basicOutput.setText(isExpanded ? "The pane was expanded." : "The pane was collapsed."));
        basicOutput.setText("The pane is expanded.");

        country.getItems().setAll("Austria", "Germany", "Switzerland", "United Kingdom", "United States");
        country.getSelectionModel().select("Germany");

        powerSavingContent.disableProperty().bind(powerSaving.selectedProperty().not());
        powerSaving.selectedProperty().addListener((obs, oldValue, on) ->
                graphicOutput.setText("Power saving is " + (on ? "on." : "off.")));
        graphicOutput.setText("Power saving is on.");
    }
}
