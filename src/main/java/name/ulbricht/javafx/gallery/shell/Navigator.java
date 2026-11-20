package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.catalog.ControlPage;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Loads the views of the gallery and keeps the navigation history for the back button.
 */
public final class Navigator {

    private static final String SHELL_ROOT = "/name/ulbricht/javafx/gallery/shell/";

    private final Consumer<String> linkOpener;
    private final Deque<Destination> history = new ArrayDeque<>();
    private final ReadOnlyObjectWrapper<Destination> current = new ReadOnlyObjectWrapper<>(this, "current");
    private final ReadOnlyObjectWrapper<Node> view = new ReadOnlyObjectWrapper<>(this, "view");
    private final ReadOnlyBooleanWrapper canGoBack = new ReadOnlyBooleanWrapper(this, "canGoBack");
    private Object currentController;

    /**
     * @param linkOpener opens external links (e.g. the API documentation) in the browser
     */
    public Navigator(Consumer<String> linkOpener) {
        this.linkOpener = Objects.requireNonNull(linkOpener);
    }

    /** The destination currently shown. */
    public ReadOnlyObjectProperty<Destination> currentProperty() {
        return current.getReadOnlyProperty();
    }

    public Destination getCurrent() {
        return current.get();
    }

    /** The view of the current destination; the shell shows it in its content area. */
    public ReadOnlyObjectProperty<Node> viewProperty() {
        return view.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty canGoBackProperty() {
        return canGoBack.getReadOnlyProperty();
    }

    public void navigate(Destination destination) {
        Objects.requireNonNull(destination);
        if (destination.equals(current.get())) {
            return;
        }
        if (current.get() != null) {
            history.push(current.get());
        }
        show(destination);
    }

    public void goBack() {
        if (!history.isEmpty()) {
            show(history.pop());
        }
    }

    /** Releases the resources of the current page, e.g. when the application stops. */
    public void disposeCurrent() {
        if (currentController instanceof Disposable disposable) {
            disposable.dispose();
        }
        currentController = null;
    }

    private void show(Destination destination) {
        LoadedView loaded = load(destination);
        disposeCurrent();
        currentController = loaded.controller();

        ScrollPane scroller = new ScrollPane(loaded.root());
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("page-scroller");

        view.set(scroller);
        current.set(destination);
        canGoBack.set(!history.isEmpty());
    }

    /** Loads the view of a destination without navigating to it. */
    public LoadedView load(Destination destination) {
        return switch (destination) {
            case Destination.Home ignored -> loadFxml(resource(SHELL_ROOT + "Home.fxml"));
            case Destination.Settings ignored -> loadFxml(resource(SHELL_ROOT + "Settings.fxml"));
            case Destination.CategoryOverview overview -> {
                LoadedView loaded = loadFxml(resource(SHELL_ROOT + "Category.fxml"));
                ((CategoryController) loaded.controller()).setCategory(overview.category());
                yield loaded;
            }
            case Destination.Demo demo -> loadPage(demo.page());
        };
    }

    /** Loads a demo page and fills its header from the catalog entry. */
    public LoadedView loadPage(ControlPage page) {
        LoadedView loaded = loadFxml(resource(page.fxmlResource()));
        if (!(loaded.root() instanceof DemoPage demoPage)) {
            throw new IllegalStateException(page.fxmlResource() + " must use DemoPage as root element");
        }
        demoPage.setPage(page, linkOpener);
        return loaded;
    }

    /** Loads an FXML file; controllers may declare a constructor taking this navigator. */
    public LoadedView loadFxml(URL location) {
        FXMLLoader loader = new FXMLLoader(location);
        loader.setControllerFactory(this::createController);
        try {
            Parent root = loader.load();
            return new LoadedView(root, loader.getController());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + location, e);
        }
    }

    private Object createController(Class<?> type) {
        try {
            for (Constructor<?> constructor : type.getConstructors()) {
                Class<?>[] params = constructor.getParameterTypes();
                if (params.length == 1 && params[0] == Navigator.class) {
                    return constructor.newInstance(this);
                }
            }
            return type.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create controller " + type.getName(), e);
        }
    }

    private static URL resource(String path) {
        return Objects.requireNonNull(Navigator.class.getResource(path), "Missing resource " + path);
    }

    /** A loaded FXML view and its controller (may be {@code null}). */
    public record LoadedView(Parent root, Object controller) {
    }
}
