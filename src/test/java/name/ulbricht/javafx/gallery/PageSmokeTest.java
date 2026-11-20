package name.ulbricht.javafx.gallery;

import name.ulbricht.javafx.gallery.catalog.Category;
import name.ulbricht.javafx.gallery.catalog.ControlPage;
import name.ulbricht.javafx.gallery.catalog.GalleryCatalog;
import name.ulbricht.javafx.gallery.shell.DemoPage;
import name.ulbricht.javafx.gallery.shell.Destination;
import name.ulbricht.javafx.gallery.shell.Disposable;
import name.ulbricht.javafx.gallery.shell.Navigator;
import name.ulbricht.javafx.gallery.theme.Theme;
import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Loads every page of the catalog and checks that FXML, controller and CSS work together.
 * <p>
 * With {@code -Dgallery.snapshots=<dir>} every page is additionally rendered to a PNG file in the light and the dark
 * theme, which is handy for a visual review.
 */
class PageSmokeTest {

    private static final String SNAPSHOT_DIR = System.getProperty("gallery.snapshots");

    private static Navigator navigator;

    @BeforeAll
    static void startToolkit() throws Exception {
        FxToolkit.registerPrimaryStage();
        navigator = new Navigator(url -> { });
    }

    static Stream<String> pageIds() {
        return GalleryCatalog.all().stream().map(ControlPage::id);
    }

    @Test
    void catalogIsConsistent() {
        Set<String> ids = new HashSet<>();
        Set<String> fxmls = new HashSet<>();
        for (ControlPage page : GalleryCatalog.all()) {
            assertTrue(ids.add(page.id()), "Duplicate id " + page.id());
            assertTrue(fxmls.add(page.fxml()), "Duplicate FXML " + page.fxml());
            assertNotNull(getClass().getResource(page.fxmlResource()), "Missing " + page.fxmlResource());
        }
        for (Category category : Category.values()) {
            assertTrue(!GalleryCatalog.byCategory(category).isEmpty(), "Empty category " + category);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("pageIds")
    void pageLoads(String id) throws Exception {
        ControlPage page = GalleryCatalog.byId(id).orElseThrow();
        Navigator.LoadedView view = onFx(() -> {
            Navigator.LoadedView loaded = navigator.loadPage(page);
            Scene scene = render(loaded.root());
            if (SNAPSHOT_DIR != null) {
                snapshot(scene, id);
            }
            if (loaded.controller() instanceof Disposable disposable) {
                disposable.dispose();
            }
            return loaded;
        });
        DemoPage demoPage = assertInstanceOf(DemoPage.class, view.root());
        assertEquals(page, demoPage.getPage());
        assertNotNull(view.controller(), "No controller for " + page.fxml());
        assertTrue(demoPage.getChildren().size() > 1, "Page " + id + " has no examples");
    }

    @ParameterizedTest
    @EnumSource(Theme.class)
    void navigationWorksInEveryTheme(Theme theme) throws Exception {
        onFx(() -> {
            ThemeManager.get().setTheme(theme);
            List<Destination> destinations = Stream.<Destination>concat(
                    Stream.of(new Destination.Home(), new Destination.Settings()),
                    Stream.of(Category.values()).map(Destination.CategoryOverview::new)).toList();
            for (Destination destination : destinations) {
                navigator.navigate(destination);
                Scene scene = render((Parent) navigator.viewProperty().get());
                if (SNAPSHOT_DIR != null && theme != Theme.SYSTEM) {
                    snapshot(scene, "_" + destination.key().replace(':', '-'));
                }
            }
            navigator.goBack();
            assertEquals(destinations.get(destinations.size() - 2), navigator.getCurrent());
            navigator.disposeCurrent();
            return null;
        });
    }

    @Test
    void shellRendersAfterThemeSwitch() throws Exception {
        onFx(() -> {
            Navigator shellNavigator = new Navigator(url -> { });
            Navigator.LoadedView shell = shellNavigator.loadFxml(
                    GalleryApp.class.getResource("shell/Shell.fxml"));
            Scene scene = new Scene(shell.root(), 1100, 700);
            ThemeManager themes = ThemeManager.get();
            themes.manage(scene);
            shellNavigator.navigate(new Destination.CategoryOverview(Category.LAYOUT));
            for (Theme theme : List.of(Theme.DARK, Theme.LIGHT, Theme.DARK)) {
                themes.setTheme(theme);
                shell.root().applyCss();
                shell.root().layout();
                if (SNAPSHOT_DIR != null) {
                    write(scene.snapshot(null), Path.of(SNAPSHOT_DIR, "shell", "after-" + theme.name().toLowerCase()
                            + "-" + System.nanoTime() + ".png"));
                }
            }
            shellNavigator.disposeCurrent();
            var icons = name.ulbricht.javafx.gallery.shell.AppIcon.create();
            assertEquals(9, icons.size());
            icons.forEach(icon -> assertTrue(!icon.isError() && icon.getWidth() > 0, "Icon not loaded: " + icon.getUrl()));
            return null;
        });
    }

    private static Scene render(Parent root) {
        Parent sceneRoot = root instanceof ScrollPane ? root : new ScrollPane(root);
        if (sceneRoot instanceof ScrollPane scrollPane) {
            scrollPane.setFitToWidth(true);
        }
        Scene scene = new Scene(sceneRoot, 1100, 800);
        ThemeManager.get().manage(scene);
        sceneRoot.applyCss();
        sceneRoot.layout();
        return scene;
    }

    /** Renders the full height of the page (not only the viewport) in light and dark. */
    private static void snapshot(Scene scene, String name) {
        ScrollPane scroller = (ScrollPane) scene.getRoot();
        Parent content = (Parent) scroller.getContent();
        ThemeManager themes = ThemeManager.get();
        Theme previous = themes.getTheme();
        for (Theme theme : List.of(Theme.LIGHT, Theme.DARK)) {
            themes.setTheme(theme);
            content.applyCss();
            content.layout();
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(theme == Theme.DARK ? Color.web("#202124") : Color.web("#f4f4f4"));
            WritableImage image = content.snapshot(parameters, null);
            write(image, Path.of(SNAPSHOT_DIR, theme.name().toLowerCase(), name + ".png"));
        }
        themes.setTheme(previous);
    }

    private static void write(WritableImage image, Path file) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffered.setRGB(x, y, reader.getArgb(x, y));
            }
        }
        try {
            Files.createDirectories(file.getParent());
            ImageIO.write(buffered, "png", file.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static <T> T onFx(java.util.concurrent.Callable<T> action) throws Exception {
        return WaitForAsyncUtils.asyncFx(action).get(60, TimeUnit.SECONDS);
    }
}
