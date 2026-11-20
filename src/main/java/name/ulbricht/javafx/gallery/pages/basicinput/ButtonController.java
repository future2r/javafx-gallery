package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.shape.SVGPath;

public class ButtonController {

    @FXML
    private Button simpleButton;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableSimple;
    @FXML
    private Label defaultOutput;
    @FXML
    private Button graphicButton;
    @FXML
    private Label graphicOutput;
    @FXML
    private ComboBox<ContentDisplay> contentDisplay;

    private int clicks;
    private int likes;

    @FXML
    private void initialize() {
        simpleButton.disableProperty().bind(disableSimple.selectedProperty());

        SVGPath heart = new SVGPath();
        heart.setContent("M8 14s-6-3.8-6-8a3.5 3.5 0 0 1 6-2.4A3.5 3.5 0 0 1 14 6c0 4.2-6 8-6 8z");
        heart.setStyle("-fx-fill: #d9534f;");
        graphicButton.setGraphic(heart);

        contentDisplay.getItems().setAll(ContentDisplay.values());
        contentDisplay.setValue(ContentDisplay.LEFT);
        graphicButton.contentDisplayProperty().bind(contentDisplay.valueProperty());
    }

    @FXML
    private void onSimpleButton() {
        clicks++;
        simpleOutput.setText("You clicked: Button 1 (" + clicks + "×)");
    }

    @FXML
    private void onDefaultOrCancel(ActionEvent event) {
        defaultOutput.setText("Fired: " + ((Button) event.getSource()).getText());
    }

    @FXML
    private void onGraphicButton() {
        likes++;
        graphicOutput.setText(likes + (likes == 1 ? " like" : " likes"));
    }
}
