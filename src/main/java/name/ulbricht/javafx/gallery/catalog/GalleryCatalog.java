package name.ulbricht.javafx.gallery.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static name.ulbricht.javafx.gallery.catalog.Category.*;

/**
 * The single source of truth for all demo pages of the gallery.
 */
public final class GalleryCatalog {

    private static final String CONTROLS = "javafx.controls";
    private static final String GRAPHICS = "javafx.graphics";
    private static final String WEB = "javafx.web";
    private static final String MEDIA = "javafx.media";

    private static final String CTL = "javafx.scene.control.";
    private static final String LAY = "javafx.scene.layout.";
    private static final String CHART = "javafx.scene.chart.";

    private static final List<ControlPage> PAGES = List.of(
            // --- Basic input -------------------------------------------------------------------------------
            page("button", "Button", BASIC_INPUT, "basicinput/Button.fxml", CONTROLS, CTL + "Button",
                    "A button gives the user a way to trigger an immediate action.", "click", "command", "action"),
            page("toggle-button", "ToggleButton", BASIC_INPUT, "basicinput/ToggleButton.fxml", CONTROLS, CTL + "ToggleButton",
                    "A button that can be switched between selected and unselected, optionally as part of a ToggleGroup.",
                    "toggle", "switch", "segmented"),
            page("check-box", "CheckBox", BASIC_INPUT, "basicinput/CheckBox.fxml", CONTROLS, CTL + "CheckBox",
                    "A check box is used to select or deselect action items; it supports an indeterminate state.",
                    "tri-state", "indeterminate", "option"),
            page("radio-button", "RadioButton", BASIC_INPUT, "basicinput/RadioButton.fxml", CONTROLS, CTL + "RadioButton",
                    "Radio buttons let users select one option from a group of mutually exclusive options.",
                    "option", "toggle group", "exclusive"),
            page("hyperlink", "Hyperlink", BASIC_INPUT, "basicinput/Hyperlink.fxml", CONTROLS, CTL + "Hyperlink",
                    "A hyperlink looks like a link in a browser and remembers whether it has been visited.",
                    "link", "url", "visited"),
            page("slider", "Slider", BASIC_INPUT, "basicinput/Slider.fxml", CONTROLS, CTL + "Slider",
                    "A slider lets the user select from a continuous or discrete range of values by moving a thumb.",
                    "range", "track", "tick"),
            page("spinner", "Spinner", BASIC_INPUT, "basicinput/Spinner.fxml", CONTROLS, CTL + "Spinner",
                    "A single-line text field that lets the user step through an ordered sequence of values.",
                    "number", "numeric", "up down", "stepper"),
            page("color-picker", "ColorPicker", BASIC_INPUT, "basicinput/ColorPicker.fxml", CONTROLS, CTL + "ColorPicker",
                    "A control that lets the user pick a color from a palette or define a custom color.",
                    "colour", "palette", "rgb"),
            page("date-picker", "DatePicker", BASIC_INPUT, "basicinput/DatePicker.fxml", CONTROLS, CTL + "DatePicker",
                    "A date picker lets the user select a date from a drop-down calendar.",
                    "calendar", "date", "day"),
            page("combo-box", "ComboBox", BASIC_INPUT, "basicinput/ComboBox.fxml", CONTROLS, CTL + "ComboBox",
                    "A drop-down list of items the user can select from; optionally editable.",
                    "dropdown", "drop-down", "select", "editable"),
            page("choice-box", "ChoiceBox", BASIC_INPUT, "basicinput/ChoiceBox.fxml", CONTROLS, CTL + "ChoiceBox",
                    "A lightweight control for choosing one item from a small set of predefined choices.",
                    "dropdown", "select", "choice"),
            page("menu-button", "MenuButton", BASIC_INPUT, "basicinput/MenuButton.fxml", CONTROLS, CTL + "MenuButton",
                    "A button that shows a menu of commands when clicked.",
                    "dropdown", "menu", "flyout"),
            page("split-menu-button", "SplitMenuButton", BASIC_INPUT, "basicinput/SplitMenuButton.fxml", CONTROLS,
                    CTL + "SplitMenuButton",
                    "A button with two parts: an action area and a drop-down arrow that opens a menu.",
                    "split button", "dropdown", "menu"),

            // --- Text --------------------------------------------------------------------------------------
            page("label", "Label", TEXT, "text/Label.fxml", CONTROLS, CTL + "Label",
                    "A non-editable text control, optionally with a graphic and a mnemonic for another control.",
                    "text block", "caption", "mnemonic", "wrap"),
            page("text-field", "TextField", TEXT, "text/TextField.fxml", CONTROLS, CTL + "TextField",
                    "A single-line text input control, optionally with a TextFormatter for filtering and converting.",
                    "text box", "input", "formatter", "prompt"),
            page("password-field", "PasswordField", TEXT, "text/PasswordField.fxml", CONTROLS, CTL + "PasswordField",
                    "A text field that masks the entered characters.",
                    "password", "secret", "masked"),
            page("text-area", "TextArea", TEXT, "text/TextArea.fxml", CONTROLS, CTL + "TextArea",
                    "A multi-line text input control.",
                    "multiline", "text box", "editor", "wrap"),
            page("html-editor", "HTMLEditor", TEXT, "text/HTMLEditor.fxml", WEB, "javafx.scene.web.HTMLEditor",
                    "A rich text editor that produces HTML.",
                    "rich text", "rich edit", "html", "wysiwyg"),

            // --- Collections -------------------------------------------------------------------------------
            page("list-view", "ListView", COLLECTIONS, "collections/ListView.fxml", CONTROLS, CTL + "ListView",
                    "Displays a vertical or horizontal list of items with selection, custom cells and editing.",
                    "list", "items", "cell factory", "selection"),
            page("table-view", "TableView", COLLECTIONS, "collections/TableView.fxml", CONTROLS, CTL + "TableView",
                    "Displays tabular data in rows and columns with sorting, resizing and editing.",
                    "data grid", "grid", "table", "columns", "sort"),
            page("tree-view", "TreeView", COLLECTIONS, "collections/TreeView.fxml", CONTROLS, CTL + "TreeView",
                    "Displays hierarchical data that can be expanded and collapsed.",
                    "tree", "hierarchy", "nodes"),
            page("tree-table-view", "TreeTableView", COLLECTIONS, "collections/TreeTableView.fxml", CONTROLS,
                    CTL + "TreeTableView",
                    "Combines a TreeView and a TableView: hierarchical rows with multiple columns.",
                    "tree", "table", "hierarchy", "grid"),

            // --- Layout ------------------------------------------------------------------------------------
            page("boxes", "HBox / VBox", LAYOUT, "layout/Boxes.fxml", GRAPHICS, LAY + "HBox",
                    "Lay out children in a single horizontal row or vertical column.",
                    "stack panel", "hbox", "vbox", "row", "column", "spacing"),
            page("grid-pane", "GridPane", LAYOUT, "layout/GridPane.fxml", GRAPHICS, LAY + "GridPane",
                    "Lays out children in a flexible grid of rows and columns.",
                    "grid", "rows", "columns", "form"),
            page("border-pane", "BorderPane", LAYOUT, "layout/BorderPane.fxml", GRAPHICS, LAY + "BorderPane",
                    "Lays out children in top, left, right, bottom and center positions.",
                    "dock panel", "regions", "center"),
            page("stack-pane", "StackPane", LAYOUT, "layout/StackPane.fxml", GRAPHICS, LAY + "StackPane",
                    "Lays out its children on top of each other in a back-to-front stack.",
                    "overlay", "z-order", "layers"),
            page("flow-pane", "FlowPane", LAYOUT, "layout/FlowPane.fxml", GRAPHICS, LAY + "FlowPane",
                    "Lays out children in a flow that wraps at the pane's boundary.",
                    "wrap panel", "wrap", "flow"),
            page("tile-pane", "TilePane", LAYOUT, "layout/TilePane.fxml", GRAPHICS, LAY + "TilePane",
                    "Lays out children in a grid of uniformly sized tiles.",
                    "uniform grid", "tiles", "wrap"),
            page("anchor-pane", "AnchorPane", LAYOUT, "layout/AnchorPane.fxml", GRAPHICS, LAY + "AnchorPane",
                    "Anchors the edges of child nodes to an offset from the pane's edges.",
                    "canvas", "anchor", "relative panel"),
            page("split-pane", "SplitPane", LAYOUT, "layout/SplitPane.fxml", CONTROLS, CTL + "SplitPane",
                    "Displays two or more sides separated by draggable dividers.",
                    "splitter", "divider", "resize"),
            page("scroll-pane", "ScrollPane", LAYOUT, "layout/ScrollPane.fxml", CONTROLS, CTL + "ScrollPane",
                    "Provides a scrolled, clipped viewport of its content.",
                    "scroll viewer", "scroll", "pan", "viewport"),
            page("titled-pane", "TitledPane", LAYOUT, "layout/TitledPane.fxml", CONTROLS, CTL + "TitledPane",
                    "A panel with a title that can be expanded and collapsed.",
                    "expander", "collapsible", "group box"),
            page("accordion", "Accordion", LAYOUT, "layout/Accordion.fxml", CONTROLS, CTL + "Accordion",
                    "A group of TitledPanes of which only one can be expanded at a time.",
                    "expander", "collapsible", "sections"),
            page("tab-pane", "TabPane", List.of(LAYOUT, NAVIGATION), "layout/TabPane.fxml", CONTROLS, CTL + "TabPane",
                    "Lets the user switch between several tabs, each showing its own content.",
                    "tabs", "tab view", "pivot"),

            // --- Menus & toolbars --------------------------------------------------------------------------
            page("menu-bar", "MenuBar", MENUS_TOOLBARS, "menus/MenuBar.fxml", CONTROLS, CTL + "MenuBar",
                    "A horizontal bar of menus with menu items, check and radio items, sub menus and accelerators.",
                    "menu", "menu item", "accelerator", "shortcut"),
            page("context-menu", "ContextMenu", MENUS_TOOLBARS, "menus/ContextMenu.fxml", CONTROLS, CTL + "ContextMenu",
                    "A popup menu shown on right click (or via the keyboard) with commands for an element.",
                    "right click", "popup menu", "menu flyout"),
            page("tool-bar", "ToolBar", MENUS_TOOLBARS, "menus/ToolBar.fxml", CONTROLS, CTL + "ToolBar",
                    "A horizontal or vertical bar of buttons and other controls; overflowing items move to a menu.",
                    "command bar", "toolbar", "overflow"),

            // --- Dialogs & flyouts -------------------------------------------------------------------------
            page("alert", "Alert", DIALOGS_FLYOUTS, "dialogs/Alert.fxml", CONTROLS, CTL + "Alert",
                    "Pre-built dialogs for information, warnings, errors and confirmations.",
                    "message box", "content dialog", "confirmation", "error", "warning"),
            page("text-input-dialog", "TextInputDialog", DIALOGS_FLYOUTS, "dialogs/TextInputDialog.fxml", CONTROLS,
                    CTL + "TextInputDialog",
                    "A dialog that asks the user to enter a line of text.",
                    "input box", "prompt", "dialog"),
            page("choice-dialog", "ChoiceDialog", DIALOGS_FLYOUTS, "dialogs/ChoiceDialog.fxml", CONTROLS,
                    CTL + "ChoiceDialog",
                    "A dialog that lets the user pick one item from a list of choices.",
                    "choice", "dialog", "select"),
            page("dialog", "Dialog", DIALOGS_FLYOUTS, "dialogs/Dialog.fxml", CONTROLS, CTL + "Dialog",
                    "A fully customizable dialog with its own content, buttons and result converter.",
                    "content dialog", "custom dialog", "login", "modal"),
            page("tooltip", "Tooltip", DIALOGS_FLYOUTS, "dialogs/Tooltip.fxml", CONTROLS, CTL + "Tooltip",
                    "Shows additional information when the mouse hovers over a control.",
                    "hint", "hover", "tip"),
            page("popup", "Popup", DIALOGS_FLYOUTS, "dialogs/Popup.fxml", GRAPHICS, "javafx.stage.Popup",
                    "A lightweight window without decorations, e.g. for flyouts and teaching tips.",
                    "flyout", "teaching tip", "popover", "overlay"),
            page("file-chooser", "FileChooser & DirectoryChooser", DIALOGS_FLYOUTS, "dialogs/FileChooser.fxml", GRAPHICS,
                    "javafx.stage.FileChooser",
                    "Native dialogs for opening and saving files and selecting directories.",
                    "file picker", "open", "save", "folder", "directory"),

            // --- Status & info -----------------------------------------------------------------------------
            page("progress-bar", "ProgressBar", STATUS_INFO, "status/ProgressBar.fxml", CONTROLS, CTL + "ProgressBar",
                    "Shows the progress of an operation as a horizontal bar, determinate or indeterminate.",
                    "progress", "task", "loading"),
            page("progress-indicator", "ProgressIndicator", STATUS_INFO, "status/ProgressIndicator.fxml", CONTROLS,
                    CTL + "ProgressIndicator",
                    "Shows progress as a circular indicator, determinate or indeterminate.",
                    "progress ring", "spinner", "busy", "loading"),
            page("separator", "Separator", STATUS_INFO, "status/Separator.fxml", CONTROLS, CTL + "Separator",
                    "A horizontal or vertical line that visually separates groups of content.",
                    "divider", "line", "rule"),

            // --- Navigation --------------------------------------------------------------------------------
            page("pagination", "Pagination", NAVIGATION, "navigation/Pagination.fxml", CONTROLS, CTL + "Pagination",
                    "Navigates between pages of content that are created on demand.",
                    "pager", "pages", "pips", "flip view"),

            // --- Media & web -------------------------------------------------------------------------------
            page("image-view", "ImageView", MEDIA_WEB, "media/ImageView.fxml", GRAPHICS, "javafx.scene.image.ImageView",
                    "Displays an image with optional viewport, scaling and smoothing.",
                    "image", "picture", "viewport", "writable image"),
            page("media-view", "MediaView", MEDIA_WEB, "media/MediaView.fxml", MEDIA, "javafx.scene.media.MediaView",
                    "Plays audio and video through a MediaPlayer.",
                    "media player", "video", "audio", "mp4"),
            page("web-view", "WebView", MEDIA_WEB, "media/WebView.fxml", WEB, "javafx.scene.web.WebView",
                    "Renders HTML content with the WebKit-based WebEngine.",
                    "browser", "html", "web engine", "javascript"),

            // --- Charts ------------------------------------------------------------------------------------
            page("line-chart", "LineChart", CHARTS, "charts/LineChart.fxml", CONTROLS, CHART + "LineChart",
                    "Plots data points of one or more series connected by lines.",
                    "chart", "graph", "line", "series"),
            page("area-chart", "AreaChart", CHARTS, "charts/AreaChart.fxml", CONTROLS, CHART + "AreaChart",
                    "Plots the area between the data line and the axis; also available stacked.",
                    "chart", "area", "stacked area"),
            page("bar-chart", "BarChart", CHARTS, "charts/BarChart.fxml", CONTROLS, CHART + "BarChart",
                    "Plots data as bars for each category.",
                    "chart", "bar", "column", "category"),
            page("stacked-bar-chart", "StackedBarChart", CHARTS, "charts/StackedBarChart.fxml", CONTROLS,
                    CHART + "StackedBarChart",
                    "Plots bars of several series stacked on top of each other per category.",
                    "chart", "stacked", "bar"),
            page("pie-chart", "PieChart", CHARTS, "charts/PieChart.fxml", CONTROLS, CHART + "PieChart",
                    "Displays proportions as slices of a pie.",
                    "chart", "pie", "donut", "slices"),
            page("scatter-chart", "ScatterChart", CHARTS, "charts/ScatterChart.fxml", CONTROLS, CHART + "ScatterChart",
                    "Plots individual data points without connecting lines.",
                    "chart", "scatter", "points"),
            page("bubble-chart", "BubbleChart", CHARTS, "charts/BubbleChart.fxml", CONTROLS, CHART + "BubbleChart",
                    "Plots data points as bubbles whose size represents a third value.",
                    "chart", "bubble", "extra value"),

            // --- Shapes & graphics -------------------------------------------------------------------------
            page("shapes", "2D shapes", SHAPES_GRAPHICS, "shapes/Shapes.fxml", GRAPHICS, "javafx.scene.shape.Shape",
                    "Rectangles, circles, ellipses, lines, polygons, arcs, curves, paths and text as scene graph nodes.",
                    "shape", "rectangle", "circle", "path", "svg", "polygon", "stroke", "fill"),
            page("canvas", "Canvas", SHAPES_GRAPHICS, "shapes/Canvas.fxml", GRAPHICS, "javafx.scene.canvas.Canvas",
                    "An immediate mode drawing surface rendered through a GraphicsContext.",
                    "draw", "paint", "graphics context", "ink"),
            page("3d", "3D graphics", SHAPES_GRAPHICS, "shapes/Graphics3D.fxml", GRAPHICS, "javafx.scene.shape.Shape3D",
                    "Boxes, spheres and cylinders with materials, lights and a perspective camera in a SubScene.",
                    "3d", "box", "sphere", "cylinder", "camera", "light", "subscene"),

            // --- Styling & motion --------------------------------------------------------------------------
            page("effects", "Effects", STYLING_MOTION, "styling/Effects.fxml", GRAPHICS, "javafx.scene.effect.Effect",
                    "Drop shadows, blurs, glows, reflections and color adjustments applied to nodes.",
                    "shadow", "blur", "glow", "reflection", "color adjust"),
            page("transitions", "Transitions", STYLING_MOTION, "styling/Transitions.fxml", GRAPHICS,
                    "javafx.animation.Transition",
                    "Ready-made animations for fading, moving, rotating, scaling and more – sequentially or in parallel.",
                    "animation", "fade", "translate", "rotate", "scale", "motion"),
            page("timeline", "Timeline", STYLING_MOTION, "styling/Timeline.fxml", GRAPHICS, "javafx.animation.Timeline",
                    "Key frame based animations of arbitrary properties with interpolators.",
                    "animation", "key frame", "interpolator", "motion"),
            page("css", "CSS styling", STYLING_MOTION, "styling/Css.fxml", GRAPHICS, "javafx.scene.doc-files.cssref",
                    "Style controls with style classes, pseudo classes, inline styles and looked-up colors.",
                    "css", "style", "theme", "pseudo class", "stylesheet")
    );

    private GalleryCatalog() {
    }

    /** All pages in catalog order. */
    public static List<ControlPage> all() {
        return PAGES;
    }

    /** Pages of the given category (including pages listed under several categories). */
    public static List<ControlPage> byCategory(Category category) {
        return PAGES.stream().filter(p -> p.categories().contains(category)).toList();
    }

    public static Optional<ControlPage> byId(String id) {
        return PAGES.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    /** Pages matching the query, sorted by title. */
    public static List<ControlPage> search(String query) {
        List<ControlPage> result = new ArrayList<>(PAGES.stream().filter(p -> p.matches(query)).toList());
        result.sort(Comparator.comparing(ControlPage::title, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    private static ControlPage page(String id, String title, Category category, String fxml, String module,
                                    String apiClass, String description, String... keywords) {
        return page(id, title, List.of(category), fxml, module, apiClass, description, keywords);
    }

    private static ControlPage page(String id, String title, List<Category> categories, String fxml, String module,
                                    String apiClass, String description, String... keywords) {
        return new ControlPage(id, title, categories, description, fxml, module, apiClass, Arrays.asList(keywords));
    }
}
