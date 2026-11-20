package name.ulbricht.javafx.gallery.pages.layout;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Controller of the StackPane demo page: z-order, alignment and overlays.
 */
public class StackPaneController {

    @FXML
    private StackPane stack;
    @FXML
    private Label stackOutput;
    @FXML
    private ComboBox<Pos> alignment;

    @FXML
    private Rectangle picture;
    @FXML
    private Label badge;
    @FXML
    private StackPane busyOverlay;
    @FXML
    private CheckBox showBadge;
    @FXML
    private CheckBox showBusy;
    @FXML
    private ComboBox<Pos> badgeAlignment;

    @FXML
    private void initialize() {
        alignment.getItems().setAll(Pos.values());
        alignment.setValue(Pos.CENTER);
        stack.alignmentProperty().bind(alignment.valueProperty());

        picture.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#3d8fd1")), new Stop(0.6, Color.web("#7a5fc7")), new Stop(1, Color.web("#e5734b"))));
        StackPane.setMargin(badge, new Insets(10));
        badge.visibleProperty().bind(showBadge.selectedProperty());
        busyOverlay.visibleProperty().bind(showBusy.selectedProperty());
        badgeAlignment.getItems().setAll(Pos.values());
        badgeAlignment.setValue(Pos.TOP_RIGHT);
        badgeAlignment.valueProperty().addListener((obs, oldPos, pos) -> StackPane.setAlignment(badge, pos));
    }

    @FXML
    private void onBoxClicked(MouseEvent event) {
        Node box = (Node) event.getSource();
        box.toFront();
        stackOutput.setText("\"" + ((Label) box).getText() + "\" is now at the front.");
    }

    @FXML
    private void onSendToBack() {
        Node front = stack.getChildren().getLast();
        front.toBack();
        stackOutput.setText("\"" + ((Label) front).getText() + "\" was sent to the back.");
    }
}
