package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.catalog.Category;
import name.ulbricht.javafx.gallery.catalog.ControlPage;
import name.ulbricht.javafx.gallery.catalog.GalleryCatalog;

import java.util.Objects;
import java.util.Optional;

/**
 * A navigation target of the gallery shell.
 */
public sealed interface Destination {

    /** Stable key, used to persist the last visited destination. */
    String key();

    /** Display title, e.g. for the navigation tree. */
    String title();

    /** The start page with tiles for all categories and controls. */
    record Home() implements Destination {
        @Override
        public String key() {
            return "home";
        }

        @Override
        public String title() {
            return "Home";
        }
    }

    /** The settings page. */
    record Settings() implements Destination {
        @Override
        public String key() {
            return "settings";
        }

        @Override
        public String title() {
            return "Settings";
        }
    }

    /** The tile overview of a category. */
    record CategoryOverview(Category category) implements Destination {
        public CategoryOverview {
            Objects.requireNonNull(category);
        }

        @Override
        public String key() {
            return "category:" + category.name();
        }

        @Override
        public String title() {
            return category.getTitle();
        }
    }

    /** A control demo page. */
    record Demo(ControlPage page) implements Destination {
        public Demo {
            Objects.requireNonNull(page);
        }

        @Override
        public String key() {
            return "page:" + page.id();
        }

        @Override
        public String title() {
            return page.title();
        }
    }

    /** Resolves a key created by {@link #key()}. */
    static Optional<Destination> fromKey(String key) {
        if (key == null) {
            return Optional.empty();
        }
        if (key.equals("home")) {
            return Optional.of(new Home());
        }
        if (key.equals("settings")) {
            return Optional.of(new Settings());
        }
        if (key.startsWith("category:")) {
            try {
                return Optional.of(new CategoryOverview(Category.valueOf(key.substring("category:".length()))));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        if (key.startsWith("page:")) {
            return GalleryCatalog.byId(key.substring("page:".length())).map(Demo::new);
        }
        return Optional.empty();
    }
}
