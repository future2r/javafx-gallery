package name.ulbricht.javafx.gallery.pages.text;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Demo page for {@link TextArea}: wrapping and editability, font size and programmatic editing.
 */
public class TextAreaController {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    private TextArea simpleArea;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox wrapText;
    @FXML
    private CheckBox editable;
    @FXML
    private CheckBox disableArea;
    @FXML
    private TextArea fontArea;
    @FXML
    private Label fontSizeTitle;
    @FXML
    private Slider fontSize;
    @FXML
    private TextArea editArea;
    @FXML
    private Label editOutput;

    private int lineCounter;

    @FXML
    private void initialize() {
        simpleArea.wrapTextProperty().bind(wrapText.selectedProperty());
        simpleArea.editableProperty().bind(editable.selectedProperty());
        simpleArea.disableProperty().bind(disableArea.selectedProperty());
        simpleArea.textProperty().addListener(obs -> updateSimpleOutput());
        updateSimpleOutput();

        fontSize.valueProperty().addListener(obs -> applyFontSize());
        applyFontSize();

        InvalidationListener caretListener = obs -> updateEditOutput();
        editArea.caretPositionProperty().addListener(caretListener);
        editArea.selectionProperty().addListener(caretListener);
        updateEditOutput();
    }

    private void updateSimpleOutput() {
        String text = simpleArea.getText();
        long lines = text.isEmpty() ? 0 : text.lines().count();
        simpleOutput.setText(text.length() + " characters, " + lines + " line(s)");
    }

    private void applyFontSize() {
        int size = (int) Math.round(fontSize.getValue());
        fontArea.setStyle("-fx-font-size: " + size + "px;");
        fontSizeTitle.setText("Font size: " + size + " px");
    }

    private void updateEditOutput() {
        IndexRange selection = editArea.getSelection();
        editOutput.setText("Caret position: " + editArea.getCaretPosition()
                + (selection.getLength() > 0 ? ", selected: " + selection.getLength() + " character(s)" : ""));
    }

    @FXML
    private void onInsert() {
        editArea.insertText(editArea.getCaretPosition(), "[" + LocalTime.now().format(TIME) + "]");
        editArea.requestFocus();
    }

    @FXML
    private void onAppend() {
        lineCounter++;
        editArea.appendText("\nAppended line " + lineCounter);
        editArea.requestFocus();
    }

    @FXML
    private void onSelectAll() {
        editArea.selectAll();
        editArea.requestFocus();
    }

    @FXML
    private void onUpperCase() {
        IndexRange selection = editArea.getSelection();
        if (selection.getLength() > 0) {
            editArea.replaceSelection(editArea.getSelectedText().toUpperCase(Locale.ROOT));
            editArea.selectRange(selection.getStart(), selection.getEnd());
        }
        editArea.requestFocus();
    }

    @FXML
    private void onUndo() {
        editArea.undo();
        editArea.requestFocus();
    }

    @FXML
    private void onClear() {
        editArea.clear();
        editArea.requestFocus();
    }
}
