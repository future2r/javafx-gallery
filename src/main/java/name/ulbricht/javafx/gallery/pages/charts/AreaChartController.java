package name.ulbricht.javafx.gallery.pages.charts;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Demo page for {@link AreaChart} and {@link StackedAreaChart}.
 */
public class AreaChartController {

    @FXML
    private AreaChart<Number, Number> visitorsChart;
    @FXML
    private Label visitorsOutput;
    @FXML
    private TextField visitorsTitle;
    @FXML
    private CheckBox visitorsAnimated;
    @FXML
    private CheckBox visitorsSymbols;
    @FXML
    private CheckBox visitorsLegend;
    @FXML
    private ComboBox<Side> visitorsLegendSide;

    @FXML
    private StackedAreaChart<Number, Number> stackedChart;
    @FXML
    private CheckBox showIndustry;
    @FXML
    private CheckBox showHouseholds;
    @FXML
    private CheckBox showTransport;
    @FXML
    private CheckBox showServices;
    @FXML
    private CheckBox stackedSymbols;

    private final Random random = new Random(7);
    private final List<SeriesToggle> stackedSeries = new ArrayList<>();

    private record SeriesToggle(CheckBox toggle, XYChart.Series<Number, Number> series) {
    }

    @FXML
    private void initialize() {
        visitorsChart.getData().add(monthlySeries("Desktop", 42, 45, 48, 44, 40, 36, 33, 35, 43, 47, 50, 53));
        visitorsChart.getData().add(monthlySeries("Mobile", 30, 34, 39, 45, 52, 58, 63, 61, 55, 49, 44, 41));
        visitorsChart.getData().add(monthlySeries("Tablet", 8, 9, 10, 12, 13, 15, 17, 16, 13, 11, 10, 12));

        ChartSupport.bindCommonOptions(visitorsChart, visitorsTitle, visitorsAnimated, visitorsLegend,
                visitorsLegendSide);
        visitorsSymbols.setSelected(visitorsChart.getCreateSymbols());
        visitorsChart.createSymbolsProperty().bind(visitorsSymbols.selectedProperty());
        visitorsChart.setOnMouseClicked(e -> ChartSupport.findData(visitorsChart, e.getTarget())
                .ifPresent(hit -> visitorsOutput.setText(hit.series().getName() + ", month "
                        + hit.data().getXValue() + ": " + hit.data().getYValue() + "k visitors")));

        stackedSeries.add(new SeriesToggle(showIndustry,
                yearlySeries("Industry", 225, 228, 226, 221, 205, 214, 208, 190, 182, 180)));
        stackedSeries.add(new SeriesToggle(showHouseholds,
                yearlySeries("Households", 128, 130, 127, 126, 129, 135, 131, 121, 118, 119)));
        stackedSeries.add(new SeriesToggle(showTransport,
                yearlySeries("Transport", 12, 12, 13, 13, 14, 14, 16, 18, 21, 24)));
        stackedSeries.add(new SeriesToggle(showServices,
                yearlySeries("Services", 145, 146, 144, 143, 140, 133, 137, 134, 130, 128)));
        for (SeriesToggle entry : stackedSeries) {
            entry.toggle().selectedProperty().addListener((obs, oldValue, newValue) -> updateStackedSeries());
        }
        updateStackedSeries();
        stackedSymbols.setSelected(false);
        stackedChart.createSymbolsProperty().bind(stackedSymbols.selectedProperty());
    }

    /** Adds or removes series so that the chart shows exactly the selected ones, in their original order. */
    private void updateStackedSeries() {
        int index = 0;
        for (SeriesToggle entry : stackedSeries) {
            boolean shown = stackedChart.getData().contains(entry.series());
            if (entry.toggle().isSelected()) {
                if (!shown) {
                    stackedChart.getData().add(index, entry.series());
                }
                index++;
            } else if (shown) {
                stackedChart.getData().remove(entry.series());
            }
        }
    }

    @FXML
    private void onRandomize() {
        for (XYChart.Series<Number, Number> series : visitorsChart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                double factor = 0.6 + random.nextDouble() * 0.8;
                data.setYValue(Math.round(data.getYValue().doubleValue() * factor));
            }
        }
        visitorsOutput.setText("Data randomized.");
    }

    private static XYChart.Series<Number, Number> monthlySeries(String name, double... values) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(i + 1, values[i]));
        }
        return series;
    }

    private static XYChart.Series<Number, Number> yearlySeries(String name, double... values) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(2015 + i, values[i]));
        }
        return series;
    }
}
