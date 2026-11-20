package name.ulbricht.javafx.gallery.theme;

import javafx.application.Application;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;

import java.util.List;
import java.util.Objects;

/**
 * Applies the Modena theme plus the gallery stylesheet, and – in dark mode – the Modena based dark overrides.
 * <p>
 * A single instance is shared application-wide ({@link #get()}), so that pages can style their dialogs.
 */
public final class ThemeManager {

    private static final String GALLERY_CSS = stylesheet("gallery.css");
    private static final String DARK_CSS = stylesheet("dark.css");

    private static final ThemeManager INSTANCE = new ThemeManager();

    private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>(this, "theme", Theme.SYSTEM);
    private final BooleanBinding dark;

    private ThemeManager() {
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        dark = Bindings.createBooleanBinding(
                () -> switch (Objects.requireNonNullElse(theme.get(), Theme.SYSTEM)) {
                    case LIGHT -> false;
                    case DARK -> true;
                    case SYSTEM -> Platform.getPreferences().getColorScheme() == ColorScheme.DARK;
                },
                theme, Platform.getPreferences().colorSchemeProperty());
    }

    public static ThemeManager get() {
        return INSTANCE;
    }

    public ObjectProperty<Theme> themeProperty() {
        return theme;
    }

    public Theme getTheme() {
        return theme.get();
    }

    public void setTheme(Theme value) {
        theme.set(value);
    }

    /** {@code true} if the effective theme is dark. */
    public BooleanBinding darkProperty() {
        return dark;
    }

    public boolean isDark() {
        return dark.get();
    }

    /** Styles a scene now and keeps it in sync with future theme changes. */
    public void manage(Scene scene) {
        apply(scene.getStylesheets());
        scene.getPreferences().setColorScheme(isDark() ? ColorScheme.DARK : ColorScheme.LIGHT);
        dark.addListener((obs, oldValue, newValue) -> {
            apply(scene.getStylesheets());
            scene.getPreferences().setColorScheme(newValue ? ColorScheme.DARK : ColorScheme.LIGHT);
        });
    }

    /** Styles a parent (e.g. a dialog pane) with the current theme. */
    public void style(Parent parent) {
        apply(parent.getStylesheets());
    }

    /** Styles a dialog with the current theme. */
    public void style(Dialog<?> dialog) {
        style(dialog.getDialogPane());
    }

    private void apply(ObservableList<String> stylesheets) {
        stylesheets.removeAll(GALLERY_CSS, DARK_CSS);
        stylesheets.addAll(0, isDark() ? List.of(GALLERY_CSS, DARK_CSS) : List.of(GALLERY_CSS));
    }

    private static String stylesheet(String name) {
        var url = ThemeManager.class.getResource("/name/ulbricht/javafx/gallery/css/" + name);
        return Objects.requireNonNull(url, "Missing stylesheet " + name).toExternalForm();
    }
}
