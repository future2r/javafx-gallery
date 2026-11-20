package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;

/**
 * Controller of the FlowPane demo page: wrapping, orientation, gaps, alignment and a tag list.
 */
public class FlowPaneController {

    private static final String[] ALT_CLASSES = {"", "alt-1", "alt-2", "alt-3"};
    private static final String[] WORDS = {
            "JavaFX", "Layout", "FlowPane", "wraps", "its", "children", "like", "words", "in", "a", "paragraph",
            "when", "space", "runs", "out"};

    @FXML
    private FlowPane flow;
    @FXML
    private ComboBox<Orientation> orientation;
    @FXML
    private Label widthLabel;
    @FXML
    private Slider width;
    @FXML
    private Label gapLabel;
    @FXML
    private Slider gap;
    @FXML
    private ComboBox<Pos> alignment;

    @FXML
    private TextField tagField;
    @FXML
    private FlowPane tags;
    @FXML
    private Label tagOutput;

    @FXML
    private void initialize() {
        for (int i = 0; i < WORDS.length; i++) {
            flow.getChildren().add(box(WORDS[i], i));
        }
        orientation.getItems().setAll(Orientation.values());
        orientation.setValue(Orientation.HORIZONTAL);
        flow.orientationProperty().bind(orientation.valueProperty());
        flow.prefWidthProperty().bind(width.valueProperty());
        widthLabel.textProperty().bind(Bindings.format("Width: %.0f", width.valueProperty()));
        flow.hgapProperty().bind(gap.valueProperty());
        flow.vgapProperty().bind(gap.valueProperty());
        gapLabel.textProperty().bind(Bindings.format("Horizontal and vertical gap: %.0f", gap.valueProperty()));
        alignment.getItems().setAll(Pos.values());
        alignment.setValue(Pos.TOP_LEFT);
        flow.alignmentProperty().bind(alignment.valueProperty());

        for (String tag : new String[]{"java", "javafx", "ui", "desktop", "open source", "gallery", "controls"}) {
            addTag(tag);
        }
        updateTagOutput();
    }

    private static Label box(String text, int index) {
        Label label = new Label(text);
        label.getStyleClass().add("demo-box");
        String alt = ALT_CLASSES[index % ALT_CLASSES.length];
        if (!alt.isEmpty()) {
            label.getStyleClass().add(alt);
        }
        return label;
    }

    @FXML
    private void onAddTag() {
        String text = tagField.getText() == null ? "" : tagField.getText().trim();
        if (!text.isEmpty()) {
            addTag(text);
            tagField.clear();
            updateTagOutput();
        }
    }

    private void addTag(String text) {
        Label tag = box(text + "  ×", tags.getChildren().size());
        tag.setCursor(Cursor.HAND);
        tag.setTooltip(new Tooltip("Click to remove"));
        tag.setOnMouseClicked(event -> {
            tags.getChildren().remove(tag);
            tagOutput.setText("Removed \"" + text + "\" – " + tags.getChildren().size() + " tags left.");
        });
        tags.getChildren().add(tag);
    }

    private void updateTagOutput() {
        tagOutput.setText(tags.getChildren().size() + " tags.");
    }
}
