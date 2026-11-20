package name.ulbricht.javafx.gallery.pages.layout;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

/**
 * Controller of the TabPane demo page: closing policy, side, drag policy, adding tabs and tab decorations.
 */
public class TabPaneController {

    @FXML
    private TabPane tabPane;
    @FXML
    private Label tabOutput;
    @FXML
    private ComboBox<TabPane.TabClosingPolicy> closingPolicy;
    @FXML
    private ComboBox<Side> side;
    @FXML
    private ComboBox<TabPane.TabDragPolicy> dragPolicy;

    @FXML
    private TabPane graphicTabs;
    @FXML
    private Tab homeTab;
    @FXML
    private Tab mailTab;
    @FXML
    private Tab settingsTab;
    @FXML
    private CheckBox fixedWidth;
    @FXML
    private CheckBox floating;

    private int tabCounter = 3;

    @FXML
    private void initialize() {
        closingPolicy.getItems().setAll(TabPane.TabClosingPolicy.values());
        closingPolicy.setValue(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabPane.tabClosingPolicyProperty().bind(closingPolicy.valueProperty());
        side.getItems().setAll(Side.values());
        side.setValue(Side.TOP);
        tabPane.sideProperty().bind(side.valueProperty());
        dragPolicy.getItems().setAll(TabPane.TabDragPolicy.values());
        dragPolicy.setValue(TabPane.TabDragPolicy.REORDER);
        tabPane.tabDragPolicyProperty().bind(dragPolicy.valueProperty());

        tabPane.getTabs().forEach(this::registerClosedHandler);
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, tab) ->
                tabOutput.setText(tab == null ? "No tab selected." : "Selected tab: " + tab.getText()));
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        tabOutput.setText(selected == null ? "No tab selected." : "Selected tab: " + selected.getText());

        homeTab.setGraphic(icon("M8 1.5 1 7.5h2v7h4v-4h2v4h4v-7h2z", "#3d8fd1"));
        mailTab.setGraphic(new Circle(5, Color.web("#e5734b")));
        mailTab.setTooltip(new Tooltip("3 unread messages"));
        settingsTab.setGraphic(icon("M8 5a3 3 0 1 0 0 6 3 3 0 0 0 0-6zm0-4 1.2 2.1 2.4-.5.5 2.4L14.2 6 13 8l1.2 2-2.1 1-.5 2.4-2.4-.5L8 15l-1.2-2.1-2.4.5-.5-2.4L1.8 10 3 8 1.8 6l2.1-1 .5-2.4 2.4.5z", "#56b16a"));
        for (Tab tab : graphicTabs.getTabs()) {
            if (tab.getContent() instanceof Label label) {
                label.setPadding(new Insets(16));
            }
        }
        fixedWidth.selectedProperty().addListener((obs, oldValue, fixed) -> {
            graphicTabs.setTabMinWidth(fixed ? 120 : 0);
            graphicTabs.setTabMaxWidth(fixed ? 120 : Double.MAX_VALUE);
        });
        floating.selectedProperty().addListener((obs, oldValue, isFloating) -> {
            if (isFloating) {
                graphicTabs.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
            } else {
                graphicTabs.getStyleClass().remove(TabPane.STYLE_CLASS_FLOATING);
            }
        });
    }

    private static SVGPath icon(String content, String color) {
        SVGPath path = new SVGPath();
        path.setContent(content);
        path.setStyle("-fx-fill: " + color + ";");
        path.setScaleX(0.8);
        path.setScaleY(0.8);
        return path;
    }

    private void registerClosedHandler(Tab tab) {
        tab.setOnClosed(event -> tabOutput.setText("Closed tab: " + tab.getText()));
    }

    @FXML
    private void onAddTab() {
        tabCounter++;
        TextArea content = new TextArea("Content of document " + tabCounter);
        content.setWrapText(true);
        Tab tab = new Tab("Document " + tabCounter, content);
        registerClosedHandler(tab);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
