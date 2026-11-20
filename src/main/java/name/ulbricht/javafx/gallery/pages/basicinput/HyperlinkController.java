package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextFlow;

/**
 * Demo page for {@link Hyperlink}: visited state, graphic and inline links in a {@link TextFlow}.
 * The links only report their click instead of opening a browser.
 */
public class HyperlinkController {

    @FXML
    private Hyperlink simpleLink;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableLink;
    @FXML
    private Hyperlink graphicLink;
    @FXML
    private Label graphicOutput;
    @FXML
    private TextFlow textFlow;
    @FXML
    private Label inlineOutput;

    @FXML
    private void initialize() {
        simpleLink.disableProperty().bind(disableLink.selectedProperty());

        SVGPath icon = new SVGPath();
        icon.setContent("M6 2h4v2H6.5L4 4v8h8V9.5L12 9v3a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2z"
                + "M9 1h6v6l-2-2-4 4-2-2 4-4z");
        icon.setStyle("-fx-fill: -fx-accent;");
        graphicLink.setGraphic(icon);

        // Text nodes are not styled by Modena; use the theme's text color so that the dark mode works.
        textFlow.getChildren().stream()
                .filter(node -> node.getStyleClass().contains("flow-text"))
                .forEach(node -> node.setStyle("-fx-fill: -fx-text-background-color;"));
    }

    @FXML
    private void onSimpleLink() {
        simpleOutput.setText("Clicked – a real application would now open https://openjfx.io");
    }

    @FXML
    private void onResetVisited() {
        simpleLink.setVisited(false);
        simpleOutput.setText("Visited state reset.");
    }

    @FXML
    private void onGraphicLink() {
        graphicOutput.setText("Clicked: " + graphicLink.getText());
        graphicLink.setVisited(false);
    }

    @FXML
    private void onInlineLink(ActionEvent event) {
        Node source = (Node) event.getSource();
        inlineOutput.setText("Clicked: " + ((Hyperlink) source).getText());
    }
}
