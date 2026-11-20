package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller of the HBox / VBox demo page: spacing, padding, alignment, fill and grow priorities.
 */
public class BoxesController {

    private static final String[] ALT_CLASSES = {"", "alt-1", "alt-2", "alt-3"};

    @FXML
    private HBox hbox;
    @FXML
    private Label hSpacingLabel;
    @FXML
    private Slider hSpacing;
    @FXML
    private Label hPaddingLabel;
    @FXML
    private Slider hPadding;
    @FXML
    private ComboBox<Pos> hAlignment;
    @FXML
    private CheckBox hFillHeight;

    @FXML
    private VBox vbox;
    @FXML
    private Label vSpacingLabel;
    @FXML
    private Slider vSpacing;
    @FXML
    private Label vPaddingLabel;
    @FXML
    private Slider vPadding;
    @FXML
    private ComboBox<Pos> vAlignment;
    @FXML
    private CheckBox vFillWidth;

    @FXML
    private Label growFirst;
    @FXML
    private Label growSecond;
    @FXML
    private Label growThird;
    @FXML
    private ComboBox<Priority> growFirstPriority;
    @FXML
    private ComboBox<Priority> growSecondPriority;
    @FXML
    private ComboBox<Priority> growThirdPriority;

    @FXML
    private void initialize() {
        hbox.spacingProperty().bind(hSpacing.valueProperty());
        hSpacingLabel.textProperty().bind(Bindings.format("Spacing: %.0f", hSpacing.valueProperty()));
        bindPadding(hbox, hPadding, hPaddingLabel);
        hAlignment.getItems().setAll(Pos.values());
        hAlignment.setValue(hbox.getAlignment());
        hbox.alignmentProperty().bind(hAlignment.valueProperty());
        hbox.fillHeightProperty().bind(hFillHeight.selectedProperty());

        vbox.spacingProperty().bind(vSpacing.valueProperty());
        vSpacingLabel.textProperty().bind(Bindings.format("Spacing: %.0f", vSpacing.valueProperty()));
        bindPadding(vbox, vPadding, vPaddingLabel);
        vAlignment.getItems().setAll(Pos.values());
        vAlignment.setValue(vbox.getAlignment());
        vbox.alignmentProperty().bind(vAlignment.valueProperty());
        vbox.fillWidthProperty().bind(vFillWidth.selectedProperty());

        bindGrow(growFirst, growFirstPriority, Priority.ALWAYS);
        bindGrow(growSecond, growSecondPriority, Priority.NEVER);
        bindGrow(growThird, growThirdPriority, Priority.NEVER);
    }

    private static void bindPadding(Region region, Slider slider, Label label) {
        label.textProperty().bind(Bindings.format("Padding: %.0f", slider.valueProperty()));
        slider.valueProperty().addListener((obs, oldValue, value) -> region.setPadding(new Insets(value.doubleValue())));
    }

    private static void bindGrow(Label box, ComboBox<Priority> combo, Priority initial) {
        combo.getItems().setAll(Priority.values());
        combo.valueProperty().addListener((obs, oldValue, priority) -> HBox.setHgrow(box, priority));
        combo.setValue(initial);
    }

    @FXML
    private void onAddToHBox() {
        addBox(hbox, false);
    }

    @FXML
    private void onRemoveFromHBox() {
        removeBox(hbox);
    }

    @FXML
    private void onAddToVBox() {
        addBox(vbox, true);
    }

    @FXML
    private void onRemoveFromVBox() {
        removeBox(vbox);
    }

    private static void addBox(Pane box, boolean vertical) {
        int index = box.getChildren().size();
        if (index >= 8) {
            return;
        }
        Label label = new Label(String.valueOf(index + 1));
        label.getStyleClass().add("demo-box");
        String alt = ALT_CLASSES[index % ALT_CLASSES.length];
        if (!alt.isEmpty()) {
            label.getStyleClass().add(alt);
        }
        if (vertical) {
            label.setMaxWidth(Double.MAX_VALUE);
        } else {
            label.setMaxHeight(Double.MAX_VALUE);
        }
        box.getChildren().add(label);
    }

    private static void removeBox(Pane box) {
        if (!box.getChildren().isEmpty()) {
            box.getChildren().removeLast();
        }
    }
}
