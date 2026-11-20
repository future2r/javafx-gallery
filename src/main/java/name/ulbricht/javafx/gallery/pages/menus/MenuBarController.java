package name.ulbricht.javafx.gallery.pages.menus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.SVGPath;

import java.util.List;

/**
 * Controller of the MenuBar demo page: simple menus with mnemonics, accelerators, check/radio items, sub menus and
 * custom menu items.
 */
public class MenuBarController {

    @FXML
    private MenuBar simpleMenuBar;
    @FXML
    private MenuItem newItem;
    @FXML
    private Menu editMenu;
    @FXML
    private MenuItem undoItem;
    @FXML
    private MenuItem cutItem;
    @FXML
    private MenuItem copyItem;
    @FXML
    private MenuItem pasteItem;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableEdit;
    @FXML
    private CheckBox disablePaste;
    @FXML
    private CheckBox useSystemMenuBar;

    @FXML
    private MenuItem boldItem;
    @FXML
    private MenuItem italicItem;
    @FXML
    private MenuItem underlineItem;
    @FXML
    private CheckMenuItem statusBarItem;
    @FXML
    private CheckMenuItem wordWrapItem;
    @FXML
    private RadioMenuItem smallItem;
    @FXML
    private RadioMenuItem mediumItem;
    @FXML
    private RadioMenuItem largeItem;
    @FXML
    private Label formatOutput;
    @FXML
    private Label viewOutput;

    @FXML
    private MenuItem circleItem;
    @FXML
    private MenuItem rectangleItem;
    @FXML
    private MenuItem starItem;
    @FXML
    private Menu recentMenu;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Label zoomValue;
    @FXML
    private Label insertOutput;

    private final ToggleGroup iconSizeGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        // Example 1
        editMenu.disableProperty().bind(disableEdit.selectedProperty());
        pasteItem.disableProperty().bind(disablePaste.selectedProperty());
        // MenuBar forbids a unidirectional binding of this property, so a listener is used instead.
        useSystemMenuBar.selectedProperty().addListener(
                (obs, oldValue, newValue) -> simpleMenuBar.setUseSystemMenuBar(newValue));
        newItem.setGraphic(icon("M4 1h6l3 3v11H4z M10 1v3h3"));
        undoItem.setGraphic(icon("M5 3L1 7l4 4V8h5a3 3 0 0 1 0 6H8v1h2a4 4 0 0 0 0-8H5z"));
        cutItem.setGraphic(icon("M4 2l8 10M12 2L4 12M4 12a2 2 0 1 0 0.1 0M12 12a2 2 0 1 0 0.1 0"));
        copyItem.setGraphic(icon("M5 1h8v10H5z M3 4v11h8v-1H4V4z"));

        // Example 2
        List.of(smallItem, mediumItem, largeItem).forEach(item -> item.setToggleGroup(iconSizeGroup));
        statusBarItem.selectedProperty().addListener((obs, oldValue, newValue) -> updateViewOutput());
        wordWrapItem.selectedProperty().addListener((obs, oldValue, newValue) -> updateViewOutput());
        iconSizeGroup.selectedToggleProperty().addListener((obs, oldValue, newValue) -> updateViewOutput());
        updateViewOutput();

        // Example 3
        circleItem.setGraphic(icon("M8 2a6 6 0 1 0 0.01 0z"));
        rectangleItem.setGraphic(icon("M2 4h12v8H2z"));
        starItem.setGraphic(icon("M8 1l2 5h5l-4 3 2 6-5-4-5 4 2-6-4-3h5z"));
        for (String file : List.of("Report.docx", "Budget 2026.xlsx", "Notes.txt")) {
            MenuItem item = new MenuItem(file);
            item.setMnemonicParsing(false);
            item.setOnAction(e -> insertOutput.setText("Opened recent file: " + file));
            recentMenu.getItems().add(item);
        }
        zoomValue.textProperty().bind(zoomSlider.valueProperty().asString("%.0f %%"));
        zoomSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> insertOutput.setText("Zoom: %.0f %%".formatted(newValue.doubleValue())));
    }

    @FXML
    private void onMenuItem(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        simpleOutput.setText("You clicked: " + plainText(item));
    }

    @FXML
    private void onFormatItem(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        String accelerator = item.getAccelerator() == null ? "" : " (" + item.getAccelerator().getDisplayText() + ")";
        formatOutput.setText("Invoked: " + plainText(item) + accelerator);
    }

    @FXML
    private void onInsertItem(ActionEvent event) {
        insertOutput.setText("Inserted: " + plainText((MenuItem) event.getSource()));
    }

    private void updateViewOutput() {
        Toggle selected = iconSizeGroup.getSelectedToggle();
        String size = selected instanceof MenuItem item ? plainText(item) : "none";
        viewOutput.setText("Status bar: %s · Word wrap: %s · %s".formatted(
                statusBarItem.isSelected() ? "on" : "off", wordWrapItem.isSelected() ? "on" : "off", size));
    }

    private static String plainText(MenuItem item) {
        return item.getText().replace("_", "");
    }

    private static Node icon(String content) {
        SVGPath path = new SVGPath();
        path.setContent(content);
        path.setStyle("-fx-fill: transparent; -fx-stroke: -fx-text-base-color; -fx-stroke-width: 1.2;");
        return path;
    }
}
