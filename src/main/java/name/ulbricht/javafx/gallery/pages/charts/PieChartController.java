package name.ulbricht.javafx.gallery.pages.charts;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Demo page for {@link PieChart}: layout options, clicking slices and live changes of the slice values.
 */
public class PieChartController {

    private static final List<String> EXTRA_SLICES =
            List.of("Code review", "Support", "Training", "Research", "Operations", "Planning");

    @FXML
    private PieChart browserChart;
    @FXML
    private Label browserOutput;
    @FXML
    private TextField browserTitle;
    @FXML
    private CheckBox browserAnimated;
    @FXML
    private CheckBox browserLegend;
    @FXML
    private ComboBox<Side> browserLegendSide;
    @FXML
    private CheckBox labelsVisible;
    @FXML
    private CheckBox clockwise;
    @FXML
    private Slider startAngle;
    @FXML
    private Slider labelLineLength;

    @FXML
    private PieChart budgetChart;
    @FXML
    private Label budgetOutput;
    @FXML
    private ComboBox<PieChart.Data> sliceSelector;
    @FXML
    private Slider sliceValue;

    private final Random random = new Random(5);
    private PieChart.Data boundSlice;
    private int extraSliceIndex;

    @FXML
    private void initialize() {
        initBrowserChart();
        initBudgetChart();
    }

    private void initBrowserChart() {
        browserChart.getData().addAll(
                new PieChart.Data("Chrome", 65.4),
                new PieChart.Data("Safari", 18.4),
                new PieChart.Data("Edge", 5.2),
                new PieChart.Data("Firefox", 2.8),
                new PieChart.Data("Samsung Internet", 2.6),
                new PieChart.Data("Opera", 2.2),
                new PieChart.Data("Other", 3.4));

        ChartSupport.bindCommonOptions(browserChart, browserTitle, browserAnimated, browserLegend,
                browserLegendSide);
        browserChart.labelsVisibleProperty().bind(labelsVisible.selectedProperty());
        browserChart.clockwiseProperty().bind(clockwise.selectedProperty());
        startAngle.setValue(browserChart.getStartAngle());
        browserChart.startAngleProperty().bind(startAngle.valueProperty());
        labelLineLength.setValue(browserChart.getLabelLineLength());
        browserChart.labelLineLengthProperty().bind(labelLineLength.valueProperty());

        browserChart.setOnMouseClicked(this::onBrowserChartClicked);
    }

    private void onBrowserChartClicked(MouseEvent event) {
        double total = browserChart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum();
        for (PieChart.Data slice : browserChart.getData()) {
            if (ChartSupport.isWithin(event.getTarget(), slice.getNode())) {
                browserOutput.setText(String.format(Locale.ROOT, "%s: %.1f %% (%.1f %% of the shown total)",
                        slice.getName(), slice.getPieValue(), slice.getPieValue() / total * 100));
                return;
            }
        }
    }

    private void initBudgetChart() {
        budgetChart.getData().addAll(
                new PieChart.Data("Development", 45),
                new PieChart.Data("Testing", 20),
                new PieChart.Data("Meetings", 15),
                new PieChart.Data("Documentation", 10));

        sliceSelector.setItems(budgetChart.getData());
        sliceSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(PieChart.Data slice) {
                return slice == null ? "" : slice.getName();
            }

            @Override
            public PieChart.Data fromString(String string) {
                return null;
            }
        });
        sliceSelector.valueProperty().addListener((obs, oldSlice, newSlice) -> bindSlice(newSlice));
        sliceSelector.setValue(budgetChart.getData().getFirst());
    }

    private void bindSlice(PieChart.Data slice) {
        if (boundSlice != null) {
            sliceValue.valueProperty().unbindBidirectional(boundSlice.pieValueProperty());
        }
        boundSlice = slice;
        sliceValue.setDisable(slice == null);
        if (slice != null) {
            sliceValue.setValue(slice.getPieValue());
            sliceValue.valueProperty().bindBidirectional(slice.pieValueProperty());
        }
    }

    @FXML
    private void onAddSlice() {
        String name = EXTRA_SLICES.get(extraSliceIndex % EXTRA_SLICES.size());
        if (extraSliceIndex >= EXTRA_SLICES.size()) {
            name += " " + (extraSliceIndex / EXTRA_SLICES.size() + 1);
        }
        extraSliceIndex++;
        PieChart.Data slice = new PieChart.Data(name, 5 + random.nextInt(20));
        budgetChart.getData().add(slice);
        sliceSelector.setValue(slice);
        budgetOutput.setText("Added slice \"" + name + "\".");
    }

    @FXML
    private void onRemoveSlice() {
        PieChart.Data slice = sliceSelector.getValue();
        if (slice == null) {
            budgetOutput.setText("No slice selected.");
            return;
        }
        bindSlice(null);
        sliceSelector.setValue(null);
        budgetChart.getData().remove(slice);
        if (!budgetChart.getData().isEmpty()) {
            sliceSelector.setValue(budgetChart.getData().getFirst());
        }
        budgetOutput.setText("Removed slice \"" + slice.getName() + "\".");
    }
}
