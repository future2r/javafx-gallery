package name.ulbricht.javafx.gallery.shell;

/**
 * Implemented by page controllers that hold resources (media players, running animations, timers …)
 * which must be released when the user navigates away from the page.
 */
public interface Disposable {

    void dispose();
}
