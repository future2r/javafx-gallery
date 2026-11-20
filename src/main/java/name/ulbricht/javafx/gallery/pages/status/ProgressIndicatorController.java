package name.ulbricht.javafx.gallery.pages.status;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;

import java.util.List;

/**
 * Controller of the ProgressIndicator demo page: determinate, indeterminate and Task-driven progress.
 */
public class ProgressIndicatorController implements Disposable {

    @FXML
    private ProgressIndicator determinate;
    @FXML
    private Label determinateOutput;
    @FXML
    private Slider progress;
    @FXML
    private Label sizeLabel;
    @FXML
    private Slider size;

    @FXML
    private ProgressIndicator small;
    @FXML
    private ProgressIndicator medium;
    @FXML
    private ProgressIndicator large;
    @FXML
    private CheckBox active;

    @FXML
    private ProgressIndicator taskIndicator;
    @FXML
    private Label taskMessage;
    @FXML
    private Button startButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label taskOutput;

    private Task<Void> task;

    @FXML
    private void initialize() {
        determinate.progressProperty().bind(progress.valueProperty().divide(100));
        determinateOutput.textProperty().bind(Bindings.format("Progress: %.0f %%", progress.valueProperty()));
        determinate.prefWidthProperty().bind(size.valueProperty());
        determinate.prefHeightProperty().bind(size.valueProperty());
        sizeLabel.textProperty().bind(Bindings.format("Size: %.0f px", size.valueProperty()));

        DoubleBinding indeterminateProgress = Bindings.when(active.selectedProperty())
                .then(ProgressIndicator.INDETERMINATE_PROGRESS).otherwise(0.0);
        for (ProgressIndicator indicator : List.of(small, medium, large)) {
            indicator.progressProperty().bind(indeterminateProgress);
        }
    }

    @FXML
    private void onStart() {
        cancelTask();
        task = new FakeWorkTask();
        taskIndicator.progressProperty().bind(task.progressProperty());
        taskMessage.textProperty().bind(task.messageProperty());
        startButton.disableProperty().bind(task.runningProperty());
        cancelButton.disableProperty().bind(task.runningProperty().not());
        task.setOnSucceeded(event -> taskOutput.setText("Task succeeded."));
        task.setOnCancelled(event -> taskOutput.setText("Task cancelled."));
        task.setOnFailed(event -> taskOutput.setText("Task failed: " + task.getException()));
        taskOutput.setText("Task running …");

        Thread thread = new Thread(task, "progress-indicator-demo");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onCancel() {
        cancelTask();
    }

    private void cancelTask() {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
    }

    @Override
    public void dispose() {
        cancelTask();
    }
}
