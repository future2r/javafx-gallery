package name.ulbricht.javafx.gallery.pages.dialogs;

import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controller of the ChoiceDialog demo page: choosing a string and choosing an enum constant.
 */
public class ChoiceDialogController {

    private static final Map<String, String> COLORS = new LinkedHashMap<>();

    static {
        COLORS.put("Blue", "#3d8fd1");
        COLORS.put("Orange", "#e5734b");
        COLORS.put("Green", "#56b16a");
        COLORS.put("Purple", "#b06ad1");
        COLORS.put("Gray", "#8a8a8a");
    }

    @FXML
    private Region colorSwatch;
    @FXML
    private Label colorOutput;
    @FXML
    private ComboBox<String> defaultChoice;
    @FXML
    private Label dayOutput;

    private DayOfWeek meetingDay = DayOfWeek.WEDNESDAY;

    @FXML
    private void initialize() {
        defaultChoice.getItems().setAll(COLORS.keySet());
        defaultChoice.setValue("Green");
        colorSwatch.getStyleClass().add("demo-area");
        dayOutput.setText("Meeting day: " + displayName(meetingDay));
    }

    @FXML
    private void onChooseColor(ActionEvent event) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(defaultChoice.getValue(), List.copyOf(COLORS.keySet()));
        prepare(dialog, event);
        dialog.setTitle("Color");
        dialog.setHeaderText("Pick your favorite color.");
        dialog.setContentText("Color:");

        dialog.showAndWait().ifPresentOrElse(color -> {
            colorSwatch.setStyle("-fx-background-color: " + COLORS.get(color) + "; -fx-background-radius: 4;");
            colorOutput.setText("You chose: " + color);
        }, () -> colorOutput.setText("Cancelled."));
    }

    @FXML
    private void onChooseDay(ActionEvent event) {
        ChoiceDialog<DayOfWeek> dialog = new ChoiceDialog<>(meetingDay, DayOfWeek.values());
        prepare(dialog, event);
        dialog.setTitle("Meeting");
        dialog.setHeaderText("On which day should the weekly meeting take place?");
        dialog.setContentText("Day:");

        dialog.showAndWait().ifPresentOrElse(day -> {
            meetingDay = day;
            dayOutput.setText("Meeting day: " + displayName(day));
        }, () -> dayOutput.setText("Cancelled – meeting day stays " + displayName(meetingDay) + "."));
    }

    private static String displayName(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    private static void prepare(ChoiceDialog<?> dialog, ActionEvent event) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();
        dialog.initOwner(owner);
        ThemeManager.get().style(dialog);
    }
}
