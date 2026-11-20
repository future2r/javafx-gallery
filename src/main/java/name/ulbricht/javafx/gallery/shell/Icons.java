package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.catalog.Category;
import javafx.scene.layout.Region;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;

/**
 * Creates CSS-colorable icons from SVG path data. The icon is a {@link Region} whose shape is the path;
 * its color is the region's background (style class {@code icon}).
 */
public final class Icons {

    public static final String BACK = "M20 11H7.8l5.6-5.6L12 4l-8 8 8 8 1.4-1.4L7.8 13H20z";
    public static final String SETTINGS =
            "M19.4 13a7.4 7.4 0 0 0 0-2l2.1-1.6-2-3.5-2.5 1a7.3 7.3 0 0 0-1.7-1L15 3.3h-4l-.4 2.6a7.3 7.3 0 0 0-1.7 1"
            + "l-2.5-1-2 3.5L6.6 11a7.4 7.4 0 0 0 0 2l-2.2 1.6 2 3.5 2.5-1a7.3 7.3 0 0 0 1.7 1l.4 2.6h4l.4-2.6a7.3 7.3"
            + " 0 0 0 1.7-1l2.5 1 2-3.5z M13 9a3 3 0 1 0 .01 0z";
    public static final String HOME = "M12 3l9 8h-3v9h-4.5v-6h-3v6H6v-9H3z";
    public static final String PAGE = "M6 3h8l4 4v14H6z M8 5v14h8V8h-3V5z";

    private Icons() {
    }

    public static Region create(String svgPath, double size) {
        SVGPath shape = new SVGPath();
        shape.setContent(svgPath);
        shape.setFillRule(FillRule.EVEN_ODD);
        Region icon = new Region();
        icon.setShape(shape);
        icon.getStyleClass().add("icon");
        icon.setMinSize(size, size);
        icon.setPrefSize(size, size);
        icon.setMaxSize(size, size);
        return icon;
    }

    public static Region forCategory(Category category, double size) {
        return create(category.getIconPath(), size);
    }
}
