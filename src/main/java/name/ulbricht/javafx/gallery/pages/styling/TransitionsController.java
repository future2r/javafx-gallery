package name.ulbricht.javafx.gallery.pages.styling;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo page for the ready-made {@link Transition}s, and for combining them with {@link SequentialTransition} and
 * {@link ParallelTransition}.
 */
public class TransitionsController implements Disposable {

    private enum Kind {
        FADE("FadeTransition"),
        TRANSLATE("TranslateTransition"),
        ROTATE("RotateTransition"),
        SCALE("ScaleTransition"),
        FILL("FillTransition"),
        STROKE("StrokeTransition"),
        PATH("PathTransition");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final Map<String, Interpolator> INTERPOLATORS = new LinkedHashMap<>();

    static {
        INTERPOLATORS.put("EASE_BOTH", Interpolator.EASE_BOTH);
        INTERPOLATORS.put("EASE_IN", Interpolator.EASE_IN);
        INTERPOLATORS.put("EASE_OUT", Interpolator.EASE_OUT);
        INTERPOLATORS.put("LINEAR", Interpolator.LINEAR);
        INTERPOLATORS.put("DISCRETE", Interpolator.DISCRETE);
        INTERPOLATORS.put("SPLINE(0.8, 0, 0.2, 1)", Interpolator.SPLINE(0.8, 0, 0.2, 1));
    }

    private static final Color FILL = Color.web("#3d8fd1");
    private static final Color FILL_TO = Color.web("#e5734b");
    private static final Color STROKE = Color.web("#1b4f7a");
    private static final Color STROKE_TO = Color.web("#ffd54f");
    private static final double NODE_SIZE = 60;

    @FXML
    private Pane singleStage;
    @FXML
    private Label singleOutput;
    @FXML
    private ComboBox<Kind> transitionType;
    @FXML
    private Slider duration;
    @FXML
    private Spinner<Integer> cycleCount;
    @FXML
    private CheckBox indefinite;
    @FXML
    private CheckBox autoReverse;
    @FXML
    private ComboBox<String> interpolator;

    @FXML
    private Pane groupStage;
    @FXML
    private Label groupOutput;
    @FXML
    private ToggleGroup groupMode;
    @FXML
    private RadioButton sequentialMode;
    @FXML
    private RadioButton parallelMode;
    @FXML
    private CheckBox groupAutoReverse;

    private final Rectangle node = new Rectangle(NODE_SIZE, NODE_SIZE);
    private final Path motionPath = new Path(
            new MoveTo(50, 110),
            new CubicCurveTo(160, -40, 380, 260, 510, 110));
    private Transition singleTransition;

    private final List<Rectangle> boxes = List.of(
            box(Color.web("#3d8fd1"), 20), box(Color.web("#e5734b"), 85), box(Color.web("#56b16a"), 150));
    private Transition groupTransition;

    @FXML
    private void initialize() {
        initSingle();
        initGroup();
    }

    // --- Example 1 -----------------------------------------------------------------------------------------

    private void initSingle() {
        node.setArcWidth(14);
        node.setArcHeight(14);
        node.setStrokeWidth(4);
        motionPath.setStroke(Color.GRAY);
        motionPath.getStrokeDashArray().setAll(6.0, 6.0);
        singleStage.getChildren().addAll(motionPath, node);

        transitionType.getItems().setAll(Kind.values());
        transitionType.setValue(Kind.TRANSLATE);
        interpolator.getItems().setAll(INTERPOLATORS.keySet());
        interpolator.setValue("EASE_BOTH");
        cycleCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2));
        cycleCount.disableProperty().bind(indefinite.selectedProperty());

