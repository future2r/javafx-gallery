package name.ulbricht.javafx.gallery.catalog;

/**
 * The categories of the gallery, in navigation order. Each category carries a simple SVG icon (24×24 view box).
 */
public enum Category {
    BASIC_INPUT("Basic input",
            "Buttons, check boxes, sliders, pickers and other controls that let users enter or choose values.",
            "M4 7h16v10H4z M7 11h10v2H7z"),
    TEXT("Text",
            "Controls for displaying and editing plain and rich text.",
            "M5 5h14v3h-2V7h-4v10h2v2H9v-2h2V7H7v1H5z"),
    COLLECTIONS("Collections",
            "Lists, tables and trees for presenting and selecting items from a collection.",
            "M4 5h3v3H4z M9 5h11v3H9z M4 10.5h3v3H4z M9 10.5h11v3H9z M4 16h3v3H4z M9 16h11v3H9z"),
    LAYOUT("Layout",
            "Panes that arrange their children: boxes, grids, stacks, split panes, scrolling and more.",
            "M3 3h8v8H3z M13 3h8v5h-8z M13 10h8v11h-8z M3 13h8v8H3z"),
    MENUS_TOOLBARS("Menus & toolbars",
            "Menu bars, context menus and toolbars that give access to commands.",
            "M3 4h18v3H3z M3 10h12v2H3z M3 14h12v2H3z M3 18h12v2H3z"),
    DIALOGS_FLYOUTS("Dialogs & flyouts",
            "Alerts, dialogs, tooltips, popups and file choosers.",
            "M3 4h18v13H8l-5 4z M7 9h10v2H7z"),
    STATUS_INFO("Status & info",
            "Controls that show progress or visually separate content.",
            "M3 8h18v8H3z M4.5 9.5h15v5h-15z M6 11h8v2H6z"),
    NAVIGATION("Navigation",
            "Controls for moving between pages and views.",
            "M4 6h6v3H4z M11 6h9v3h-9z M4 10h16v9H4z"),
    MEDIA_WEB("Media & web",
            "Display images, play audio and video, and render web content.",
            "M3 5h18v14H3z M10 9v6l5-3z"),
    CHARTS("Charts",
            "The chart controls of JavaFX for visualizing data.",
            "M4 20V4h2v14h14v2z M8 16v-5h3v5z M13 16V8h3v8z M18 16v-3h2v3z"),
    SHAPES_GRAPHICS("Shapes & graphics",
            "2D shapes, immediate mode drawing on a canvas, and 3D scenes.",
            "M3 21l6-11 6 11z M16 3a5 5 0 1 0 0.01 0z"),
    STYLING_MOTION("Styling & motion",
            "Visual effects, animations, transitions and CSS styling.",
            "M12 2l2.5 6.5L21 9l-5 4.5L17.5 21 12 17l-5.5 4L8 13.5 3 9l6.5-.5z");

    private final String title;
    private final String description;
    private final String iconPath;

    Category(String title, String description, String iconPath) {
        this.title = title;
        this.description = description;
        this.iconPath = iconPath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    /** SVG path data of the category icon, designed for a 24×24 view box. */
    public String getIconPath() {
        return iconPath;
    }
}
