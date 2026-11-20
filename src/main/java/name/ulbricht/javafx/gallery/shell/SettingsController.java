package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.theme.Theme;
import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * Settings page: theme selection and information about the runtime.
 */
public class SettingsController {

    @FXML
    private ToggleGroup themeGroup;
    @FXML
    private RadioButton lightTheme;
    @FXML
    private RadioButton darkTheme;
    @FXML
    private RadioButton systemTheme;
    @FXML
    private Label systemSchemeLabel;
    @FXML
    private Label javaVersion;
    @FXML
    private Label javafxVersion;
    @FXML
    private Label operatingSystem;

    @FXML
    private void initialize() {
        ThemeManager themes = ThemeManager.get();
        lightTheme.setUserData(Theme.LIGHT);
        darkTheme.setUserData(Theme.DARK);
        systemTheme.setUserData(Theme.SYSTEM);
        lightTheme.setText(Theme.LIGHT.getDisplayName());
        darkTheme.setText(Theme.DARK.getDisplayName());
        systemTheme.setText(Theme.SYSTEM.getDisplayName());

        switch (themes.getTheme()) {
            case LIGHT -> lightTheme.setSelected(true);
            case DARK -> darkTheme.setSelected(true);
            case SYSTEM -> systemTheme.setSelected(true);
        }
        themeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                themes.setTheme((Theme) newToggle.getUserData());
            }
        });

        systemSchemeLabel.textProperty().bind(Platform.getPreferences().colorSchemeProperty()
                .map(scheme -> "The operating system currently uses the " + scheme.name().toLowerCase() + " color scheme."));

        javaVersion.setText(System.getProperty("java.vendor") + " " + Runtime.version());
        javafxVersion.setText(System.getProperty("javafx.runtime.version", "unknown"));
        operatingSystem.setText(System.getProperty("os.name") + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")");
    }
}
