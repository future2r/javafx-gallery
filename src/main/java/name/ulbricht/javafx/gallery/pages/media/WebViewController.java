package name.ulbricht.javafx.gallery.pages.media;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Controller of the WebView demo page: a small browser with history, zoom and loading state, and the interaction
 * between Java and JavaScript via {@link WebEngine#executeScript(String)} and {@link WebEngine#setOnAlert}.
 * <p>
 * A JavaScript-to-Java bridge object ({@code JSObject.setMember}) is deliberately not used: it would require the
 * bridge's package to be exported to {@code javafx.web}.
 */
public class WebViewController implements Disposable {

    private static final String SAMPLE = "/name/ulbricht/javafx/gallery/web/sample.html";

    @FXML
    private Button backButton;
    @FXML
    private Button forwardButton;
    @FXML
    private TextField addressField;
    @FXML
    private WebView browser;
    @FXML
    private Label browserOutput;
    @FXML
    private Label zoomLabel;
    @FXML
    private Slider zoomSlider;
    @FXML
    private CheckBox javaScriptEnabled;
    @FXML
    private CheckBox contextMenuEnabled;

    @FXML
    private WebView scriptView;
    @FXML
    private TextField messageField;
    @FXML
    private Label scriptOutput;
    @FXML
    private ColorPicker accentPicker;
    @FXML
    private TextField titleField;

    @FXML
    private void initialize() {
        // Example 1
        WebEngine engine = browser.getEngine();
        WebHistory history = engine.getHistory();
        backButton.disableProperty().bind(history.currentIndexProperty().lessThanOrEqualTo(0));
        forwardButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> history.getCurrentIndex() >= history.getEntries().size() - 1,
                history.currentIndexProperty(), history.getEntries()));
        engine.locationProperty().addListener((obs, oldLocation, newLocation) -> addressField.setText(newLocation));
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            String text = "Load state: " + newState;
            if (newState == Worker.State.SUCCEEDED && engine.getTitle() != null) {
                text += " – \"" + engine.getTitle() + "\"";
            } else if (newState == Worker.State.FAILED && engine.getLoadWorker().getException() != null) {
                text += " – " + engine.getLoadWorker().getException().getMessage();
            }
            browserOutput.setText(text);
        });
        engine.setOnAlert(event -> browserOutput.setText("JavaScript alert: " + event.getData()));
        browser.zoomProperty().bind(zoomSlider.valueProperty());
        zoomLabel.textProperty().bind(zoomSlider.valueProperty().multiply(100).asString("Zoom: %.0f %%"));
        engine.javaScriptEnabledProperty().bind(javaScriptEnabled.selectedProperty());
        browser.contextMenuEnabledProperty().bind(contextMenuEnabled.selectedProperty());
        loadSample(engine);

        // Example 2
        WebEngine scriptEngine = scriptView.getEngine();
        scriptEngine.setOnAlert(event -> scriptOutput.setText("Java received alert(): " + event.getData()));
        scriptEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                applyAccent();
            }
        });
        accentPicker.setValue(Color.web("#3d8fd1"));
        accentPicker.valueProperty().addListener((obs, oldColor, newColor) -> applyAccent());
        loadSample(scriptEngine);
    }

    // --- Example 1 ---------------------------------------------------------------------------------------------

    @FXML
    private void onBack() {
        WebHistory history = browser.getEngine().getHistory();
        if (history.getCurrentIndex() > 0) {
            history.go(-1);
        }
    }

    @FXML
    private void onForward() {
        WebHistory history = browser.getEngine().getHistory();
        if (history.getCurrentIndex() < history.getEntries().size() - 1) {
            history.go(1);
        }
    }

    @FXML
    private void onReload() {
        browser.getEngine().reload();
    }

    @FXML
    private void onGo() {
        String address = addressField.getText() == null ? "" : addressField.getText().strip();
        if (address.isEmpty()) {
            return;
        }
        if (!address.contains("://") && !address.startsWith("about:") && !address.startsWith("data:")) {
            address = "https://" + address;
        }
        browser.getEngine().load(address);
    }

    @FXML
    private void onHome() {
        loadSample(browser.getEngine());
    }

    // --- Example 2 ---------------------------------------------------------------------------------------------

    @FXML
    private void onSendMessage() {
        Object result = execute("showMessage(" + toJsString(messageField.getText()) + ")");
        if (result != null) {
            scriptOutput.setText("showMessage() returned: " + result);
        }
    }

    @FXML
    private void onReadClicks() {
        Object result = execute("getClicks()");
        if (result != null) {
            scriptOutput.setText("getClicks() returned " + result + " (" + result.getClass().getSimpleName() + ")");
        }
    }

    @FXML
    private void onSetTitle() {
        Object result = execute("document.getElementById('title').textContent = "
                + toJsString(titleField.getText()));
        if (result != null) {
            scriptOutput.setText("Heading set to: " + result);
        }
    }

    private void applyAccent() {
        Color color = accentPicker.getValue() == null ? Color.web("#3d8fd1") : accentPicker.getValue();
        String hex = String.format(Locale.ROOT, "#%02x%02x%02x",
                Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255), Math.round(color.getBlue() * 255));
        execute("setAccent('" + hex + "')");
    }

    /** Runs a script in the second WebView; JavaScript errors are shown in the output label. */
    private Object execute(String script) {
        try {
            return scriptView.getEngine().executeScript(script);
        } catch (RuntimeException e) {
            // netscape.javascript.JSException, e.g. when the page is not loaded yet
            scriptOutput.setText("JavaScript error: " + e.getMessage());
            return null;
        }
    }

    /** Converts a Java string into a safely quoted JavaScript string literal. */
    private static String toJsString(String text) {
        StringBuilder js = new StringBuilder("'");
        for (char c : (text == null ? "" : text).toCharArray()) {
            switch (c) {
                case '\'' -> js.append("\\'");
                case '\\' -> js.append("\\\\");
                case '\n' -> js.append("\\n");
                case '\r' -> js.append("\\r");
                case ' ' -> js.append("\\u2028");
                case ' ' -> js.append("\\u2029");
                case '<' -> js.append("\\x3c");
                default -> js.append(c);
            }
        }
        return js.append('\'').toString();
    }

    /**
     * Loads the bundled sample page. WebEngine handles file: and jar: URLs itself; for other protocols (e.g. jrt:
     * inside a jlink image) the HTML is read and passed as content.
     */
    private void loadSample(WebEngine engine) {
        URL url = WebViewController.class.getResource(SAMPLE);
        if (url == null) {
            engine.loadContent("<p>Sample page not found.</p>");
            return;
        }
        String protocol = url.getProtocol();
        if ("file".equals(protocol) || "jar".equals(protocol)) {
            engine.load(url.toExternalForm());
            return;
        }
        try (InputStream in = url.openStream()) {
            engine.loadContent(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            engine.loadContent("<p>Could not read the sample page: " + e.getMessage() + "</p>");
        }
    }

    @Override
    public void dispose() {
        // stop scripts and animations of the loaded pages
        browser.getEngine().loadContent("");
        scriptView.getEngine().loadContent("");
    }
}
