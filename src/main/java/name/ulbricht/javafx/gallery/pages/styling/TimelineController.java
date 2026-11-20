package name.ulbricht.javafx.gallery.pages.styling;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Demo page for {@link Timeline}: key frame animations with interpolators and rate, periodic Timelines for a clock and
 * a stopwatch, and an {@link AnimationTimer} game loop.
 */
public class TimelineController implements Disposable {

    private static final Map<String, Interpolator> INTERPOLATORS = new LinkedHashMap<>();

    static {
        INTERPOLATORS.put("EASE_BOTH", Interpolator.EASE_BOTH);
        INTERPOLATORS.put("LINEAR", Interpolator.LINEAR);
        INTERPOLATORS.put("EASE_IN", Interpolator.EASE_IN);
        INTERPOLATORS.put("EASE_OUT", Interpolator.EASE_OUT);
        INTERPOLATORS.put("SPLINE(0.5, 0, 0.1, 1)", Interpolator.SPLINE(0.5, 0, 0.1, 1));
        INTERPOLATORS.put("SPLINE(0.9, 0.1, 0.9, 0.1)", Interpolator.SPLINE(0.9, 0.1, 0.9, 0.1));
        INTERPOLATORS.put("DISCRETE", Interpolator.DISCRETE);
    }

    private static final Color PAPER = Color.web("#fbfbfd");
    private static final Color[] BALL_COLORS = {
            Color.web("#3d8fd1"), Color.web("#e5734b"), Color.web("#56b16a"), Color.web("#b06ad1"),
            Color.web("#ffb300"), Color.web("#d9534f")};

    @FXML
    private Pane keyFrameStage;
    @FXML
    private Label keyFrameOutput;
    @FXML
    private ComboBox<String> interpolator;
    @FXML
    private Slider rate;
    @FXML
    private CheckBox autoReverse;
    @FXML
    private CheckBox indefinite;

    @FXML
    private Label clockLabel;
    @FXML
    private Label stopwatchLabel;
    @FXML
    private Button stopwatchButton;
    @FXML
    private CheckBox showSeconds;
    @FXML
    private CheckBox twentyFourHours;

    @FXML
    private Canvas ballCanvas;
    @FXML
    private Label fpsOutput;
    @FXML
    private CheckBox timerRunning;
    @FXML
    private Slider ballCount;
    @FXML
    private CheckBox gravity;

    private final Circle circle = new Circle(40, 100, 20);
    private Timeline keyFrameTimeline;

    private Timeline clockTimeline;
    private Timeline stopwatchTimeline;
    private long stopwatchStartNanos;
    private long stopwatchAccumulatedNanos;

    private final Random random = new Random(23);
    private final List<Ball> balls = new ArrayList<>();
    private AnimationTimer ballTimer;

    /** Mutable state of one bouncing ball. */
    private static final class Ball {
        double x;
        double y;
        double vx;
        double vy;
        final double radius;
        final Color color;

        Ball(double x, double y, double vx, double vy, double radius, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
            this.color = color;
        }
    }

    @FXML
    private void initialize() {
        initKeyFrames();
        initClock();
        initBalls();
    }

    // --- Example 1 -----------------------------------------------------------------------------------------

    private void initKeyFrames() {
        keyFrameStage.getChildren().add(circle);
        interpolator.getItems().setAll(INTERPOLATORS.keySet());
        interpolator.setValue("EASE_BOTH");
        interpolator.valueProperty().addListener((obs, o, n) -> rebuildKeyFrameTimeline());
        autoReverse.selectedProperty().addListener((obs, o, n) -> rebuildKeyFrameTimeline());
        indefinite.selectedProperty().addListener((obs, o, n) -> rebuildKeyFrameTimeline());
        rebuildKeyFrameTimeline();
    }

