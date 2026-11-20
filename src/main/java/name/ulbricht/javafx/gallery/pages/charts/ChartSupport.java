package name.ulbricht.javafx.gallery.pages.charts;

import javafx.event.EventTarget;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.Locale;
import java.util.Optional;

/**
 * Small helpers shared by the chart demo pages: binding the common chart options and finding the data item
 * that was clicked.
 */
final class ChartSupport {

    private ChartSupport() {
    }

    /** A data item together with the series it belongs to. */
    record Hit<X, Y>(XYChart.Series<X, Y> series, XYChart.Data<X, Y> data) {
    }

    /**
     * Binds title, animation and legend of a chart to the given option controls. The text field and the combo box
     * are initialized from the chart.
     */
    static void bindCommonOptions(Chart chart, TextField title, CheckBox animated, CheckBox legendVisible,
                                  ComboBox<Side> legendSide) {
        title.setText(chart.getTitle());
        chart.titleProperty().bind(title.textProperty());

        animated.setSelected(chart.getAnimated());
        chart.animatedProperty().bind(animated.selectedProperty());

        legendVisible.setSelected(chart.isLegendVisible());
        chart.legendVisibleProperty().bind(legendVisible.selectedProperty());

        legendSide.getItems().setAll(Side.values());
        legendSide.setValue(chart.getLegendSide());
        chart.legendSideProperty().bind(legendSide.valueProperty());
        legendSide.disableProperty().bind(legendVisible.selectedProperty().not());
    }

    /** Binds the label of an axis to a text field, which is initialized with the current label. */
    static void bindAxisLabel(Axis<?> axis, TextField field) {
        field.setText(axis.getLabel());
        axis.labelProperty().bind(field.textProperty());
    }

    /** {@code true} if {@code target} is {@code node} or one of its descendants. */
    static boolean isWithin(EventTarget target, Node node) {
        if (node == null || !(target instanceof Node start)) {
            return false;
        }
        for (Node n = start; n != null; n = n.getParent()) {
            if (n == node) {
                return true;
            }
        }
        return false;
    }

    /** Finds the data item whose node (symbol, bar, bubble …) contains the event target. */
    static <X, Y> Optional<Hit<X, Y>> findData(XYChart<X, Y> chart, EventTarget target) {
        for (XYChart.Series<X, Y> series : chart.getData()) {
            for (XYChart.Data<X, Y> data : series.getData()) {
                if (isWithin(target, data.getNode())) {
                    return Optional.of(new Hit<>(series, data));
                }
            }
        }
        return Optional.empty();
    }

    /** Formats a number with one decimal place. */
    static String format(Number value) {
        return String.format(Locale.ROOT, "%.1f", value.doubleValue());
    }
}
