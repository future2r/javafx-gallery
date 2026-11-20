package name.ulbricht.javafx.gallery.settings;

import name.ulbricht.javafx.gallery.theme.Theme;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Persists user settings (theme, window bounds, last visited page) via {@link Preferences}.
 */
public final class AppSettings {

    private static final String KEY_THEME = "theme";
    private static final String KEY_X = "window.x";
    private static final String KEY_Y = "window.y";
    private static final String KEY_WIDTH = "window.width";
    private static final String KEY_HEIGHT = "window.height";
    private static final String KEY_MAXIMIZED = "window.maximized";
    private static final String KEY_LAST_DESTINATION = "lastDestination";

    private final Preferences prefs;

    public AppSettings() {
        this(Preferences.userRoot().node("name/ulbricht/javafx/gallery"));
    }

    public AppSettings(Preferences prefs) {
        this.prefs = prefs;
    }

    public Theme getTheme() {
        try {
            return Theme.valueOf(prefs.get(KEY_THEME, Theme.SYSTEM.name()));
        } catch (IllegalArgumentException e) {
            return Theme.SYSTEM;
        }
    }

    public void setTheme(Theme theme) {
        prefs.put(KEY_THEME, theme.name());
    }

    /** Returns the saved window bounds or {@code null} if none were saved yet. */
    public WindowBounds getWindowBounds() {
        double width = prefs.getDouble(KEY_WIDTH, -1);
        double height = prefs.getDouble(KEY_HEIGHT, -1);
        if (width <= 0 || height <= 0) {
            return null;
        }
        return new WindowBounds(prefs.getDouble(KEY_X, Double.NaN), prefs.getDouble(KEY_Y, Double.NaN),
                width, height, prefs.getBoolean(KEY_MAXIMIZED, false));
    }

    public void setWindowBounds(WindowBounds bounds) {
        prefs.putDouble(KEY_X, bounds.x());
        prefs.putDouble(KEY_Y, bounds.y());
        prefs.putDouble(KEY_WIDTH, bounds.width());
        prefs.putDouble(KEY_HEIGHT, bounds.height());
        prefs.putBoolean(KEY_MAXIMIZED, bounds.maximized());
    }

    public String getLastDestination() {
        return prefs.get(KEY_LAST_DESTINATION, null);
    }

    public void setLastDestination(String key) {
        prefs.put(KEY_LAST_DESTINATION, key);
    }

    public void flush() {
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            System.getLogger(AppSettings.class.getName())
                    .log(System.Logger.Level.WARNING, "Could not store settings", e);
        }
    }

    public record WindowBounds(double x, double y, double width, double height, boolean maximized) {
    }
}
