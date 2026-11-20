package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * Demo page for {@link ComboBox}: basic, editable, a custom cell factory and a font picker.
 */
public class ComboBoxController {

    /** A color with a display name, used as item type of the color ComboBox. */
    private record NamedColor(String name, Color color) {
    }

    @FXML
    private ComboBox<String> simpleCombo;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableCombo;
    @FXML
    private ComboBox<String> sizeCombo;
    @FXML
    private Label sizeSample;
    @FXML
    private Label sizeOutput;
    @FXML
    private CheckBox editableSize;
    @FXML
    private ComboBox<NamedColor> colorCombo;
    @FXML
    private Rectangle colorPreview;
    @FXML
    private ComboBox<Integer> visibleRows;
    @FXML
    private ComboBox<String> fontCombo;
    @FXML
    private Label fontSample;

    @FXML
    private void initialize() {
        simpleCombo.getItems().setAll("Apple", "Banana", "Cherry", "Grape", "Kiwi", "Mango", "Orange", "Pear");
        simpleCombo.disableProperty().bind(disableCombo.selectedProperty());
        simpleCombo.valueProperty().addListener((obs, oldValue, value) ->
                simpleOutput.setText(value == null ? "Nothing selected." : "You selected: " + value));

        sizeCombo.getItems().setAll("8", "9", "10", "11", "12", "14", "16", "18", "20", "24", "28", "36", "48", "72");
        sizeCombo.editableProperty().bind(editableSize.selectedProperty());
        sizeCombo.valueProperty().addListener((obs, oldValue, value) -> applyFontSize(value));
        sizeCombo.setValue("14");

        colorCombo.getItems().setAll(
                new NamedColor("Blue", Color.web("#3d8fd1")),
                new NamedColor("Orange", Color.web("#e5734b")),
                new NamedColor("Green", Color.web("#56b16a")),
                new NamedColor("Purple", Color.web("#b06ad1")),
                new NamedColor("Gold", Color.web("#e0b000")),
                new NamedColor("Red", Color.web("#d9534f")),
                new NamedColor("Teal", Color.web("#00a2a2")),
                new NamedColor("Gray", Color.web("#8a8a8a")));
        colorCombo.setCellFactory(listView -> new ColorCell());
        colorCombo.setButtonCell(new ColorCell());
        colorCombo.valueProperty().addListener((obs, oldValue, value) ->
                colorPreview.setFill(value == null ? Color.TRANSPARENT : value.color()));
        colorCombo.getSelectionModel().selectFirst();

        visibleRows.getItems().setAll(3, 5, 10);
        visibleRows.setValue(colorCombo.getVisibleRowCount());
        visibleRows.valueProperty().addListener((obs, oldValue, value) -> {
            if (value != null) {
                colorCombo.setVisibleRowCount(value);
            }
        });

        fontCombo.getItems().setAll(Font.getFamilies());
        fontCombo.setCellFactory(listView -> new FontCell());
        fontCombo.setButtonCell(new FontCell());
        fontCombo.valueProperty().addListener((obs, oldValue, family) -> {
            if (family != null) {
                fontSample.setFont(Font.font(family, 18));
            }
        });
        fontCombo.setValue(Font.getDefault().getFamily());
    }

    private void applyFontSize(String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        try {
            double size = Double.parseDouble(value.trim());
            if (size < 4 || size > 120) {
                sizeOutput.setText("Please enter a size between 4 and 120.");
                return;
            }
            sizeSample.setFont(Font.font(Font.getDefault().getFamily(), size));
            sizeOutput.setText("Font size: " + value.trim());
        } catch (NumberFormatException e) {
            sizeOutput.setText("\"" + value + "\" is not a number.");
        }
    }

    /** List cell showing a color swatch and the color name. */
    private static final class ColorCell extends ListCell<NamedColor> {

        private final Rectangle swatch = new Rectangle(14, 14);

        ColorCell() {
            swatch.setArcWidth(4);
            swatch.setArcHeight(4);
        }

        @Override
        protected void updateItem(NamedColor item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                swatch.setFill(item.color());
                setText(item.name());
                setGraphic(swatch);
            }
        }
    }

    /** List cell rendering a font family name in its own font. */
    private static final class FontCell extends ListCell<String> {

        @Override
        protected void updateItem(String family, boolean empty) {
            super.updateItem(family, empty);
            if (empty || family == null) {
                setText(null);
                setFont(Font.getDefault());
            } else {
                setText(family);
                setFont(Font.font(family, 13));
            }
        }
    }
}
