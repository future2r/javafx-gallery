package name.ulbricht.javafx.gallery.pages.status;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

/**
 * Controller of the ProgressBar demo page: determinate, indeterminate and Task-driven progress.
 */
public class ProgressBarController implements Disposable {

    @FXML
    private ProgressBar determinateBar;
    @FXML
    private Label determinateOutput;
    @FXML
    private Slider progress;

    @FXML
    private ProgressBar indeterminateBar;
    @FXML
    private CheckBox indeterminate;

    @FXML
    private ProgressBar taskBar;
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
        determinateBar.progressProperty().bind(progress.valueProperty().divide(100));
        determinateOutput.textProperty().bind(Bindings.format("Progress: %.0f %%", progress.valueProperty()));

        indeterminateBar.progressProperty().bind(Bindings.when(indeterminate.selectedProperty())
                .then(ProgressBar.INDETERMINATE_PROGRESS).otherwise(0.65));
    }

    @FXML
    private void onStart() {
        cancelTask();
        task = new FakeWorkTask();
        taskBar.progressProperty().bind(task.progressProperty());
        taskMessage.textProperty().bind(task.messageProperty());
        startButton.disableProperty().bind(task.runningProperty());
        cancelButton.disableProperty().bind(task.runningProperty().not());
        task.setOnSucceeded(event -> taskOutput.setText("Task succeeded."));
        task.setOnCancelled(event -> taskOutput.setText("Task cancelled."));
        task.setOnFailed(event -> taskOutput.setText("Task failed: " + task.getException()));
        taskOutput.setText("Task running …");

        Thread thread = new Thread(task, "progress-bar-demo");
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
