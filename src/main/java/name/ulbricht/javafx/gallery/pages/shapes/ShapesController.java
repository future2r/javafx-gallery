package name.ulbricht.javafx.gallery.pages.shapes;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo page for the 2D shapes of {@code javafx.scene.shape}: all shape types with common stroke and fill options,
 * rounded rectangles and arcs, and shape operations.
 */
public class ShapesController {

    private static final Map<String, List<Double>> DASH_STYLES = new LinkedHashMap<>();

    static {
        DASH_STYLES.put("Solid", List.of());
        DASH_STYLES.put("Dashed", List.of(12.0, 6.0));
        DASH_STYLES.put("Dotted", List.of(0.5, 8.0));
        DASH_STYLES.put("Dash-dot", List.of(14.0, 6.0, 0.5, 6.0));
    }

    private static final String UNION = "Union";
    private static final String INTERSECT = "Intersect";
    private static final String SUBTRACT_AB = "Subtract (circle − rectangle)";
    private static final String SUBTRACT_BA = "Subtract (rectangle − circle)";

    @FXML
    private FlowPane shapeGallery;
    @FXML
    private ColorPicker fillColor;
    @FXML
    private ColorPicker strokeColor;
    @FXML
    private Slider strokeWidth;
    @FXML
    private ComboBox<String> dashStyle;
    @FXML
    private ComboBox<StrokeLineCap> lineCap;
    @FXML
    private ComboBox<StrokeLineJoin> lineJoin;
    @FXML
    private ComboBox<StrokeType> strokeType;

    @FXML
    private Rectangle roundRect;
    @FXML
    private Arc arc;
    @FXML
    private Slider arcWidth;
    @FXML
    private Slider arcHeight;
    @FXML
    private Slider arcStart;
    @FXML
    private Slider arcLength;
    @FXML
    private ComboBox<ArcType> arcType;

    @FXML
    private Pane combinePane;
    @FXML
    private Label combineOutput;
    @FXML
    private ComboBox<String> combineOperation;

    private final List<Shape> closedShapes = new ArrayList<>();
    private final List<Shape> openShapes = new ArrayList<>();

    private Shape combineResult;

    @FXML
    private void initialize() {
        initGallery();
        initArcs();
        initCombine();
    }

    // --- Example 1 -----------------------------------------------------------------------------------------

    private void initGallery() {
        Rectangle rectangle = new Rectangle(90, 60);
        rectangle.setArcWidth(16);
        rectangle.setArcHeight(16);
        addClosed("Rectangle", rectangle);
        addClosed("Circle", new Circle(35));
        addClosed("Ellipse", new Ellipse(45, 30));
        addOpen("Line", new Line(0, 0, 90, 60));
        addOpen("Polyline", new Polyline(0, 60, 20, 10, 45, 50, 70, 0, 90, 40));
        addClosed("Polygon", star(35, 15, 5));
        Arc pie = new Arc(0, 0, 40, 40, 30, 300);
        pie.setType(ArcType.ROUND);
        addClosed("Arc", pie);
        addOpen("QuadCurve", new QuadCurve(0, 60, 45, -30, 90, 60));
        addOpen("CubicCurve", new CubicCurve(0, 30, 30, -30, 60, 90, 90, 30));
        Path house = new Path(new MoveTo(0, 70), new VLineTo(30), new LineTo(45, 0), new LineTo(90, 30),
                new VLineTo(70), new HLineTo(60), new ArcTo(15, 15, 0, 30, 70, false, false), new ClosePath());
        addClosed("Path", house);
        SVGPath heart = new SVGPath();
        heart.setContent("M45 75 C15 55 0 40 0 22 A22 22 0 0 1 45 12 A22 22 0 0 1 90 22 C90 40 75 55 45 75 Z");
        addClosed("SVGPath", heart);
        Text text = new Text("Text");
        text.setFont(Font.font("System", FontWeight.BOLD, 44));
        addClosed("Text", text);

        fillColor.setValue(Color.web("#3d8fd1"));
        strokeColor.setValue(Color.web("#1b4f7a"));
        dashStyle.getItems().setAll(DASH_STYLES.keySet());
        dashStyle.setValue("Solid");
        lineCap.getItems().setAll(StrokeLineCap.values());
        lineCap.setValue(StrokeLineCap.SQUARE);
        lineJoin.getItems().setAll(StrokeLineJoin.values());
        lineJoin.setValue(StrokeLineJoin.MITER);
        strokeType.getItems().setAll(StrokeType.values());
        strokeType.setValue(StrokeType.CENTERED);

        fillColor.valueProperty().addListener((obs, o, n) -> applyStyle());
        strokeColor.valueProperty().addListener((obs, o, n) -> applyStyle());
        strokeWidth.valueProperty().addListener((obs, o, n) -> applyStyle());
        dashStyle.valueProperty().addListener((obs, o, n) -> applyStyle());
        lineCap.valueProperty().addListener((obs, o, n) -> applyStyle());
        lineJoin.valueProperty().addListener((obs, o, n) -> applyStyle());
        strokeType.valueProperty().addListener((obs, o, n) -> applyStyle());
        applyStyle();
    }

