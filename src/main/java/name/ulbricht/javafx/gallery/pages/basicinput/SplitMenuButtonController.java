package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Demo page for {@link SplitMenuButton}: a color button remembering the last choice and a save button with a
 * default command.
 */
public class SplitMenuButtonController {

    private static final Map<String, Color> COLORS = new LinkedHashMap<>();

    static {
        COLORS.put("Blue", Color.web("#3d8fd1"));
        COLORS.put("Orange", Color.web("#e5734b"));
        COLORS.put("Green", Color.web("#56b16a"));
        COLORS.put("Purple", Color.web("#b06ad1"));
    }

    @FXML
    private SplitMenuButton colorButton;
    @FXML
    private Rectangle colorTarget;
    @FXML
    private Label colorOutput;
    @FXML
    private CheckBox disableButton;
    @FXML
    private ComboBox<Side> popupSide;
    @FXML
    private SplitMenuButton saveButton;
    @FXML
    private Label saveOutput;

    private final Rectangle currentSwatch = new Rectangle(14, 14);
    private String currentColor = "Blue";

    @FXML
    private void initialize() {
        colorButton.disableProperty().bind(disableButton.selectedProperty());
        popupSide.getItems().setAll(Side.values());
        popupSide.setValue(Side.BOTTOM);
        colorButton.popupSideProperty().bind(popupSide.valueProperty());

        COLORS.forEach((name, color) -> {
            Rectangle swatch = new Rectangle(14, 14, color);
            MenuItem item = new MenuItem(name, swatch);
            item.setOnAction(e -> {
                currentColor = name;
                currentSwatch.setFill(color);
                applyColor();
            });
            colorButton.getItems().add(item);
        });
        currentSwatch.setFill(COLORS.get(currentColor));
        colorButton.setGraphic(currentSwatch);
        colorTarget.setFill(Color.TRANSPARENT);
        colorTarget.setStroke(Color.GRAY);
        colorTarget.getStrokeDashArray().setAll(4.0, 4.0);
        colorOutput.setText("Current color: " + currentColor);
    }

    @FXML
    private void onApplyColor() {
        applyColor();
    }

    private void applyColor() {
        colorTarget.setFill(COLORS.get(currentColor));
        colorOutput.setText("Applied: " + currentColor);
    }

    @FXML
    private void onSave() {
        saveOutput.setText("Main part clicked: Save");
    }

    @FXML
    private void onSaveItem(ActionEvent event) {
        saveOutput.setText("Menu item clicked: " + ((MenuItem) event.getSource()).getText());
    }
}
