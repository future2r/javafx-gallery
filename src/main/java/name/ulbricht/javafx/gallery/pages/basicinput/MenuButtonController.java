package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * Demo page for {@link MenuButton}: simple commands, the different menu item types and a menu built at runtime.
 */
public class MenuButtonController {

    @FXML
    private MenuButton simpleMenuButton;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableMenuButton;
    @FXML
    private ComboBox<Side> popupSide;
    @FXML
    private CheckMenuItem showStatusBar;
    @FXML
    private CheckMenuItem showSidebar;
    @FXML
    private RadioMenuItem sortByName;
    @FXML
    private RadioMenuItem sortByDate;
    @FXML
    private RadioMenuItem sortBySize;
    @FXML
    private Label viewOutput;
    @FXML
    private MenuButton dynamicMenuButton;
    @FXML
    private Label dynamicOutput;

    private final ToggleGroup sortGroup = new ToggleGroup();
    private String zoom = "100 %";
    private int fileCounter;

    @FXML
    private void initialize() {
        simpleMenuButton.disableProperty().bind(disableMenuButton.selectedProperty());
        popupSide.getItems().setAll(Side.values());
        popupSide.setValue(Side.BOTTOM);
        simpleMenuButton.popupSideProperty().bind(popupSide.valueProperty());

        sortByName.setToggleGroup(sortGroup);
        sortByDate.setToggleGroup(sortGroup);
        sortBySize.setToggleGroup(sortGroup);
        InvalidationListener update = obs -> updateViewOutput();
        showStatusBar.selectedProperty().addListener(update);
        showSidebar.selectedProperty().addListener(update);
        sortGroup.selectedToggleProperty().addListener(update);
        updateViewOutput();

        onAddItem();
        onAddItem();
    }

    @FXML
    private void onSimpleItem(ActionEvent event) {
        simpleOutput.setText("You clicked: " + ((MenuItem) event.getSource()).getText());
    }

    @FXML
    private void onZoom(ActionEvent event) {
        zoom = ((MenuItem) event.getSource()).getText();
        updateViewOutput();
    }

    @FXML
    private void onAddItem() {
        fileCounter++;
        MenuItem item = new MenuItem("Document " + fileCounter + ".txt");
        item.setOnAction(e -> dynamicOutput.setText("Open " + item.getText()));
        dynamicMenuButton.getItems().add(item);
        dynamicOutput.setText(dynamicMenuButton.getItems().size() + " item(s) in the menu");
    }

    @FXML
    private void onClearItems() {
        dynamicMenuButton.getItems().clear();
        dynamicOutput.setText("The menu is empty.");
    }

    private void updateViewOutput() {
        String sort = sortGroup.getSelectedToggle() instanceof RadioMenuItem item ? item.getText() : "unsorted";
        viewOutput.setText("Status bar: " + (showStatusBar.isSelected() ? "on" : "off")
                + ", sidebar: " + (showSidebar.isSelected() ? "on" : "off")
                + ", " + sort.toLowerCase() + ", zoom " + zoom);
    }
}