        transitionType.valueProperty().addListener((obs, o, n) -> invalidateSingle());
        duration.valueProperty().addListener((obs, o, n) -> invalidateSingle());
        cycleCount.valueProperty().addListener((obs, o, n) -> invalidateSingle());
        indefinite.selectedProperty().addListener((obs, o, n) -> invalidateSingle());
        autoReverse.selectedProperty().addListener((obs, o, n) -> invalidateSingle());
        interpolator.valueProperty().addListener((obs, o, n) -> invalidateSingle());
        invalidateSingle();
    }

    private void invalidateSingle() {
        if (singleTransition != null) {
            singleTransition.stop();
            singleTransition = null;
        }
        Kind kind = transitionType.getValue();
        resetNode(kind);
        singleOutput.textProperty().unbind();
        singleOutput.setText(kind + " – press Play.");
    }

    private void resetNode(Kind kind) {
        boolean moving = kind == Kind.TRANSLATE || kind == Kind.PATH;
        node.setTranslateX(moving ? 30 : (singleStage.getPrefWidth() - NODE_SIZE) / 2);
        node.setTranslateY(80);
        node.setOpacity(1);
        node.setRotate(0);
        node.setScaleX(1);
        node.setScaleY(1);
        node.setFill(FILL);
        node.setStroke(STROKE);
        motionPath.setVisible(kind == Kind.PATH);
    }

    private Transition createSingle(Kind kind) {
        Duration d = Duration.seconds(duration.getValue());
        Transition transition = switch (kind) {
            case FADE -> {
                FadeTransition t = new FadeTransition(d, node);
                t.setFromValue(1);
                t.setToValue(0.1);
                yield t;
            }
            case TRANSLATE -> {
                TranslateTransition t = new TranslateTransition(d, node);
                t.setFromX(30);
                t.setToX(singleStage.getPrefWidth() - NODE_SIZE - 30);
                yield t;
            }
            case ROTATE -> {
                RotateTransition t = new RotateTransition(d, node);
                t.setByAngle(360);
                yield t;
            }
            case SCALE -> {
                ScaleTransition t = new ScaleTransition(d, node);
                t.setToX(2.2);
                t.setToY(2.2);
                yield t;
            }
            case FILL -> new FillTransition(d, node, FILL, FILL_TO);
            case STROKE -> new StrokeTransition(d, node, STROKE, STROKE_TO);
            case PATH -> {
                PathTransition t = new PathTransition(d, motionPath, node);
                t.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                yield t;
            }
        };
        transition.setCycleCount(indefinite.isSelected() ? Animation.INDEFINITE : cycleCount.getValue());
        transition.setAutoReverse(autoReverse.isSelected());
        transition.setInterpolator(INTERPOLATORS.getOrDefault(interpolator.getValue(), Interpolator.EASE_BOTH));
        return transition;
    }

    @FXML
    private void onPlaySingle() {
        Kind kind = transitionType.getValue();
        if (singleTransition == null || singleTransition.getStatus() == Animation.Status.STOPPED) {
            if (singleTransition != null) {
                resetNode(kind);
            }
            singleTransition = createSingle(kind);
            singleOutput.textProperty().bind(Bindings.concat(kind.toString(), " – status: ",
                    singleTransition.statusProperty()));
        }
        singleTransition.play();
    }

    @FXML
    private void onPauseSingle() {
        if (singleTransition != null) {
            singleTransition.pause();
        }
    }

    @FXML
    private void onStopSingle() {
        if (singleTransition != null) {
            singleTransition.stop();
        }
    }

    // --- Example 2 -----------------------------------------------------------------------------------------

    private static Rectangle box(Color color, double y) {
        Rectangle box = new Rectangle(50, 50, color);
        box.setArcWidth(10);
        box.setArcHeight(10);
        box.setTranslateY(y);
        return box;
    }

    private void initGroup() {
        groupStage.getChildren().addAll(boxes);
        groupMode.selectedToggleProperty().addListener((obs, o, n) -> invalidateGroup());
        groupAutoReverse.selectedProperty().addListener((obs, o, n) -> invalidateGroup());
        invalidateGroup();
    }

    private void invalidateGroup() {
        if (groupTransition != null) {
            groupTransition.stop();
            groupTransition = null;
        }
        for (Rectangle box : boxes) {
            box.setTranslateX(30);
            box.setRotate(0);
        }
        groupOutput.textProperty().unbind();
        groupOutput.setText(modeName() + " – press Play.");
    }

    private String modeName() {
        return parallelMode.isSelected() ? "ParallelTransition" : "SequentialTransition";
    }

    private Transition createGroup() {
        Transition[] children = new Transition[boxes.size()];
        for (int i = 0; i < boxes.size(); i++) {
            Rectangle box = boxes.get(i);
            TranslateTransition move = new TranslateTransition(Duration.seconds(1), box);
            move.setFromX(30);
            move.setToX(groupStage.getPrefWidth() - 50 - 30);
            RotateTransition turn = new RotateTransition(Duration.seconds(1), box);
            turn.setFromAngle(0);
            turn.setToAngle(180);
            children[i] = new ParallelTransition(move, turn);
        }
        Transition transition = sequentialMode.isSelected()
                ? new SequentialTransition(children)
                : new ParallelTransition(children);
        if (groupAutoReverse.isSelected()) {
            transition.setCycleCount(2);
            transition.setAutoReverse(true);
        }
        return transition;
    }

    @FXML
    private void onPlayGroup() {
        if (groupTransition == null || groupTransition.getStatus() == Animation.Status.STOPPED) {
            groupTransition = createGroup();
            groupOutput.textProperty().bind(Bindings.concat(modeName(), " – status: ",
                    groupTransition.statusProperty()));
        }
        groupTransition.play();
    }

    @FXML
    private void onPauseGroup() {
        if (groupTransition != null) {
            groupTransition.pause();
        }
    }

    @FXML
    private void onStopGroup() {
        if (groupTransition != null) {
            groupTransition.stop();
        }
    }

    @Override
    public void dispose() {
        if (singleTransition != null) {
            singleTransition.stop();
        }
        if (groupTransition != null) {
            groupTransition.stop();
        }
    }
}
