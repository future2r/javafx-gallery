package name.ulbricht.javafx.gallery.shell;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * The application icon (a blue rounded square with four tiles) in the sizes Windows uses for the window title bar,
 * the taskbar and Alt+Tab. The PNG files are bundled as resources.
 */
public final class AppIcon {

    private static final int[] SIZES = {16, 20, 24, 32, 40, 48, 64, 128, 256};

    private AppIcon() {
    }

    public static List<Image> create() {
        return IntStream.of(SIZES).mapToObj(AppIcon::load).toList();
    }

    private static Image load(int size) {
        String path = "/name/ulbricht/javafx/gallery/icons/app-icon-" + size + ".png";
        URL url = Objects.requireNonNull(AppIcon.class.getResource(path), "Missing icon " + path);
        return new Image(url.toExternalForm());
    }
}