    private void addClosed(String name, Shape shape) {
        closedShapes.add(shape);
        addToGallery(name, shape);
    }

    private void addOpen(String name, Shape shape) {
        openShapes.add(shape);
        addToGallery(name, shape);
    }

    private void addToGallery(String name, Node shape) {
        StackPane holder = new StackPane(shape);
        holder.setPrefSize(110, 90);
        holder.setMinSize(110, 90);
        VBox cell = new VBox(6, holder, new Label(name));
        cell.setAlignment(Pos.CENTER);
        shapeGallery.getChildren().add(cell);
    }

    private static Polygon star(double outerRadius, double innerRadius, int points) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < points * 2; i++) {
            double radius = i % 2 == 0 ? outerRadius : innerRadius;
            double angle = Math.PI * i / points - Math.PI / 2;
            polygon.getPoints().addAll(Math.cos(angle) * radius, Math.sin(angle) * radius);
        }
        return polygon;
    }

    private void applyStyle() {
        List<Double> dashes = DASH_STYLES.getOrDefault(dashStyle.getValue(), List.of());
        List<Shape> all = new ArrayList<>(closedShapes);
        all.addAll(openShapes);
        for (Shape shape : all) {
            shape.setStroke(strokeColor.getValue());
            shape.setStrokeWidth(strokeWidth.getValue());
            shape.getStrokeDashArray().setAll(dashes);
            shape.setStrokeLineCap(lineCap.getValue());
            shape.setStrokeLineJoin(lineJoin.getValue());
            shape.setStrokeType(strokeType.getValue());
        }
        for (Shape shape : closedShapes) {
            shape.setFill(fillColor.getValue());
        }
        for (Shape shape : openShapes) {
            shape.setFill(null);
        }
    }

    // --- Example 2 -----------------------------------------------------------------------------------------

    private void initArcs() {
        roundRect.arcWidthProperty().bind(arcWidth.valueProperty());
        roundRect.arcHeightProperty().bind(arcHeight.valueProperty());
        arc.startAngleProperty().bind(arcStart.valueProperty());
        arc.lengthProperty().bind(arcLength.valueProperty());
        arcType.getItems().setAll(ArcType.values());
        arcType.setValue(ArcType.ROUND);
        arc.typeProperty().bind(arcType.valueProperty());
    }

    // --- Example 3 -----------------------------------------------------------------------------------------

    private void initCombine() {
        combinePane.getChildren().addAll(operandOutline(circleOperand()), operandOutline(rectangleOperand()));
        combineOperation.getItems().setAll(UNION, INTERSECT, SUBTRACT_AB, SUBTRACT_BA);
        combineOperation.valueProperty().addListener((obs, o, n) -> updateCombine());
        combineOperation.setValue(UNION);
    }

    private static Circle circleOperand() {
        return new Circle(140, 100, 80);
    }

    private static Rectangle rectangleOperand() {
        return new Rectangle(160, 40, 200, 120);
    }

    private static Shape operandOutline(Shape shape) {
        shape.setFill(null);
        shape.setStroke(Color.GRAY);
        shape.getStrokeDashArray().setAll(6.0, 4.0);
        return shape;
    }

    private void updateCombine() {
        String operation = combineOperation.getValue();
        Shape a = circleOperand();
        Shape b = rectangleOperand();
        Shape result = switch (operation) {
            case INTERSECT -> Shape.intersect(a, b);
            case SUBTRACT_AB -> Shape.subtract(a, b);
            case SUBTRACT_BA -> Shape.subtract(b, a);
            default -> Shape.union(a, b);
        };
        result.setFill(Color.web("#b06ad1", 0.75));
        result.setStroke(Color.web("#5d2a78"));
        result.setStrokeWidth(2);
        if (combineResult != null) {
            combinePane.getChildren().remove(combineResult);
        }
        combineResult = result;
        combinePane.getChildren().add(0, result);
        combineOutput.setText(operation + " → " + result.getClass().getSimpleName() + " with "
                + (result instanceof Path path ? path.getElements().size() : 0) + " path elements");
    }
}
