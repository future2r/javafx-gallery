package name.ulbricht.javafx.gallery.pages.dialogs;

import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * Controller of the TextInputDialog demo page: a simple text prompt and a prompt with input validation.
 */
public class TextInputDialogController {

    @FXML
    private TextField defaultValue;
    @FXML
    private TextField headerText;
    @FXML
    private TextField promptText;
    @FXML
    private Label nameOutput;
    @FXML
    private Label ageOutput;

    private int age = -1;

    @FXML
    private void initialize() {
        // nothing to prepare – the dialogs are created on demand
    }

    @FXML
    private void onAskName(ActionEvent event) {
        TextInputDialog dialog = createDialog(defaultValue.getText(), event);
        dialog.setTitle("Your name");
        dialog.setHeaderText(headerText.getText().isBlank() ? null : headerText.getText());
        dialog.setContentText("Name:");
        dialog.getEditor().setPromptText(promptText.getText());

        Optional<String> result = dialog.showAndWait();
        nameOutput.setText(result
                .map(name -> name.isBlank() ? "You entered an empty name." : "Hello, " + name.strip() + "!")
                .orElse("Cancelled."));
    }

    @FXML
    private void onAskAge(ActionEvent event) {
        TextInputDialog dialog = createDialog(age < 0 ? "" : Integer.toString(age), event);
        dialog.setTitle("Age");
        dialog.setHeaderText("Enter an age between 0 and 150.");
        dialog.setContentText("Age:");
        dialog.getEditor().setPromptText("e.g. 42");

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !isValidAge(dialog.getEditor().getText()), dialog.getEditor().textProperty()));

        dialog.showAndWait().ifPresentOrElse(text -> {
            age = Integer.parseInt(text.strip());
            ageOutput.setText("Age set to " + age + ".");
        }, () -> ageOutput.setText("Cancelled – age unchanged."));
    }

    private static boolean isValidAge(String text) {
        String value = text == null ? "" : text.strip();
        if (!value.matches("\\d{1,3}")) {
            return false;
        }
        return Integer.parseInt(value) <= 150;
    }

    private static TextInputDialog createDialog(String defaultValue, ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        ThemeManager.get().style(dialog);
        return dialog;
    }
}
