package name.ulbricht.javafx.gallery;

/**
 * Plain entry point that does not extend {@link javafx.application.Application}; useful when the application is
 * started from the class path (e.g. from an IDE run configuration without module support).
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        GalleryApp.main(args);
    }
}
