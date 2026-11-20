package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Controller of the SplitPane demo page: orientation, divider positions and nested split panes.
 */
public class SplitPaneController {

    private static final String[] ALT_CLASSES = {"", "alt-1", "alt-2", "alt-3"};

    @FXML
    private SplitPane split;
    @FXML
    private Label firstSide;
    @FXML
    private ComboBox<Orientation> orientation;
    @FXML
    private Label positionLabel;
    @FXML
    private Slider position;
    @FXML
    private CheckBox resizableWithParent;

    @FXML
    private SplitPane outerSplit;
    @FXML
    private SplitPane innerSplit;
    @FXML
    private Label nestedOutput;

    private final InvalidationListener dividerListener = obs -> updateNestedOutput();
    private int addedPanes;

    @FXML
    private void initialize() {
        orientation.getItems().setAll(Orientation.values());
        orientation.setValue(split.getOrientation());
        split.orientationProperty().bind(orientation.valueProperty());
        position.valueProperty().bindBidirectional(split.getDividers().getFirst().positionProperty());
        positionLabel.textProperty().bind(Bindings.format("Divider position: %.2f", position.valueProperty()));
        resizableWithParent.selectedProperty().addListener((obs, oldValue, resizable) ->
                SplitPane.setResizableWithParent(firstSide, resizable));

        onResetDividers();
        outerSplit.getDividers().forEach(divider -> divider.positionProperty().addListener(dividerListener));
        innerSplit.getDividers().forEach(divider -> divider.positionProperty().addListener(dividerListener));
        outerSplit.getDividers().addListener((ListChangeListener<SplitPane.Divider>) change -> {
            while (change.next()) {
                change.getRemoved().forEach(divider -> divider.positionProperty().removeListener(dividerListener));
                change.getAddedSubList().forEach(divider -> divider.positionProperty().addListener(dividerListener));
            }
            updateNestedOutput();
        });
        updateNestedOutput();
    }

    private void updateNestedOutput() {
        nestedOutput.setText("Outer dividers: " + format(outerSplit) + "   Inner divider: " + format(innerSplit));
    }

    private static String format(SplitPane pane) {
        return pane.getDividers().stream()
                .map(divider -> String.format(Locale.ROOT, "%.2f", divider.getPosition()))
                .collect(Collectors.joining(", "));
    }

    @FXML
    private void onResetDividers() {
        int count = outerSplit.getDividers().size();
        double[] positions = new double[count];
        if (count == 2) {
            positions[0] = 0.25;
            positions[1] = 0.75;
        } else {
            for (int i = 0; i < count; i++) {
                positions[i] = (i + 1.0) / (count + 1.0);
            }
        }
        outerSplit.setDividerPositions(positions);
        innerSplit.setDividerPositions(0.7);
    }

    @FXML
    private void onAddPane() {
        if (outerSplit.getItems().size() >= 6) {
            return;
        }
        addedPanes++;
        Label label = new Label("Pane " + addedPanes);
        label.getStyleClass().add("demo-box");
        String alt = ALT_CLASSES[addedPanes % ALT_CLASSES.length];
        if (!alt.isEmpty()) {
            label.getStyleClass().add(alt);
        }
        label.setMinWidth(40);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        outerSplit.getItems().add(label);
    }

    @FXML
    private void onRemovePane() {
        if (outerSplit.getItems().size() > 2) {
            outerSplit.getItems().removeLast();
        }
    }
}
