package name.ulbricht.javafx.gallery.theme;

/** The theme options offered on the settings page. */
public enum Theme {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("Use system setting");

    private final String displayName;

    Theme(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
