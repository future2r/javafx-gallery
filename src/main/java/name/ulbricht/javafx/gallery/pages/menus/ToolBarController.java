package name.ulbricht.javafx.gallery.pages.menus;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.SVGPath;

/**
 * Controller of the ToolBar demo page: a horizontal editor toolbar, overflow handling and vertical orientation.
 */
public class ToolBarController {

    private static final String ICON_NEW = "M4 1h6l3 3v11H4z M10 1v3h3";
    private static final String ICON_OPEN = "M1 4h5l1 2h8v8H1z";
    private static final String ICON_SAVE = "M2 2h10l2 2v10H2z M5 2v4h6V2 M5 14v-5h6v5";
    private static final String ICON_CURSOR = "M3 1l10 7-5 1 3 5-2 1-3-5-3 3z";
    private static final String ICON_PEN = "M2 14l1-4 8-8 3 3-8 8z M10 3l3 3";
    private static final String ICON_SHAPE = "M2 2h7v7H2z M11 9a3 3 0 1 0 0.01 0z";
    private static final String ICON_ERASER = "M2 11l7-7 5 5-5 5H5z M6 14h8";

    @FXML
    private Button newButton;
    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private ToggleButton boldToggle;
    @FXML
    private ToggleButton italicToggle;
    @FXML
    private ToggleButton underlineToggle;
    @FXML
    private ComboBox<Integer> fontSize;
    @FXML
    private Label preview;
    @FXML
    private Label editorOutput;

    @FXML
    private ToolBar overflowToolBar;
    @FXML
    private Label overflowOutput;
    @FXML
    private Slider widthSlider;

    @FXML
    private ToolBar orientedToolBar;
    @FXML
    private Button cursorButton;
    @FXML
    private Button penButton;
    @FXML
    private Button shapeButton;
    @FXML
    private Button eraseButton;
    @FXML
    private Label drawOutput;
    @FXML
    private ComboBox<Orientation> orientationChoice;

    @FXML
    private void initialize() {
        // Example 1
        decorate(newButton, ICON_NEW, "New document");
        decorate(openButton, ICON_OPEN, "Open document");
        decorate(saveButton, ICON_SAVE, "Save document");
        fontSize.getItems().setAll(10, 12, 14, 16, 20, 24);
        fontSize.setValue(14);
        preview.styleProperty().bind(Bindings.createStringBinding(
                () -> "-fx-font-size: %dpx; -fx-font-weight: %s; -fx-font-style: %s; -fx-underline: %s;".formatted(
                        fontSize.getValue() == null ? 14 : fontSize.getValue(),
                        boldToggle.isSelected() ? "bold" : "normal",
                        italicToggle.isSelected() ? "italic" : "normal",
                        underlineToggle.isSelected()),
                fontSize.valueProperty(), boldToggle.selectedProperty(), italicToggle.selectedProperty(),
                underlineToggle.selectedProperty()));

        // Example 2
        String[][] commands = {
                {"New", ICON_NEW}, {"Open", ICON_OPEN}, {"Save", ICON_SAVE}, {"Select", ICON_CURSOR},
                {"Draw", ICON_PEN}, {"Shapes", ICON_SHAPE}, {"Erase", ICON_ERASER}
        };
        for (int i = 0; i < commands.length; i++) {
            String[] command = commands[i];
            if (i == 3) {
                overflowToolBar.getItems().add(new Separator());
            }
            Button button = new Button(command[0], icon(command[1]));
            button.setOnAction(e -> overflowOutput.setText("Clicked: " + command[0]));
            overflowToolBar.getItems().add(button);
        }
        overflowToolBar.prefWidthProperty().bind(widthSlider.valueProperty());

        // Example 3
        decorate(cursorButton, ICON_CURSOR, "Select");
        decorate(penButton, ICON_PEN, "Pen");
        decorate(shapeButton, ICON_SHAPE, "Shape");
        decorate(eraseButton, ICON_ERASER, "Eraser");
        orientationChoice.getItems().setAll(Orientation.values());
        orientationChoice.setValue(Orientation.HORIZONTAL);
        orientationChoice.valueProperty().addListener((obs, oldValue, newValue) -> {
            Orientation orientation = newValue == null ? Orientation.HORIZONTAL : newValue;
            orientedToolBar.setOrientation(orientation);
            orientedToolBar.getItems().stream()
                    .filter(Separator.class::isInstance)
                    .map(Separator.class::cast)
                    .forEach(separator -> separator.setOrientation(orientation == Orientation.HORIZONTAL
                            ? Orientation.VERTICAL : Orientation.HORIZONTAL));
        });
    }

    @FXML
    private void onToolButton(ActionEvent event) {
        editorOutput.setText("Clicked: " + ((Node) event.getSource()).getUserData());
    }

    @FXML
    private void onDrawTool(ActionEvent event) {
        drawOutput.setText("Tool: " + ((Node) event.getSource()).getUserData());
    }

    private static void decorate(Button button, String iconPath, String tooltip) {
        button.setGraphic(icon(iconPath));
        button.setTooltip(new Tooltip(tooltip));
    }

    private static Node icon(String content) {
        SVGPath path = new SVGPath();
        path.setContent(content);
        path.setStyle("-fx-fill: transparent; -fx-stroke: -fx-text-base-color; -fx-stroke-width: 1.2;");
        return path;
    }
}
