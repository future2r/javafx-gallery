package name.ulbricht.javafx.gallery.pages.text;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Demo page for {@link HTMLEditor}: editing, reading and writing the HTML text and rendering it in a
 * {@link WebView}.
 */
public class HTMLEditorController {

    private static final String SAMPLE = """
            <html><body>
            <h2>HTMLEditor</h2>
            <p>The <b>HTMLEditor</b> is a <i>rich text</i> editor based on <u>WebView</u>.</p>
            <ul>
              <li>Fonts, sizes and colors</li>
              <li>Bold, italic, underline and strike-through</li>
              <li>Lists, alignment and indentation</li>
            </ul>
            <p style="color: #3d8fd1;">Its content is plain HTML.</p>
            </body></html>
            """;

    @FXML
    private HTMLEditor editor;
    @FXML
    private TextArea htmlSource;
    @FXML
    private WebView preview;
    @FXML
    private Label previewOutput;

    @FXML
    private void initialize() {
        editor.setHtmlText(SAMPLE);
    }

    @FXML
    private void onShowHtml() {
        htmlSource.setText(editor.getHtmlText());
    }

    @FXML
    private void onLoadSample() {
        editor.setHtmlText(SAMPLE);
        htmlSource.clear();
    }

    @FXML
    private void onClear() {
        editor.setHtmlText("");
        htmlSource.clear();
    }

    @FXML
    private void onUpdatePreview() {
        preview.getEngine().loadContent(editor.getHtmlText());
        previewOutput.setText("Preview updated at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}
