package name.ulbricht.javafx.gallery.pages.charts;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Random;

/**
 * Demo page for {@link StackedBarChart}: stacked energy mix data and live adding/removing of series.
 */
public class StackedBarChartController {

    private static final List<String> YEARS = List.of("2019", "2020", "2021", "2022", "2023", "2024");
    private static final List<String> WEEKDAYS = List.of("Mon", "Tue", "Wed", "Thu", "Fri");
    private static final List<String> TEAMS =
            List.of("Team Alpha", "Team Beta", "Team Gamma", "Team Delta", "Team Epsilon", "Team Zeta");

    @FXML
    private StackedBarChart<String, Number> energyChart;
    @FXML
    private CategoryAxis energyXAxis;
    @FXML
    private Label energyOutput;
    @FXML
    private TextField energyTitle;
    @FXML
    private CheckBox energyAnimated;
    @FXML
    private CheckBox energyLegend;
    @FXML
    private ComboBox<Side> energyLegendSide;
    @FXML
    private Slider energyCategoryGap;

    @FXML
    private StackedBarChart<String, Number> tasksChart;
    @FXML
    private CategoryAxis tasksXAxis;
    @FXML
    private Label tasksOutput;

    private final Random random = new Random(3);

    @FXML
    private void initialize() {
        energyXAxis.setCategories(FXCollections.observableArrayList(YEARS));
        energyChart.getData().add(yearSeries("Coal", 171, 134, 165, 179, 131, 105));
        energyChart.getData().add(yearSeries("Natural gas", 90, 95, 89, 79, 76, 72));
        energyChart.getData().add(yearSeries("Nuclear", 75, 64, 69, 35, 7, 0));
        energyChart.getData().add(yearSeries("Wind", 126, 132, 114, 125, 142, 136));
        energyChart.getData().add(yearSeries("Solar", 46, 50, 49, 61, 62, 72));

        ChartSupport.bindCommonOptions(energyChart, energyTitle, energyAnimated, energyLegend, energyLegendSide);
        energyCategoryGap.setValue(energyChart.getCategoryGap());
        energyChart.categoryGapProperty().bind(energyCategoryGap.valueProperty());
        energyChart.setOnMouseClicked(e -> ChartSupport.findData(energyChart, e.getTarget())
                .ifPresent(hit -> energyOutput.setText(hit.series().getName() + ", " + hit.data().getXValue()
                        + ": " + hit.data().getYValue() + " TWh")));

        tasksXAxis.setCategories(FXCollections.observableArrayList(WEEKDAYS));
        onAddSeries();
        onAddSeries();
        onAddSeries();
        tasksChart.setOnMouseClicked(e -> ChartSupport.findData(tasksChart, e.getTarget())
                .ifPresent(hit -> tasksOutput.setText(hit.series().getName() + ", " + hit.data().getXValue()
                        + ": " + hit.data().getYValue() + " tasks")));
    }

    private static XYChart.Series<String, Number> yearSeries(String name, int... values) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(YEARS.get(i), values[i]));
        }
        return series;
    }

    @FXML
    private void onAddSeries() {
        int count = tasksChart.getData().size();
        if (count >= TEAMS.size()) {
            tasksOutput.setText("All " + TEAMS.size() + " teams are shown.");
            return;
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(TEAMS.get(count));
        for (String day : WEEKDAYS) {
            series.getData().add(new XYChart.Data<>(day, 2 + random.nextInt(10)));
        }
        tasksChart.getData().add(series);
        tasksOutput.setText("Added \"" + series.getName() + "\".");
    }

    @FXML
    private void onRemoveSeries() {
        if (tasksChart.getData().isEmpty()) {
            tasksOutput.setText("There is no series left to remove.");
            return;
        }
        XYChart.Series<String, Number> removed = tasksChart.getData().removeLast();
        tasksOutput.setText("Removed \"" + removed.getName() + "\".");
    }

    @FXML
    private void onRandomize() {
        for (XYChart.Series<String, Number> series : tasksChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.setYValue(2 + random.nextInt(10));
            }
        }
        tasksOutput.setText("Values randomized.");
    }
}
