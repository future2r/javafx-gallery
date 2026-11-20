package name.ulbricht.javafx.gallery.pages.charts;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Demo page for {@link BarChart}: grouped vertical bars, a horizontal bar chart and live updated values.
 */
public class BarChartController implements Disposable {

    private static final String[] QUARTERS = {"Q1", "Q2", "Q3", "Q4"};
    private static final String ORDER_AS_LISTED = "As listed";
    private static final String ORDER_ASCENDING = "Ascending";
    private static final String ORDER_DESCENDING = "Descending";

    private record Language(String name, double share) {
    }

    private static final List<Language> LANGUAGES = List.of(
            new Language("JavaScript", 62.3), new Language("Python", 51.0), new Language("SQL", 51.5),
            new Language("TypeScript", 38.5), new Language("Java", 30.3), new Language("C#", 27.1),
            new Language("C++", 23.0), new Language("Go", 13.5), new Language("Rust", 13.1),
            new Language("Kotlin", 9.4));

    @FXML
    private BarChart<String, Number> revenueChart;
    @FXML
    private NumberAxis revenueYAxis;
    @FXML
    private Label revenueOutput;
    @FXML
    private TextField revenueTitle;
    @FXML
    private TextField revenueYLabel;
    @FXML
    private CheckBox revenueAnimated;
    @FXML
    private CheckBox revenueLegend;
    @FXML
    private ComboBox<Side> revenueLegendSide;
    @FXML
    private Slider barGap;
    @FXML
    private Slider categoryGap;

    @FXML
    private BarChart<Number, String> languageChart;
    @FXML
    private ComboBox<String> languageOrder;

    @FXML
    private BarChart<String, Number> loadChart;
    @FXML
    private CheckBox loadRunning;

    private final Random random = new Random(11);
    private Timeline loadTimeline;

    @FXML
    private void initialize() {
        initRevenueChart();
        initLanguageChart();
        initLoadChart();
    }

    private void initRevenueChart() {
        revenueChart.getData().add(quarterSeries("Europe", 32.4, 35.1, 31.8, 40.2));
        revenueChart.getData().add(quarterSeries("North America", 41.0, 43.7, 45.2, 52.9));
        revenueChart.getData().add(quarterSeries("Asia", 25.3, 29.8, 34.6, 38.1));

        ChartSupport.bindCommonOptions(revenueChart, revenueTitle, revenueAnimated, revenueLegend,
                revenueLegendSide);
        ChartSupport.bindAxisLabel(revenueYAxis, revenueYLabel);
        barGap.setValue(revenueChart.getBarGap());
        revenueChart.barGapProperty().bind(barGap.valueProperty());
        categoryGap.setValue(revenueChart.getCategoryGap());
        revenueChart.categoryGapProperty().bind(categoryGap.valueProperty());

        revenueChart.setOnMouseClicked(e -> ChartSupport.findData(revenueChart, e.getTarget())
                .ifPresent(hit -> revenueOutput.setText(hit.series().getName() + ", " + hit.data().getXValue()
                        + ": " + ChartSupport.format(hit.data().getYValue()) + " million €")));
    }

    private static XYChart.Series<String, Number> quarterSeries(String name, double... values) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(QUARTERS[i], values[i]));
        }
        return series;
    }

    @FXML
    private void onRandomizeRevenue() {
        for (XYChart.Series<String, Number> series : revenueChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.setYValue(Math.round((20 + random.nextDouble() * 40) * 10) / 10.0);
            }
        }
        revenueOutput.setText("Values randomized.");
    }

    private void initLanguageChart() {
        languageOrder.getItems().setAll(ORDER_AS_LISTED, ORDER_ASCENDING, ORDER_DESCENDING);
        languageOrder.valueProperty().addListener((obs, oldValue, newValue) -> updateLanguageChart());
        languageOrder.setValue(ORDER_ASCENDING);
    }

    private void updateLanguageChart() {
        List<Language> languages = new ArrayList<>(LANGUAGES);
        if (ORDER_ASCENDING.equals(languageOrder.getValue())) {
            languages.sort(Comparator.comparingDouble(Language::share));
        } else if (ORDER_DESCENDING.equals(languageOrder.getValue())) {
            languages.sort(Comparator.comparingDouble(Language::share).reversed());
        }
        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("Share");
        for (Language language : languages) {
            series.getData().add(new XYChart.Data<>(language.share(), language.name()));
        }
        languageChart.getData().setAll(List.of(series));
    }

    private void initLoadChart() {
        XYChart.Series<String, Number> cpuSeries = new XYChart.Series<>();
        cpuSeries.setName("CPU");
        XYChart.Series<String, Number> ioSeries = new XYChart.Series<>();
        ioSeries.setName("I/O");
        for (String server : List.of("web-01", "web-02", "db-01", "cache-01", "batch-01")) {
            cpuSeries.getData().add(new XYChart.Data<>(server, 20 + random.nextInt(60)));
            ioSeries.getData().add(new XYChart.Data<>(server, 10 + random.nextInt(50)));
        }
        loadChart.getData().add(cpuSeries);
        loadChart.getData().add(ioSeries);

        loadTimeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> updateLoad()));
        loadTimeline.setCycleCount(Animation.INDEFINITE);
        loadRunning.selectedProperty().addListener((obs, wasRunning, running) -> {
            if (running) {
                loadTimeline.play();
            } else {
                loadTimeline.pause();
            }
        });
        loadTimeline.play();
    }

    private void updateLoad() {
        for (XYChart.Series<String, Number> series : loadChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                double value = data.getYValue().doubleValue() + random.nextGaussian() * 15;
                data.setYValue(Math.max(2, Math.min(98, value)));
            }
        }
    }

    @Override
    public void dispose() {
        loadTimeline.stop();
    }
}
