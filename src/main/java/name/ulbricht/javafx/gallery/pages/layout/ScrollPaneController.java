package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Controller of the ScrollPane demo page: scroll bar policies, panning, fitting and zoomed content.
 */
public class ScrollPaneController {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane scrollContent;
    @FXML
    private Label scrollOutput;
    @FXML
    private ComboBox<ScrollPane.ScrollBarPolicy> hbarPolicy;
    @FXML
    private ComboBox<ScrollPane.ScrollBarPolicy> vbarPolicy;
    @FXML
    private CheckBox pannable;
    @FXML
    private CheckBox fitToWidth;
    @FXML
    private CheckBox fitToHeight;

    @FXML
    private ScrollPane zoomPane;
    @FXML
    private Label zoomLabel;
    @FXML
    private Slider zoom;

    @FXML
    private void initialize() {
        for (int i = 0; i < 120; i++) {
            Color color = Color.hsb((i * 3) % 360, 0.55, 0.85);
            Label tile = new Label(String.valueOf(i + 1));
            tile.setPrefSize(64, 48);
            tile.setStyle("-fx-alignment: center; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;"
                    + " -fx-background-color: " + toWeb(color) + ";");
            scrollContent.getChildren().add(tile);
        }
        hbarPolicy.getItems().setAll(ScrollPane.ScrollBarPolicy.values());
        hbarPolicy.setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.hbarPolicyProperty().bind(hbarPolicy.valueProperty());
        vbarPolicy.getItems().setAll(ScrollPane.ScrollBarPolicy.values());
        vbarPolicy.setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.vbarPolicyProperty().bind(vbarPolicy.valueProperty());
        scrollPane.pannableProperty().bind(pannable.selectedProperty());
        scrollPane.fitToWidthProperty().bind(fitToWidth.selectedProperty());
        scrollPane.fitToHeightProperty().bind(fitToHeight.selectedProperty());
        scrollOutput.textProperty().bind(Bindings.format("hvalue: %.2f   vvalue: %.2f",
                scrollPane.hvalueProperty(), scrollPane.vvalueProperty()));

        Group content = new Group(createDrawing());
        content.scaleXProperty().bind(zoom.valueProperty());
        content.scaleYProperty().bind(zoom.valueProperty());
        zoomPane.setContent(new Group(content));
        zoomLabel.textProperty().bind(Bindings.format("Zoom: %.0f %%", zoom.valueProperty().multiply(100)));
    }

    private static String toWeb(Color color) {
        return String.format("#%02x%02x%02x", Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255));
    }

    private static StackPane createDrawing() {
        Rectangle background = new Rectangle(900, 700);
        background.setFill(Color.web("#1f3b5a"));
        Group shapes = new Group(background);
        for (int i = 0; i < 40; i++) {
            Circle circle = new Circle(40 + (i * 37) % 840, 40 + (i * 53) % 620, 10 + (i * 7) % 40);
            circle.setFill(Color.hsb((i * 29) % 360, 0.6, 0.95, 0.75));
            shapes.getChildren().add(circle);
        }
        Text text = new Text("Drag to pan · use the slider to zoom");
        text.setFont(Font.font("System", FontWeight.BOLD, 28));
        text.setFill(Color.WHITE);
        text.setTextOrigin(VPos.TOP);
        text.setX(200);
        text.setY(330);
        shapes.getChildren().add(text);
        return new StackPane(shapes);
    }

    @FXML
    private void onScrollTopLeft() {
        zoomPane.setHvalue(zoomPane.getHmin());
        zoomPane.setVvalue(zoomPane.getVmin());
    }

    @FXML
    private void onScrollCenter() {
        zoomPane.setHvalue((zoomPane.getHmin() + zoomPane.getHmax()) / 2);
        zoomPane.setVvalue((zoomPane.getVmin() + zoomPane.getVmax()) / 2);
    }

    @FXML
    private void onScrollBottomRight() {
        zoomPane.setHvalue(zoomPane.getHmax());
        zoomPane.setVvalue(zoomPane.getVmax());
    }
}
