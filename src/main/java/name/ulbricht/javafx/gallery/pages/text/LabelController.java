package name.ulbricht.javafx.gallery.pages.text;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextAlignment;

/**
 * Demo page for {@link Label}: wrapping, graphics, mnemonics and text overrun.
 */
public class LabelController {

    @FXML
    private Label wrapLabel;
    @FXML
    private CheckBox wrapText;
    @FXML
    private ComboBox<TextAlignment> textAlignment;
    @FXML
    private Label graphicLabel;
    @FXML
    private ComboBox<ContentDisplay> contentDisplay;
    @FXML
    private Label gapTitle;
    @FXML
    private Slider graphicTextGap;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameField;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailField;
    @FXML
    private Label overrunLabel;
    @FXML
    private Label overrunOutput;
    @FXML
    private Slider overrunWidth;
    @FXML
    private ComboBox<OverrunStyle> textOverrun;
    @FXML
    private TextField ellipsisString;

    @FXML
    private void initialize() {
        wrapLabel.wrapTextProperty().bind(wrapText.selectedProperty());
        textAlignment.getItems().setAll(TextAlignment.values());
        textAlignment.setValue(TextAlignment.LEFT);
        wrapLabel.textAlignmentProperty().bind(textAlignment.valueProperty());

        SVGPath gear = new SVGPath();
        gear.setContent("M9.4 1l.4 2a5 5 0 0 1 1.4.8l1.9-.7 1.4 2.4-1.5 1.3a5 5 0 0 1 0 1.6l1.5 1.3-1.4 2.4-1.9-.7"
                + "a5 5 0 0 1-1.4.8l-.4 2H6.6l-.4-2a5 5 0 0 1-1.4-.8l-1.9.7L1.5 9.7 3 8.4a5 5 0 0 1 0-1.6L1.5 5.5"
                + "l1.4-2.4 1.9.7a5 5 0 0 1 1.4-.8l.4-2zM8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5z");
        gear.setStyle("-fx-fill: -fx-accent;");
        gear.setScaleX(1.4);
        gear.setScaleY(1.4);
        graphicLabel.setGraphic(gear);
        contentDisplay.getItems().setAll(ContentDisplay.values());
        contentDisplay.setValue(ContentDisplay.LEFT);
        graphicLabel.contentDisplayProperty().bind(contentDisplay.valueProperty());
        graphicLabel.graphicTextGapProperty().bind(graphicTextGap.valueProperty());
        gapTitle.textProperty().bind(graphicTextGap.valueProperty().asString("Graphic text gap: %.0f"));

        nameLabel.setLabelFor(nameField);
        emailLabel.setLabelFor(emailField);

        overrunLabel.setMinWidth(0);
        overrunLabel.prefWidthProperty().bind(overrunWidth.valueProperty());
        overrunLabel.maxWidthProperty().bind(overrunWidth.valueProperty());
        textOverrun.getItems().setAll(OverrunStyle.values());
        textOverrun.setValue(OverrunStyle.ELLIPSIS);
        overrunLabel.textOverrunProperty().bind(textOverrun.valueProperty());
        overrunLabel.ellipsisStringProperty().bind(ellipsisString.textProperty());
        overrunOutput.textProperty().bind(overrunWidth.valueProperty().asString("Label width: %.0f px"));
    }
}
