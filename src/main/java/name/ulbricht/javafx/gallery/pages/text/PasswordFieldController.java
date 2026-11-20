package name.ulbricht.javafx.gallery.pages.text;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

/**
 * Demo page for {@link PasswordField}: basic usage, a reveal option and live strength/confirmation checks.
 */
public class PasswordFieldController {

    @FXML
    private PasswordField simpleField;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableField;
    @FXML
    private PasswordField revealPassword;
    @FXML
    private TextField revealPlain;
    @FXML
    private CheckBox showPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private ProgressBar strengthBar;
    @FXML
    private Label strengthLabel;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label confirmOutput;

    @FXML
    private void initialize() {
        simpleField.disableProperty().bind(disableField.selectedProperty());

        revealPlain.textProperty().bindBidirectional(revealPassword.textProperty());
        revealPlain.visibleProperty().bind(showPassword.selectedProperty());
        revealPassword.visibleProperty().bind(showPassword.selectedProperty().not());

        InvalidationListener validation = obs -> validate();
        newPassword.textProperty().addListener(validation);
        confirmPassword.textProperty().addListener(validation);
        validate();
    }

    @FXML
    private void onSimpleAction() {
        // A real application would never display the password; we only show its length.
        simpleOutput.setText("Submitted a password with " + simpleField.getLength() + " characters.");
    }

    private void validate() {
        String password = newPassword.getText();
        int score = strength(password);
        strengthBar.setProgress(score / 5.0);
        strengthLabel.setText(password.isEmpty() ? "" : score <= 2 ? "Weak" : score <= 3 ? "Medium" : "Strong");

        String confirmation = confirmPassword.getText();
        if (confirmation.isEmpty()) {
            confirmOutput.setText("Repeat the password.");
        } else {
            confirmOutput.setText(confirmation.equals(password) ? "Passwords match." : "Passwords do not match.");
        }
    }

    private static int strength(String password) {
        int score = 0;
        if (password.length() >= 8) {
            score++;
        }
        if (password.length() >= 12) {
            score++;
        }
        if (password.chars().anyMatch(Character::isUpperCase) && password.chars().anyMatch(Character::isLowerCase)) {
            score++;
        }
        if (password.chars().anyMatch(Character::isDigit)) {
            score++;
        }
        if (password.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) {
            score++;
        }
        return score;
    }
}
