package name.ulbricht.javafx.gallery.pages.charts;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.Random;

/**
 * Demo page for {@link LineChart}: a static multi-series chart, live editing of series and data points and a
 * chart updated by a {@link Timeline}.
 */
public class LineChartController implements Disposable {

    private static final String[] MONTHS =
            {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final int LIVE_WINDOW = 60;

    @FXML
    private LineChart<String, Number> temperatureChart;
    @FXML
    private CategoryAxis temperatureXAxis;
    @FXML
    private NumberAxis temperatureYAxis;
    @FXML
    private Label temperatureOutput;
    @FXML
    private TextField temperatureTitle;
    @FXML
    private TextField temperatureXLabel;
    @FXML
    private TextField temperatureYLabel;
    @FXML
    private CheckBox temperatureAnimated;
    @FXML
    private CheckBox temperatureSymbols;
    @FXML
    private CheckBox temperatureLegend;
    @FXML
    private ComboBox<Side> temperatureLegendSide;

    @FXML
    private LineChart<Number, Number> salesChart;
    @FXML
    private NumberAxis salesXAxis;
    @FXML
    private Label salesOutput;

    @FXML
    private LineChart<Number, Number> liveChart;
    @FXML
    private NumberAxis liveXAxis;
    @FXML
    private CheckBox liveRunning;
    @FXML
    private Slider liveSpeed;

    private final Random random = new Random(42);
    private int nextWeek = 1;
    private int seriesCounter;

    private final XYChart.Series<Number, Number> cpuSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> memorySeries = new XYChart.Series<>();
    private Timeline liveTimeline;
    private int liveX;
    private double cpu = 35;
    private double memory = 55;

    @FXML
    private void initialize() {
        initTemperatureChart();
        initSalesChart();
        initLiveChart();
    }

    private void initTemperatureChart() {
        temperatureChart.getData().add(temperatureSeries("Berlin",
                0.6, 1.8, 5.2, 10.0, 14.6, 17.9, 19.9, 19.4, 15.2, 10.2, 5.3, 1.9));
        temperatureChart.getData().add(temperatureSeries("Madrid",
                6.3, 7.9, 11.2, 12.9, 16.7, 22.4, 25.6, 25.1, 20.9, 15.1, 9.9, 6.9));
        temperatureChart.getData().add(temperatureSeries("Oslo",
                -4.3, -4.0, -0.2, 4.5, 10.8, 15.2, 16.4, 15.2, 10.8, 6.3, 0.7, -3.1));

        ChartSupport.bindCommonOptions(temperatureChart, temperatureTitle, temperatureAnimated, temperatureLegend,
                temperatureLegendSide);
        ChartSupport.bindAxisLabel(temperatureXAxis, temperatureXLabel);
        ChartSupport.bindAxisLabel(temperatureYAxis, temperatureYLabel);
        temperatureSymbols.setSelected(temperatureChart.getCreateSymbols());
        temperatureChart.createSymbolsProperty().bind(temperatureSymbols.selectedProperty());

        temperatureChart.setOnMouseClicked(e -> ChartSupport.findData(temperatureChart, e.getTarget())
                .ifPresent(hit -> temperatureOutput.setText(hit.series().getName() + ", " + hit.data().getXValue()
                        + ": " + ChartSupport.format(hit.data().getYValue()) + " °C")));
    }

    private static XYChart.Series<String, Number> temperatureSeries(String city, double... values) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(city);
        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(MONTHS[i], values[i]));
        }
        return series;
    }

    private void initSalesChart() {
        nextWeek = 9;
        onAddSeries();
        onAddSeries();
        salesXAxis.setTickUnit(1);
    }

    @FXML
    private void onAddSeries() {
        seriesCounter++;
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Product " + (char) ('A' + (seriesCounter - 1) % 26));
        int firstWeek = salesChart.getData().isEmpty() ? 1 : firstWeek();
        double value = 40 + random.nextInt(60);
        for (int week = firstWeek; week < nextWeek; week++) {
            value = nextValue(value);
            series.getData().add(new XYChart.Data<>(week, value));
        }
        salesChart.getData().add(series);
        salesOutput.setText("Added series \"" + series.getName() + "\".");
    }

    @FXML
    private void onRemoveSeries() {
        if (salesChart.getData().isEmpty()) {
            salesOutput.setText("There is no series left to remove.");
            return;
        }
        XYChart.Series<Number, Number> removed = salesChart.getData().removeLast();
        salesOutput.setText("Removed series \"" + removed.getName() + "\".");
    }

    @FXML
    private void onAddPoint() {
        for (XYChart.Series<Number, Number> series : salesChart.getData()) {
            double last = series.getData().isEmpty() ? 60 : series.getData().getLast().getYValue().doubleValue();
            series.getData().add(new XYChart.Data<>(nextWeek, nextValue(last)));
        }
        salesOutput.setText("Added week " + nextWeek + ".");
        nextWeek++;
    }

    @FXML
    private void onRemovePoint() {
        boolean removed = false;
        for (XYChart.Series<Number, Number> series : salesChart.getData()) {
            if (!series.getData().isEmpty()) {
                series.getData().removeFirst();
                removed = true;
            }
        }
        salesOutput.setText(removed ? "Removed the first data point of every series." : "No data points left.");
    }

    private int firstWeek() {
        int first = nextWeek;
        for (XYChart.Series<Number, Number> series : salesChart.getData()) {
            if (!series.getData().isEmpty()) {
                first = Math.min(first, series.getData().getFirst().getXValue().intValue());
            }
        }
        return first;
    }

    private double nextValue(double previous) {
        return Math.max(5, Math.round(previous + random.nextGaussian() * 12));
    }

    private void initLiveChart() {
        cpuSeries.setName("CPU");
        memorySeries.setName("Memory");
        for (int i = 0; i < LIVE_WINDOW; i++) {
            addLiveSample();
        }
        liveChart.getData().add(cpuSeries);
        liveChart.getData().add(memorySeries);

        liveTimeline = new Timeline(new KeyFrame(Duration.millis(250), e -> addLiveSample()));
        liveTimeline.setCycleCount(Animation.INDEFINITE);
        liveTimeline.rateProperty().bind(liveSpeed.valueProperty());
        liveRunning.selectedProperty().addListener((obs, wasRunning, running) -> {
            if (running) {
                liveTimeline.play();
            } else {
                liveTimeline.pause();
            }
        });
        liveTimeline.play();
    }

    private void addLiveSample() {
        liveX++;
        cpu = clamp(cpu + random.nextGaussian() * 8, 2, 98);
        memory = clamp(memory + random.nextGaussian() * 2, 20, 95);
        cpuSeries.getData().add(new XYChart.Data<>(liveX, cpu));
        memorySeries.getData().add(new XYChart.Data<>(liveX, memory));
        if (cpuSeries.getData().size() > LIVE_WINDOW) {
            cpuSeries.getData().removeFirst();
            memorySeries.getData().removeFirst();
        }
        liveXAxis.setLowerBound(Math.max(0, liveX - LIVE_WINDOW));
        liveXAxis.setUpperBound(Math.max(LIVE_WINDOW, liveX));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void dispose() {
        liveTimeline.stop();
    }
}
