package name.ulbricht.javafx.gallery.pages.media;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Controller of the ImageView demo page. All images are generated at runtime with a {@link PixelWriter} or with
 * {@link javafx.scene.Node#snapshot}, so the page needs no image files.
 */
public class ImageViewController {

    private static final int IMAGE_WIDTH = 480;
    private static final int IMAGE_HEIGHT = 300;

    private static final String[] PIXEL_ART = {
            "................",
            "..XXX......XXX..",
            ".XRRRX....XRRRX.",
            "XRRWRRX..XRRRRRX",
            "XRWRRRRXXRRRRRRX",
            "XRRRRRRRRRRRRRRX",
            "XRRRRRRRRRRRRRRX",
            ".XRRRRRRRRRRRRX.",
            "..XRRRRRRRRRRX..",
            "...XRRRRRRRRX...",
            "....XRRRRRRX....",
            ".....XRRRRX.....",
            "......XRRX......",
            ".......XX.......",
            "................",
            "................"
    };

    @FXML
    private ImageView scaledView;
    @FXML
    private Label scaledOutput;
    @FXML
    private Label fitWidthLabel;
    @FXML
    private Slider fitWidth;
    @FXML
    private Label fitHeightLabel;
    @FXML
    private Slider fitHeight;
    @FXML
    private CheckBox preserveRatio;
    @FXML
    private CheckBox smooth;

    @FXML
    private ImageView smoothPixelView;
    @FXML
    private ImageView sharpPixelView;

    @FXML
    private Pane thumbnailPane;
    @FXML
    private ImageView thumbnailView;
    @FXML
    private Rectangle viewportMarker;
    @FXML
    private ImageView viewportView;
    @FXML
    private Label viewportOutput;
    @FXML
    private Label viewportXLabel;
    @FXML
    private Slider viewportX;
    @FXML
    private Label viewportYLabel;
    @FXML
    private Slider viewportY;
    @FXML
    private Label viewportWidthLabel;
    @FXML
    private Slider viewportWidth;
    @FXML
    private Label viewportHeightLabel;
    @FXML
    private Slider viewportHeight;

    @FXML
    private ImageView rotatedView;
    @FXML
    private Label rotationOutput;
    @FXML
    private Label rotateLabel;
    @FXML
    private Slider rotate;
    @FXML
    private CheckBox mirror;

    private Image landscape;

    @FXML
    private void initialize() {
        landscape = createLandscape(IMAGE_WIDTH, IMAGE_HEIGHT);

        // Example 1
        scaledView.setImage(landscape);
        scaledView.fitWidthProperty().bind(fitWidth.valueProperty());
        scaledView.fitHeightProperty().bind(fitHeight.valueProperty());
        scaledView.preserveRatioProperty().bind(preserveRatio.selectedProperty());
        scaledView.smoothProperty().bind(smooth.selectedProperty());
        fitWidthLabel.textProperty().bind(fitWidth.valueProperty().asString("Fit width: %.0f"));
        fitHeightLabel.textProperty().bind(fitHeight.valueProperty().asString("Fit height: %.0f"));
        scaledView.layoutBoundsProperty().addListener((obs, oldValue, newValue) -> scaledOutput.setText(
                "Image %d × %d px, displayed at %.0f × %.0f px".formatted(IMAGE_WIDTH, IMAGE_HEIGHT,
                        newValue.getWidth(), newValue.getHeight())));
        scaledOutput.setText("Image %d × %d px".formatted(IMAGE_WIDTH, IMAGE_HEIGHT));

        // Example 2
        Image pixelArt = createPixelArt();
        smoothPixelView.setImage(pixelArt);
        sharpPixelView.setImage(pixelArt);

        // Example 3
        thumbnailView.setImage(landscape);
        viewportView.setImage(landscape);
        viewportMarker.getStrokeDashArray().setAll(6.0, 4.0);
        viewportMarker.setMouseTransparent(true);
        InvalidationListener viewportUpdater = obs -> updateViewport();
        for (Slider slider : new Slider[] {viewportX, viewportY, viewportWidth, viewportHeight}) {
            slider.valueProperty().addListener(viewportUpdater);
        }
        updateViewport();

        // Example 4
        rotatedView.setImage(landscape);
        rotatedView.rotateProperty().bind(rotate.valueProperty());
        rotatedView.scaleXProperty().bind(mirror.selectedProperty().map(selected -> selected ? -1.0 : 1.0));
        rotateLabel.textProperty().bind(rotate.valueProperty().asString("Rotate: %.0f°"));
        rotationOutput.setText("Showing the generated landscape.");
    }

    private void updateViewport() {
        double x = Math.min(viewportX.getValue(), IMAGE_WIDTH - 1);
        double y = Math.min(viewportY.getValue(), IMAGE_HEIGHT - 1);
        double width = Math.min(viewportWidth.getValue(), IMAGE_WIDTH - x);
        double height = Math.min(viewportHeight.getValue(), IMAGE_HEIGHT - y);
        viewportView.setViewport(new Rectangle2D(x, y, width, height));

        double scale = thumbnailView.getFitWidth() / IMAGE_WIDTH;
        viewportMarker.setX(x * scale);
        viewportMarker.setY(y * scale);
        viewportMarker.setWidth(width * scale);
        viewportMarker.setHeight(height * scale);

        viewportXLabel.setText("X: %.0f".formatted(x));
        viewportYLabel.setText("Y: %.0f".formatted(y));
        viewportWidthLabel.setText("Width: %.0f".formatted(width));
        viewportHeightLabel.setText("Height: %.0f".formatted(height));
        viewportOutput.setText("Viewport %.0f, %.0f, %.0f × %.0f".formatted(x, y, width, height));
    }

    @FXML
    private void onSnapshot() {
        Rectangle background = new Rectangle(240, 160);
        background.setArcWidth(24);
        background.setArcHeight(24);
        background.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#3d8fd1")), new Stop(1, Color.web("#b06ad1"))));
        Circle circle = new Circle(60, 80, 40, Color.web("#e5734b"));
        Polygon triangle = new Polygon(130, 120, 175, 40, 220, 120);
        triangle.setFill(Color.web("#56b16a"));
        Text text = new Text(20, 148, "snapshot()");
        text.setFont(Font.font("System", FontWeight.BOLD, 16));
        text.setFill(Color.WHITE);
        Group shapes = new Group(background, circle, triangle, text);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage snapshot = shapes.snapshot(parameters, null);
        rotatedView.setImage(snapshot);
        rotationOutput.setText("Showing a snapshot of %.0f × %.0f px.".formatted(
                snapshot.getWidth(), snapshot.getHeight()));
    }

    @FXML
    private void onResetImage() {
        rotatedView.setImage(landscape);
        rotationOutput.setText("Showing the generated landscape.");
    }

    /** Paints a simple sunset landscape pixel by pixel. */
    private static Image createLandscape(int width, int height) {
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();
        double sunX = width * 0.7;
        double sunY = height * 0.55;
        double sunRadius = height * 0.14;
        Color skyTop = Color.rgb(28, 42, 102);
        Color skyHorizon = Color.rgb(246, 152, 96);
        Color sun = Color.rgb(255, 228, 150);
        Color farMountains = Color.rgb(92, 62, 112);
        Color nearMountains = Color.rgb(44, 32, 66);
        Color water = Color.rgb(54, 70, 128);

        for (int x = 0; x < width; x++) {
            double far = height * 0.52 + 26 * Math.sin(x / 47.0) + 12 * Math.sin(x / 17.0 + 1);
            double near = height * 0.68 + 18 * Math.sin(x / 33.0 + 2) + 7 * Math.sin(x / 9.0);
            for (int y = 0; y < height; y++) {
                Color color;
                if (y > height * 0.86) {
                    double t = (y - height * 0.86) / (height * 0.14);
                    boolean glitter = Math.abs(x - sunX) < 40 * (1 - t) + 10 && (y % 6 < 2);
                    color = glitter ? sun.interpolate(water, t) : water.interpolate(Color.BLACK, t * 0.5);
                } else if (y > near) {
                    color = nearMountains;
                } else if (y > far) {
                    color = farMountains.interpolate(nearMountains, (y - far) / Math.max(1, near - far) * 0.6);
                } else if (Math.hypot(x - sunX, y - sunY) < sunRadius) {
                    color = sun;
                } else {
                    color = skyTop.interpolate(skyHorizon, Math.min(1, (double) y / (height * 0.6)));
                }
                writer.setColor(x, y, color);
            }
        }
        return image;
    }

    /** Creates a 16 × 16 heart from {@link #PIXEL_ART}. */
    private static Image createPixelArt() {
        WritableImage image = new WritableImage(16, 16);
        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                Color color = switch (PIXEL_ART[y].charAt(x)) {
                    case 'X' -> Color.rgb(60, 20, 30);
                    case 'R' -> Color.rgb(217, 83, 79);
                    case 'W' -> Color.WHITE;
                    default -> Color.TRANSPARENT;
                };
                writer.setColor(x, y, color);
            }
        }
        return image;
    }
}
