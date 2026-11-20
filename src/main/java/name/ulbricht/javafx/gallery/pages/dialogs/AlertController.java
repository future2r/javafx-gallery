package name.ulbricht.javafx.gallery.pages.dialogs;

import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Controller of the Alert demo page: all alert types, custom buttons and expandable content.
 */
public class AlertController {

    @FXML
    private ComboBox<AlertType> alertType;
    @FXML
    private TextField headerText;
    @FXML
    private TextField contentText;
    @FXML
    private CheckBox modal;
    @FXML
    private Label typeOutput;
    @FXML
    private Label customOutput;
    @FXML
    private CheckBox expanded;
    @FXML
    private Label expandableOutput;

    @FXML
    private void initialize() {
        alertType.getItems().setAll(AlertType.values());
        alertType.setValue(AlertType.INFORMATION);
    }

    @FXML
    private void onShowAlert(ActionEvent event) {
        AlertType type = alertType.getValue() == null ? AlertType.INFORMATION : alertType.getValue();
        Alert alert = createAlert(type, event);
        alert.setTitle(type + " alert");
        alert.setHeaderText(headerText.getText().isBlank() ? null : headerText.getText());
        alert.setContentText(contentText.getText());
        if (type == AlertType.NONE) {
            // an alert of type NONE has no buttons and could not be closed otherwise
            alert.getButtonTypes().setAll(ButtonType.OK);
        }
        if (modal.isSelected()) {
            Optional<ButtonType> result = alert.showAndWait();
            typeOutput.setText("Result: " + result.map(ButtonType::getText).orElse("(no button)"));
        } else {
            alert.initModality(Modality.NONE);
            alert.setOnHidden(e -> typeOutput.setText("Closed non-modal alert, result: "
                    + (alert.getResult() == null ? "(no button)" : alert.getResult().getText())));
            alert.show();
            typeOutput.setText("show() returned immediately – the alert is still open.");
        }
    }

    @FXML
    private void onCustomButtons(ActionEvent event) {
        ButtonType save = new ButtonType("Save", ButtonData.YES);
        ButtonType dontSave = new ButtonType("Don't save", ButtonData.NO);
        ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        Alert alert = createAlert(AlertType.CONFIRMATION, event);
        alert.getButtonTypes().setAll(save, dontSave, cancel);
        alert.setTitle("Close document");
        alert.setHeaderText("Save changes to \"Untitled.txt\"?");
        alert.setContentText("Your changes will be lost if you don't save them.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() == cancel) {
            customOutput.setText("Cancelled – the document stays open.");
        } else if (result.get() == save) {
            customOutput.setText("Saved and closed.");
        } else {
            customOutput.setText("Closed without saving.");
        }
    }

    @FXML
    private void onExpandable(ActionEvent event) {
        Exception exception = new IllegalStateException("Could not connect to the server",
                new java.net.ConnectException("Connection refused"));
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        TextArea details = new TextArea(stackTrace.toString());
        details.setEditable(false);
        details.setWrapText(false);
        details.setPrefRowCount(10);
        VBox.setVgrow(details, Priority.ALWAYS);
        VBox expandable = new VBox(6, new Label("Stack trace:"), details);

        Alert alert = createAlert(AlertType.ERROR, event);
        alert.setTitle("Error");
        alert.setHeaderText("The operation failed");
        alert.setContentText(exception.getMessage());
        alert.getDialogPane().setExpandableContent(expandable);
        alert.getDialogPane().setExpanded(expanded.isSelected());
        alert.setResizable(true);
        alert.showAndWait();
        expandableOutput.setText("Details were " + (alert.getDialogPane().isExpanded() ? "expanded" : "collapsed")
                + " when the dialog closed.");
    }

    private static Alert createAlert(AlertType type, ActionEvent event) {
        Alert alert = new Alert(type);
        alert.initOwner(((Node) event.getSource()).getScene().getWindow());
        ThemeManager.get().style(alert);
        return alert;
    }
}
