package name.ulbricht.javafx.gallery.pages.shapes;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Demo page for {@link Canvas}: drawing primitives through a {@link GraphicsContext}, a freehand drawing surface
 * and pixel level access with a {@link PixelWriter}.
 */
public class CanvasController {

    private static final Color PAPER = Color.web("#fbfbfd");

    @FXML
    private Canvas drawingCanvas;
    @FXML
    private Slider lineWidth;
    @FXML
    private Slider globalAlpha;
    @FXML
    private Slider rotation;
    @FXML
    private CheckBox showGrid;
    @FXML
    private CheckBox dropShadow;

    @FXML
    private Canvas inkCanvas;
    @FXML
    private Label inkOutput;
    @FXML
    private ColorPicker penColor;
    @FXML
    private Slider penWidth;

    @FXML
    private Canvas fractalCanvas;
    @FXML
    private Slider iterations;
    @FXML
    private Slider hueShift;

    private int strokes;

    @FXML
    private void initialize() {
        lineWidth.valueProperty().addListener((obs, o, n) -> drawScene());
        globalAlpha.valueProperty().addListener((obs, o, n) -> drawScene());
        rotation.valueProperty().addListener((obs, o, n) -> drawScene());
        showGrid.selectedProperty().addListener((obs, o, n) -> drawScene());
        dropShadow.selectedProperty().addListener((obs, o, n) -> drawScene());
        drawScene();

        penColor.setValue(Color.web("#1f4e9c"));
        clearInk();
        inkCanvas.setOnMousePressed(this::startStroke);
        inkCanvas.setOnMouseDragged(this::continueStroke);

        iterations.valueProperty().addListener((obs, o, n) -> drawFractal());
        hueShift.valueProperty().addListener((obs, o, n) -> drawFractal());
        drawFractal();
    }

    // --- Example 1 -----------------------------------------------------------------------------------------

    private void drawScene() {
        GraphicsContext gc = drawingCanvas.getGraphicsContext2D();
        double w = drawingCanvas.getWidth();
        double h = drawingCanvas.getHeight();

        gc.save();
        gc.setFill(PAPER);
        gc.fillRect(0, 0, w, h);
        if (showGrid.isSelected()) {
            gc.setStroke(Color.web("#e1e5ec"));
            gc.setLineWidth(1);
            for (double x = 20.5; x < w; x += 20) {
                gc.strokeLine(x, 0, x, h);
            }
            for (double y = 20.5; y < h; y += 20) {
                gc.strokeLine(0, y, w, y);
            }
        }

        gc.translate(w / 2, h / 2);
        gc.rotate(rotation.getValue());
        gc.translate(-w / 2, -h / 2);
        gc.setGlobalAlpha(globalAlpha.getValue());
        gc.setEffect(dropShadow.isSelected() ? new DropShadow(10, 4, 4, Color.color(0, 0, 0, 0.45)) : null);
        gc.setLineWidth(lineWidth.getValue());
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        // rounded rectangle with a linear gradient
        gc.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#5fb0f0")), new Stop(1, Color.web("#1f5fa8"))));
        gc.fillRoundRect(30, 30, 140, 100, 24, 24);
        gc.setStroke(Color.web("#123d6b"));
        gc.strokeRoundRect(30, 30, 140, 100, 24, 24);

        // oval with a radial gradient
        gc.setFill(new RadialGradient(0, 0, 0.35, 0.35, 0.7, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffe08a")), new Stop(1, Color.web("#e5734b"))));
        gc.fillOval(210, 30, 110, 110);
        gc.setStroke(Color.web("#8a3a1d"));
        gc.strokeOval(210, 30, 110, 110);

        // pie slice
        gc.setFill(Color.web("#56b16a"));
        gc.fillArc(360, 30, 110, 110, 30, 300, ArcType.ROUND);
        gc.setStroke(Color.web("#2e6b3b"));
        gc.strokeArc(360, 30, 110, 110, 30, 300, ArcType.ROUND);

        // triangle polygon
        gc.setFill(Color.web("#b06ad1"));
        gc.fillPolygon(new double[]{40, 110, 170}, new double[]{270, 160, 270}, 3);
        gc.setStroke(Color.web("#5d2a78"));
        gc.strokePolygon(new double[]{40, 110, 170}, new double[]{270, 160, 270}, 3);

        // Bézier path with a dashed stroke
        gc.setStroke(Color.web("#d9534f"));
        gc.setLineDashes(12, 8);
        gc.beginPath();
        gc.moveTo(200, 250);
        gc.bezierCurveTo(250, 140, 320, 300, 370, 190);
        gc.quadraticCurveTo(400, 140, 440, 200);
        gc.stroke();
        gc.setLineDashes();

        // text
        gc.setFill(Color.web("#24292f"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 30));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Canvas", 490, 250);
        gc.restore();
    }

    // --- Example 2 -----------------------------------------------------------------------------------------

    private void startStroke(MouseEvent event) {
        GraphicsContext gc = inkCanvas.getGraphicsContext2D();
        gc.setStroke(penColor.getValue());
        gc.setLineWidth(penWidth.getValue());
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.beginPath();
        gc.moveTo(event.getX(), event.getY());
        gc.lineTo(event.getX(), event.getY());
        gc.stroke();
        strokes++;
        inkOutput.setText(strokes + (strokes == 1 ? " stroke" : " strokes"));
    }

    private void continueStroke(MouseEvent event) {
        GraphicsContext gc = inkCanvas.getGraphicsContext2D();
        gc.lineTo(event.getX(), event.getY());
        gc.stroke();
        // restart the path at the current point, so that earlier segments are not stroked again
        gc.beginPath();
        gc.moveTo(event.getX(), event.getY());
    }

    @FXML
    private void onClearInk() {
        clearInk();
        strokes = 0;
        inkOutput.setText("Canvas cleared.");
    }

    private void clearInk() {
        GraphicsContext gc = inkCanvas.getGraphicsContext2D();
        gc.setFill(PAPER);
        gc.fillRect(0, 0, inkCanvas.getWidth(), inkCanvas.getHeight());
    }

    // --- Example 3 -----------------------------------------------------------------------------------------

    private void drawFractal() {
        int width = (int) fractalCanvas.getWidth();
        int height = (int) fractalCanvas.getHeight();
        int maxIterations = (int) iterations.getValue();
        Color[] palette = new Color[maxIterations];
        for (int i = 0; i < maxIterations; i++) {
            double t = (double) i / maxIterations;
            palette[i] = Color.hsb((hueShift.getValue() + 360 * t) % 360, 0.85, Math.min(1, 0.25 + t * 2.5));
        }

        PixelWriter writer = fractalCanvas.getGraphicsContext2D().getPixelWriter();
        double scale = 3.0 / width;
        for (int py = 0; py < height; py++) {
            double ci = (py - height / 2.0) * scale;
            for (int px = 0; px < width; px++) {
                double cr = (px - width * 0.65) * scale;
                double zr = 0;
                double zi = 0;
                int n = 0;
                while (n < maxIterations && zr * zr + zi * zi <= 4) {
                    double tmp = zr * zr - zi * zi + cr;
                    zi = 2 * zr * zi + ci;
                    zr = tmp;
                    n++;
                }
                writer.setColor(px, py, n == maxIterations ? Color.BLACK : palette[n]);
            }
        }
    }
}
