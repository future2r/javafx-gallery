package name.ulbricht.javafx.gallery.pages.charts;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Demo page for {@link BubbleChart}: country statistics with the population as bubble size and randomly moving
 * bubbles driven by a {@link Timeline}.
 */
public class BubbleChartController implements Disposable {

    private record Country(String name, double gdp, double lifeExpectancy, double populationMillions) {
    }

    @FXML
    private BubbleChart<Number, Number> countryChart;
    @FXML
    private Label countryOutput;
    @FXML
    private TextField countryTitle;
    @FXML
    private CheckBox countryAnimated;
    @FXML
    private CheckBox countryLegend;
    @FXML
    private ComboBox<Side> countryLegendSide;
    @FXML
    private Slider bubbleScale;

    @FXML
    private BubbleChart<Number, Number> liveChart;
    @FXML
    private CheckBox liveRunning;

    private final Map<XYChart.Data<Number, Number>, Country> countries = new HashMap<>();
    private final Random random = new Random(17);
    private Timeline liveTimeline;

    @FXML
    private void initialize() {
        initCountryChart();
        initLiveChart();
    }

    private void initCountryChart() {
        addRegion("Europe", List.of(
                new Country("Germany", 52.7, 81.0, 84),
                new Country("France", 44.4, 82.3, 68),
                new Country("Italy", 38.4, 83.1, 59),
                new Country("Spain", 32.7, 83.3, 48),
                new Country("Poland", 22.1, 77.6, 37)));
        addRegion("Asia", List.of(
                new Country("China", 12.6, 78.6, 1410),
                new Country("India", 2.5, 70.4, 1430),
                new Country("Japan", 33.8, 84.5, 125),
                new Country("Indonesia", 4.9, 68.2, 278),
                new Country("South Korea", 33.1, 83.5, 52)));
        addRegion("Americas", List.of(
                new Country("United States", 80.4, 77.5, 335),
                new Country("Brazil", 10.0, 73.4, 216),
                new Country("Mexico", 13.8, 74.8, 128),
                new Country("Canada", 53.4, 81.7, 40),
                new Country("Argentina", 13.7, 76.1, 46)));
        addRegion("Africa", List.of(
                new Country("Nigeria", 1.6, 54.5, 224),
                new Country("Egypt", 3.5, 70.2, 113),
                new Country("South Africa", 6.0, 64.9, 60),
                new Country("Ethiopia", 1.3, 66.6, 127),
                new Country("Kenya", 2.0, 62.7, 55)));
        addRegion("Oceania", List.of(
                new Country("Australia", 64.7, 83.1, 27),
                new Country("New Zealand", 48.5, 82.1, 5)));

        ChartSupport.bindCommonOptions(countryChart, countryTitle, countryAnimated, countryLegend,
                countryLegendSide);
        bubbleScale.valueProperty().addListener((obs, oldValue, newValue) -> updateBubbleSizes());

        countryChart.setOnMouseClicked(e -> ChartSupport.findData(countryChart, e.getTarget()).ifPresent(hit -> {
            Country country = countries.get(hit.data());
            if (country != null) {
                countryOutput.setText(String.format(Locale.ROOT,
                        "%s (%s): GDP per capita %.1fk US$, life expectancy %.1f years, population %.0f million",
                        country.name(), hit.series().getName(), country.gdp(), country.lifeExpectancy(),
                        country.populationMillions()));
            }
        }));
    }

    private void addRegion(String region, List<Country> regionCountries) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(region);
        for (Country country : regionCountries) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(country.gdp(), country.lifeExpectancy(),
                    radius(country));
            countries.put(data, country);
            series.getData().add(data);
        }
        countryChart.getData().add(series);
    }

    private double radius(Country country) {
        return Math.sqrt(country.populationMillions()) * bubbleScale.getValue();
    }

    private void updateBubbleSizes() {
        countries.forEach((data, country) -> data.setExtraValue(radius(country)));
    }

    private void initLiveChart() {
        for (String name : List.of("Red team", "Green team", "Blue team")) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            for (int i = 0; i < 6; i++) {
                series.getData().add(new XYChart.Data<>(10 + random.nextInt(80), 10 + random.nextInt(80),
                        2 + random.nextInt(5)));
            }
            liveChart.getData().add(series);
        }

        liveTimeline = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> moveBubbles()));
        liveTimeline.setCycleCount(Animation.INDEFINITE);
        liveRunning.selectedProperty().addListener((obs, wasRunning, running) -> {
            if (running) {
                liveTimeline.play();
            } else {
                liveTimeline.pause();
            }
        });
        liveTimeline.play();
    }

    private void moveBubbles() {
        for (XYChart.Series<Number, Number> series : liveChart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                data.setXValue(clamp(data.getXValue().doubleValue() + random.nextGaussian() * 8, 5, 95));
                data.setYValue(clamp(data.getYValue().doubleValue() + random.nextGaussian() * 8, 5, 95));
                double extra = ((Number) data.getExtraValue()).doubleValue();
                data.setExtraValue(clamp(extra + random.nextGaussian(), 1.5, 8));
            }
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void dispose() {
        liveTimeline.stop();
    }
}
