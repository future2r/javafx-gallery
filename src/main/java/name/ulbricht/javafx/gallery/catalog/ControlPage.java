package name.ulbricht.javafx.gallery.catalog;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A single demo page of the gallery.
 *
 * @param id          unique, stable id (used for persistence and navigation)
 * @param title       display title
 * @param categories  categories the page appears in; the first one is the primary category
 * @param description short description shown in the page header and on tiles
 * @param fxml        FXML resource, relative to {@code /name/ulbricht/javafx/gallery/pages/}
 * @param apiModule   JavaFX module containing the documented class, e.g. {@code javafx.controls}
 * @param apiClass    fully qualified name of the documented class
 * @param keywords    additional search terms
 */
public record ControlPage(String id, String title, List<Category> categories, String description,
                          String fxml, String apiModule, String apiClass, List<String> keywords) {

    private static final String JAVADOC_BASE = "https://openjfx.io/javadoc/25/";
    private static final String PAGES_ROOT = "/name/ulbricht/javafx/gallery/pages/";

    public ControlPage {
        Objects.requireNonNull(id);
        Objects.requireNonNull(title);
        categories = List.copyOf(categories);
        if (categories.isEmpty()) {
            throw new IllegalArgumentException("Page " + id + " needs at least one category");
        }
        keywords = List.copyOf(keywords);
    }

    public Category primaryCategory() {
        return categories.getFirst();
    }

    /** Absolute resource path of the FXML file. */
    public String fxmlResource() {
        return PAGES_ROOT + fxml;
    }

    /** Link to the official JavaFX API documentation of the demonstrated class. */
    public String javadocUrl() {
        return JAVADOC_BASE + apiModule + "/" + apiClass.replace('.', '/') + ".html";
    }

    /** Display name of the documentation link, e.g. {@code Button} or {@code CSS Reference Guide}. */
    public String apiDisplayName() {
        if (apiClass.endsWith("doc-files.cssref")) {
            return "JavaFX CSS Reference Guide";
        }
        return apiClass.substring(apiClass.lastIndexOf('.') + 1) + " API documentation";
    }

    /** Case-insensitive match against title, keywords, class name and categories. */
    public boolean matches(String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        return title.toLowerCase(Locale.ROOT).contains(q)
                || apiClass.toLowerCase(Locale.ROOT).contains(q)
                || keywords.stream().anyMatch(k -> k.toLowerCase(Locale.ROOT).contains(q))
                || categories.stream().anyMatch(c -> c.getTitle().toLowerCase(Locale.ROOT).contains(q));
    }
}
