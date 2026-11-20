package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller of the GridPane demo page: spans, gaps, constraints, a form layout and runtime children.
 */
public class GridPaneController {

    private static final String[] ALT_CLASSES = {"", "alt-1", "alt-2", "alt-3"};

    @FXML
    private GridPane spanGrid;
    @FXML
    private Label hgapLabel;
    @FXML
    private Slider hgap;
    @FXML
    private Label vgapLabel;
    @FXML
    private Slider vgap;
    @FXML
    private Label paddingLabel;
    @FXML
    private Slider padding;
    @FXML
    private CheckBox gridLines;

    @FXML
    private ColumnConstraints labelColumn;
    @FXML
    private ComboBox<HPos> labelAlignment;

    @FXML
    private GridPane dynamicGrid;
    @FXML
    private Label dynamicOutput;
    @FXML
    private Label columnsLabel;
    @FXML
    private Slider columns;

    private final List<Node> cells = new ArrayList<>();

    @FXML
    private void initialize() {
        spanGrid.hgapProperty().bind(hgap.valueProperty());
        hgapLabel.textProperty().bind(Bindings.format("Horizontal gap: %.0f", hgap.valueProperty()));
        spanGrid.vgapProperty().bind(vgap.valueProperty());
        vgapLabel.textProperty().bind(Bindings.format("Vertical gap: %.0f", vgap.valueProperty()));
        paddingLabel.textProperty().bind(Bindings.format("Padding: %.0f", padding.valueProperty()));
        padding.valueProperty().addListener((obs, oldValue, value) -> spanGrid.setPadding(new Insets(value.doubleValue())));
        spanGrid.gridLinesVisibleProperty().bind(gridLines.selectedProperty());

        labelAlignment.getItems().setAll(HPos.values());
        labelAlignment.setValue(labelColumn.getHalignment());
        labelColumn.halignmentProperty().bind(labelAlignment.valueProperty());

        columnsLabel.textProperty().bind(Bindings.format("Columns: %.0f", columns.valueProperty()));
        columns.valueProperty().addListener((obs, oldValue, value) -> relayoutCells());
        for (int i = 0; i < 6; i++) {
            onAddCell();
        }
    }

    @FXML
    private void onAddCell() {
        if (cells.size() >= 24) {
            return;
        }
        int index = cells.size();
        Label label = new Label("Item " + (index + 1));
        label.getStyleClass().add("demo-box");
        String alt = ALT_CLASSES[index % ALT_CLASSES.length];
        if (!alt.isEmpty()) {
            label.getStyleClass().add(alt);
        }
        label.setMaxWidth(Double.MAX_VALUE);
        cells.add(label);
        relayoutCells();
    }

    @FXML
    private void onRemoveCell() {
        if (!cells.isEmpty()) {
            cells.removeLast();
            relayoutCells();
        }
    }

    private void relayoutCells() {
        int columnCount = Math.max(1, (int) Math.round(columns.getValue()));
        dynamicGrid.getChildren().clear();
        for (int i = 0; i < cells.size(); i++) {
            dynamicGrid.add(cells.get(i), i % columnCount, i / columnCount);
        }
        int rows = (cells.size() + columnCount - 1) / columnCount;
        dynamicOutput.setText(cells.size() + " children in " + columnCount + " column(s) and " + rows + " row(s).");
    }
}
