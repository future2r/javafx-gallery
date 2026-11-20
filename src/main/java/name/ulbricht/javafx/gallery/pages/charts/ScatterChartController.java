package name.ulbricht.javafx.gallery.pages.charts;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Demo page for {@link ScatterChart}: two point clouds with layout options and a chart that adds points where the
 * user clicks.
 */
public class ScatterChartController {

    @FXML
    private ScatterChart<Number, Number> bodyChart;
    @FXML
    private NumberAxis bodyXAxis;
    @FXML
    private NumberAxis bodyYAxis;
    @FXML
    private Label bodyOutput;
    @FXML
    private TextField bodyTitle;
    @FXML
    private TextField bodyXLabel;
    @FXML
    private TextField bodyYLabel;
    @FXML
    private CheckBox bodyAnimated;
    @FXML
    private CheckBox bodyLegend;
    @FXML
    private ComboBox<Side> bodyLegendSide;
    @FXML
    private CheckBox verticalGridLines;
    @FXML
    private CheckBox horizontalGridLines;
    @FXML
    private CheckBox alternativeRows;

    @FXML
    private ScatterChart<Number, Number> drawChart;
    @FXML
    private NumberAxis drawXAxis;
    @FXML
    private NumberAxis drawYAxis;
    @FXML
    private Label drawOutput;
    @FXML
    private ComboBox<String> drawSeries;

    private final Random random = new Random(13);
    private final XYChart.Series<Number, Number> women = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> men = new XYChart.Series<>();

    @FXML
    private void initialize() {
        women.setName("Women");
        men.setName("Men");
        women.getData().setAll(generate(165, 7, 62, 9));
        men.getData().setAll(generate(179, 7, 80, 11));
        bodyChart.getData().add(women);
        bodyChart.getData().add(men);

        ChartSupport.bindCommonOptions(bodyChart, bodyTitle, bodyAnimated, bodyLegend, bodyLegendSide);
        ChartSupport.bindAxisLabel(bodyXAxis, bodyXLabel);
        ChartSupport.bindAxisLabel(bodyYAxis, bodyYLabel);
        bodyChart.verticalGridLinesVisibleProperty().bind(verticalGridLines.selectedProperty());
        bodyChart.horizontalGridLinesVisibleProperty().bind(horizontalGridLines.selectedProperty());
        bodyChart.alternativeRowFillVisibleProperty().bind(alternativeRows.selectedProperty());
        bodyChart.setOnMouseClicked(e -> ChartSupport.findData(bodyChart, e.getTarget())
                .ifPresent(hit -> bodyOutput.setText(hit.series().getName() + ": "
                        + ChartSupport.format(hit.data().getXValue()) + " cm, "
                        + ChartSupport.format(hit.data().getYValue()) + " kg")));

        for (String name : List.of("Series A", "Series B", "Series C")) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(name);
            drawChart.getData().add(series);
            drawSeries.getItems().add(name);
        }
        drawSeries.getSelectionModel().selectFirst();
        drawChart.setOnMouseClicked(this::onDrawChartClicked);
    }

    private List<XYChart.Data<Number, Number>> generate(double meanHeight, double sdHeight, double meanWeight,
                                                         double sdWeight) {
        List<XYChart.Data<Number, Number>> points = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            double z = random.nextGaussian();
            double height = meanHeight + z * sdHeight;
            // weight correlates with height
            double weight = meanWeight + (0.7 * z + 0.7 * random.nextGaussian()) * sdWeight;
            points.add(new XYChart.Data<>(Math.round(height * 10) / 10.0, Math.round(weight * 10) / 10.0));
        }
        return points;
    }

    @FXML
    private void onRegenerate() {
        women.getData().setAll(generate(165, 7, 62, 9));
        men.getData().setAll(generate(179, 7, 80, 11));
        bodyOutput.setText("New random samples generated.");
    }

    private void onDrawChartClicked(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        var hit = ChartSupport.findData(drawChart, event.getTarget());
        if (hit.isPresent()) {
            hit.get().series().getData().remove(hit.get().data());
            drawOutput.setText("Removed a point from " + hit.get().series().getName() + ".");
            return;
        }
        double x = drawXAxis.getValueForDisplay(
                drawXAxis.sceneToLocal(event.getSceneX(), event.getSceneY()).getX()).doubleValue();
        double y = drawYAxis.getValueForDisplay(
                drawYAxis.sceneToLocal(event.getSceneX(), event.getSceneY()).getY()).doubleValue();
        if (x < drawXAxis.getLowerBound() || x > drawXAxis.getUpperBound()
                || y < drawYAxis.getLowerBound() || y > drawYAxis.getUpperBound()) {
            return;
        }
        int index = Math.max(0, drawSeries.getSelectionModel().getSelectedIndex());
        XYChart.Series<Number, Number> series = drawChart.getData().get(index);
        series.getData().add(new XYChart.Data<>(x, y));
        drawOutput.setText(String.format(Locale.ROOT, "Added (%.1f, %.1f) to %s – %d points in total.",
                x, y, series.getName(), pointCount()));
    }

    private int pointCount() {
        return drawChart.getData().stream().mapToInt(series -> series.getData().size()).sum();
    }

    @FXML
    private void onClearPoints() {
        for (XYChart.Series<Number, Number> series : drawChart.getData()) {
            series.getData().clear();
        }
        drawOutput.setText("All points removed.");
    }
}
