package name.ulbricht.javafx.gallery;

import name.ulbricht.javafx.gallery.settings.AppSettings;
import name.ulbricht.javafx.gallery.shell.AppIcon;
import name.ulbricht.javafx.gallery.shell.Destination;
import name.ulbricht.javafx.gallery.shell.Navigator;
import name.ulbricht.javafx.gallery.shell.ShellController;
import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Objects;

/**
 * The JavaFX Gallery application.
 */
public class GalleryApp extends Application {

    static {
        // The gallery's UI language is English; keep control texts (dialog buttons, color names,
        // number formats) consistent with it regardless of the operating system locale.
        Locale.setDefault(Locale.US);
    }

    private final AppSettings settings = new AppSettings();
    private Navigator navigator;
    private Stage stage;
    private AppSettings.WindowBounds normalBounds;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        ThemeManager themes = ThemeManager.get();
        themes.setTheme(settings.getTheme());
        themes.themeProperty().addListener((obs, oldTheme, newTheme) -> settings.setTheme(newTheme));

        navigator = new Navigator(url -> getHostServices().showDocument(url));
        Navigator.LoadedView shell = navigator.loadFxml(
                Objects.requireNonNull(getClass().getResource("shell/Shell.fxml")));
        ShellController shellController = (ShellController) shell.controller();

        Scene scene = new Scene(shell.root(), 1280, 820);
        themes.manage(scene);
        installShortcuts(scene, shellController);

        navigator.currentProperty().addListener((obs, oldDest, newDest) -> settings.setLastDestination(newDest.key()));
        navigator.navigate(Destination.fromKey(settings.getLastDestination()).orElseGet(Destination.Home::new));

        stage.setTitle("JavaFX Gallery");
        stage.getIcons().setAll(AppIcon.create());
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setScene(scene);
        restoreBounds();
        stage.show();
    }

    @Override
    public void stop() {
        if (navigator != null) {
            navigator.disposeCurrent();
        }
        if (stage != null) {
            storeBounds();
        }
        settings.flush();
    }

    private void installShortcuts(Scene scene, ShellController shellController) {
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN), navigator::goBack);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN),
                shellController::focusSearch);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN),
                shellController::focusSearch);
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.BACK) {
                navigator.goBack();
                event.consume();
            }
        });
    }

    private void restoreBounds() {
        AppSettings.WindowBounds bounds = settings.getWindowBounds();
        if (bounds == null) {
            stage.setWidth(1280);
            stage.setHeight(820);
            stage.centerOnScreen();
        } else {
            stage.setWidth(bounds.width());
            stage.setHeight(bounds.height());
            boolean visible = !Double.isNaN(bounds.x()) && !Screen.getScreensForRectangle(
                    new Rectangle2D(bounds.x(), bounds.y(), bounds.width(), bounds.height())).isEmpty();
            if (visible) {
                stage.setX(bounds.x());
                stage.setY(bounds.y());
            } else {
                stage.centerOnScreen();
            }
            stage.setMaximized(bounds.maximized());
        }
        normalBounds = currentBounds();
        // remember the bounds of the non-maximized window, so that they survive a maximized session
        Runnable track = () -> {
            if (!stage.isMaximized() && !stage.isIconified() && !stage.isFullScreen()) {
                normalBounds = currentBounds();
            }
        };
        stage.xProperty().addListener(o -> track.run());
        stage.yProperty().addListener(o -> track.run());
        stage.widthProperty().addListener(o -> track.run());
        stage.heightProperty().addListener(o -> track.run());
    }

    private void storeBounds() {
        AppSettings.WindowBounds bounds = stage.isMaximized() ? normalBounds : currentBounds();
        if (bounds != null) {
            settings.setWindowBounds(new AppSettings.WindowBounds(bounds.x(), bounds.y(), bounds.width(),
                    bounds.height(), stage.isMaximized()));
        }
    }

    private AppSettings.WindowBounds currentBounds() {
        return new AppSettings.WindowBounds(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight(), false);
    }
}
