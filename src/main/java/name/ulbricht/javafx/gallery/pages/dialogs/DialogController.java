package name.ulbricht.javafx.gallery.pages.dialogs;

import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.regex.Pattern;

/**
 * Controller of the Dialog demo page: a login dialog with result converter, modality/resizing options and
 * validation through an event filter on the OK button.
 */
public class DialogController {

    /** Result of the login dialog. */
    public record Credentials(String userName, String password) {
    }

    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    @FXML
    private Label loginOutput;
    @FXML
    private ComboBox<Modality> modality;
    @FXML
    private CheckBox resizable;
    @FXML
    private CheckBox showHelpButton;
    @FXML
    private Label optionsOutput;
    @FXML
    private Label subscribeOutput;

    @FXML
    private void initialize() {
        modality.getItems().setAll(Modality.values());
        modality.setValue(Modality.WINDOW_MODAL);
    }

    // --- Example 1 ---------------------------------------------------------------------------------------------

    @FXML
    private void onLogin(ActionEvent event) {
        Dialog<Credentials> dialog = createDialog(event);
        dialog.setTitle("Sign in");
        dialog.setHeaderText("Sign in to your account");

        ButtonType loginType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginType, ButtonType.CANCEL);

        TextField userName = new TextField();
        userName.setPromptText("User name");
        PasswordField password = new PasswordField();
        password.setPromptText("At least 4 characters");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("User name:"), userName);
        form.addRow(1, new Label("Password:"), password);
        dialog.getDialogPane().setContent(form);

        Node loginButton = dialog.getDialogPane().lookupButton(loginType);
        loginButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> userName.getText().isBlank() || password.getText().length() < 4,
                userName.textProperty(), password.textProperty()));

        dialog.setResultConverter(button -> button == loginType
                ? new Credentials(userName.getText().strip(), password.getText())
                : null);
        Platform.runLater(userName::requestFocus);

        dialog.showAndWait().ifPresentOrElse(
                credentials -> loginOutput.setText("Signed in as " + credentials.userName()
                        + " (password with " + credentials.password().length() + " characters)."),
                () -> loginOutput.setText("Sign in cancelled."));
    }

    // --- Example 2 ---------------------------------------------------------------------------------------------

    @FXML
    private void onShowOptionsDialog(ActionEvent event) {
        Modality selectedModality = modality.getValue() == null ? Modality.WINDOW_MODAL : modality.getValue();
        Dialog<ButtonType> dialog = createDialog(event);
        dialog.initModality(selectedModality);
        dialog.setResizable(resizable.isSelected());
        dialog.setTitle("Sync settings");
        dialog.setHeaderText("Synchronize your settings across devices?");

        Label helpText = new Label();
        helpText.setWrapText(true);
        helpText.getStyleClass().add("example-output");
        VBox content = new VBox(10,
                new Label("Modality: " + selectedModality + ", resizable: " + resizable.isSelected()),
                new CheckBox("Include passwords"),
                helpText);
        content.setPrefWidth(360);
        dialog.getDialogPane().setContent(content);

        ButtonType sync = new ButtonType("Sync now", ButtonData.OK_DONE);
        ButtonType later = new ButtonType("Later", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(sync, later);
        if (showHelpButton.isSelected()) {
            ButtonType help = new ButtonType("Help", ButtonData.HELP);
            dialog.getDialogPane().getButtonTypes().add(help);
            // consume the action so that the Help button does not close the dialog
            dialog.getDialogPane().lookupButton(help).addEventFilter(ActionEvent.ACTION, e -> {
                e.consume();
                helpText.setText("Sync keeps your settings identical on all devices you are signed in to.");
            });
        }

        if (selectedModality == Modality.NONE) {
            dialog.setOnHidden(e -> optionsOutput.setText("Non-modal dialog closed with: "
                    + (dialog.getResult() == null ? "(no button)" : dialog.getResult().getText())));
            dialog.show();
            optionsOutput.setText("show() returned immediately – you can still interact with the gallery.");
        } else {
            optionsOutput.setText("Closed with: " + dialog.showAndWait().map(ButtonType::getText).orElse("(no button)"));
        }
    }

    // --- Example 3 ---------------------------------------------------------------------------------------------

    @FXML
    private void onSubscribe(ActionEvent event) {
        Dialog<String> dialog = createDialog(event);
        dialog.setTitle("Newsletter");
        dialog.setHeaderText("Enter your e-mail address");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField email = new TextField();
        email.setPromptText("name@example.com");
        Label error = new Label();
        error.setStyle("-fx-text-fill: #d9534f;");
        VBox content = new VBox(8, email, error);
        content.setPrefWidth(320);
        dialog.getDialogPane().setContent(content);
        email.textProperty().addListener((obs, oldValue, newValue) -> error.setText(""));

        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, e -> {
            if (!EMAIL.matcher(email.getText().strip()).matches()) {
                error.setText("Please enter a valid e-mail address.");
                email.requestFocus();
                e.consume();
            }
        });
        dialog.setResultConverter(button -> button == ButtonType.OK ? email.getText().strip() : null);
        Platform.runLater(email::requestFocus);

        dialog.showAndWait().ifPresentOrElse(
                address -> subscribeOutput.setText("Subscribed: " + address),
                () -> subscribeOutput.setText("Cancelled."));
    }

    private static <R> Dialog<R> createDialog(ActionEvent event) {
        Dialog<R> dialog = new Dialog<>();
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        ThemeManager.get().style(dialog);
        return dialog;
    }
}