    private void rebuildKeyFrameTimeline() {
        if (keyFrameTimeline != null) {
            keyFrameTimeline.stop();
            keyFrameTimeline.rateProperty().unbind();
        }
        Interpolator interp = INTERPOLATORS.getOrDefault(interpolator.getValue(), Interpolator.EASE_BOTH);
        keyFrameTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(circle.translateXProperty(), 0),
                        new KeyValue(circle.radiusProperty(), 20),
                        new KeyValue(circle.fillProperty(), Color.web("#3d8fd1")),
                        new KeyValue(circle.opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(circle.translateXProperty(), 240, interp),
                        new KeyValue(circle.radiusProperty(), 50, interp),
                        new KeyValue(circle.fillProperty(), Color.web("#e5734b"), interp),
                        new KeyValue(circle.opacityProperty(), 0.6, interp)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(circle.translateXProperty(), 480, interp),
                        new KeyValue(circle.radiusProperty(), 20, interp),
                        new KeyValue(circle.fillProperty(), Color.web("#56b16a"), interp),
                        new KeyValue(circle.opacityProperty(), 1, interp)));
        keyFrameTimeline.setAutoReverse(autoReverse.isSelected());
        keyFrameTimeline.setCycleCount(indefinite.isSelected() ? Animation.INDEFINITE : 1);
        keyFrameTimeline.rateProperty().bind(rate.valueProperty());
        Timeline timeline = keyFrameTimeline;
        timeline.currentTimeProperty().addListener((obs, o, time) -> showKeyFrameState(timeline, time));
        timeline.statusProperty().addListener((obs, o, status) ->
                showKeyFrameState(timeline, timeline.getCurrentTime()));
        showKeyFrameState(timeline, Duration.ZERO);
        timeline.play();
    }

    private void showKeyFrameState(Timeline timeline, Duration time) {
        keyFrameOutput.setText(String.format(Locale.ROOT, "Status: %s, current time: %.2f s, rate: %.2f",
                timeline.getStatus(), time.toSeconds(), timeline.getRate()));
    }

    @FXML
    private void onPlay() {
        keyFrameTimeline.play();
    }

    @FXML
    private void onPause() {
        keyFrameTimeline.pause();
    }

    @FXML
    private void onStop() {
        keyFrameTimeline.stop();
    }

    // --- Example 2 -----------------------------------------------------------------------------------------

    private void initClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.millis(200), e -> updateClock()));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        showSeconds.selectedProperty().addListener((obs, o, n) -> updateClock());
        twentyFourHours.selectedProperty().addListener((obs, o, n) -> updateClock());
        updateClock();
        clockTimeline.play();

        stopwatchTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> updateStopwatch()));
        stopwatchTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private void updateClock() {
        String pattern = (twentyFourHours.isSelected() ? "HH:mm" : "hh:mm")
                + (showSeconds.isSelected() ? ":ss" : "")
                + (twentyFourHours.isSelected() ? "" : " a");
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)));
    }

    @FXML
    private void onStartStopwatch() {
        if (stopwatchTimeline.getStatus() == Animation.Status.RUNNING) {
            stopwatchAccumulatedNanos += System.nanoTime() - stopwatchStartNanos;
            stopwatchTimeline.stop();
            stopwatchButton.setText("Start");
        } else {
            stopwatchStartNanos = System.nanoTime();
            stopwatchTimeline.play();
            stopwatchButton.setText("Stop");
        }
        updateStopwatch();
    }

    @FXML
    private void onResetStopwatch() {
        stopwatchAccumulatedNanos = 0;
        stopwatchStartNanos = System.nanoTime();
        updateStopwatch();
    }

    private void updateStopwatch() {
        long nanos = stopwatchAccumulatedNanos;
        if (stopwatchTimeline.getStatus() == Animation.Status.RUNNING) {
            nanos += System.nanoTime() - stopwatchStartNanos;
        }
        long tenths = nanos / 100_000_000L;
        stopwatchLabel.setText(String.format(Locale.ROOT, "%02d:%02d.%d",
                tenths / 600, (tenths / 10) % 60, tenths % 10));
    }

    // --- Example 3 -----------------------------------------------------------------------------------------

    private void initBalls() {
        ballCount.valueProperty().addListener((obs, o, n) -> adjustBallCount());
        adjustBallCount();

        ballTimer = new AnimationTimer() {
            private long last;
            private long fpsWindowStart;
            private int frames;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    fpsWindowStart = now;
                    return;
                }
                double dt = Math.min(0.05, (now - last) / 1e9);
                last = now;
                step(dt);
                drawBalls();
                frames++;
                if (now - fpsWindowStart >= 1_000_000_000L) {
                    fpsOutput.setText(String.format(Locale.ROOT, "%d balls, %.0f frames per second", balls.size(),
                            frames * 1e9 / (now - fpsWindowStart)));
                    frames = 0;
                    fpsWindowStart = now;
                }
            }

            @Override
            public void stop() {
                super.stop();
                last = 0;
            }
        };
        timerRunning.selectedProperty().addListener((obs, wasRunning, running) -> {
            if (running) {
                ballTimer.start();
            } else {
                ballTimer.stop();
            }
        });
        drawBalls();
        ballTimer.start();
    }

    private void adjustBallCount() {
        int count = (int) Math.round(ballCount.getValue());
        while (balls.size() > count) {
            balls.removeLast();
        }
        while (balls.size() < count) {
            double radius = 8 + random.nextDouble() * 14;
            balls.add(new Ball(
                    radius + random.nextDouble() * (ballCanvas.getWidth() - 2 * radius),
                    radius + random.nextDouble() * (ballCanvas.getHeight() / 2),
                    (random.nextDouble() - 0.5) * 400,
                    (random.nextDouble() - 0.5) * 200,
                    radius,
                    BALL_COLORS[balls.size() % BALL_COLORS.length]));
        }
        drawBalls();
    }

    private void step(double dt) {
        double width = ballCanvas.getWidth();
        double height = ballCanvas.getHeight();
        for (Ball ball : balls) {
            if (gravity.isSelected()) {
                ball.vy += 900 * dt;
            }
            ball.x += ball.vx * dt;
            ball.y += ball.vy * dt;
            if (ball.x < ball.radius) {
                ball.x = ball.radius;
                ball.vx = Math.abs(ball.vx);
            } else if (ball.x > width - ball.radius) {
                ball.x = width - ball.radius;
                ball.vx = -Math.abs(ball.vx);
            }
            if (ball.y < ball.radius) {
                ball.y = ball.radius;
                ball.vy = Math.abs(ball.vy);
            } else if (ball.y > height - ball.radius) {
                ball.y = height - ball.radius;
                // lose a little energy on the floor, but keep bouncing
                ball.vy = -Math.max(Math.abs(ball.vy) * 0.9, gravity.isSelected() ? 450 : 0);
            }
        }
    }

    private void drawBalls() {
        GraphicsContext gc = ballCanvas.getGraphicsContext2D();
        gc.setFill(PAPER);
        gc.fillRect(0, 0, ballCanvas.getWidth(), ballCanvas.getHeight());
        for (Ball ball : balls) {
            gc.setFill(ball.color);
            gc.fillOval(ball.x - ball.radius, ball.y - ball.radius, ball.radius * 2, ball.radius * 2);
        }
    }

    @Override
    public void dispose() {
        keyFrameTimeline.stop();
        clockTimeline.stop();
        stopwatchTimeline.stop();
        ballTimer.stop();
    }
}
