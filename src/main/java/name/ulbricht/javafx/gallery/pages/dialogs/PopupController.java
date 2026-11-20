package name.ulbricht.javafx.gallery.pages.dialogs;

import name.ulbricht.javafx.gallery.shell.Disposable;
import name.ulbricht.javafx.gallery.theme.ThemeManager;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;

/**
 * Controller of the Popup demo page: a flyout card with auto-hide options and a teaching tip that can be placed on
 * any side of its target.
 */
public class PopupController implements Disposable {

    private static final double GAP = 6;

    @FXML
    private Button flyoutButton;
    @FXML
    private Label flyoutOutput;
    @FXML
    private CheckBox autoHide;
    @FXML
    private CheckBox hideOnEscape;
    @FXML
    private CheckBox consumeAutoHiding;

    @FXML
    private Button tipTarget;
    @FXML
    private Label tipOutput;
    @FXML
    private ComboBox<Side> placement;

    private Popup flyout;
    private VBox flyoutCard;
    private Popup tip;
    private VBox tipCard;

    @FXML
    private void initialize() {
        placement.getItems().setAll(Side.values());
        placement.setValue(Side.RIGHT);
    }

    // --- Example 1 ---------------------------------------------------------------------------------------------

    @FXML
    private void onShowFlyout() {
        if (flyout == null) {
            Label title = new Label("All items will be removed. Do you want to continue?");
            title.setWrapText(true);
            Button confirm = new Button("Yes, empty my cart");
            confirm.setDefaultButton(true);
            confirm.setOnAction(e -> {
                flyoutOutput.setText("Cart emptied.");
                flyout.hide();
            });
            flyoutCard = card(title, confirm);
            flyoutCard.setPrefWidth(260);

            flyout = new Popup();
            flyout.getContent().add(flyoutCard);
            flyout.setOnHidden(e -> {
                if (!"Cart emptied.".equals(flyoutOutput.getText())) {
                    flyoutOutput.setText("Flyout closed without confirming.");
                }
            });
        }
        if (flyout.isShowing()) {
            flyout.hide();
            return;
        }
        flyout.setAutoHide(autoHide.isSelected());
        flyout.setHideOnEscape(hideOnEscape.isSelected());
        flyout.setConsumeAutoHidingEvents(consumeAutoHiding.isSelected());
        ThemeManager.get().style(flyoutCard);

        flyoutOutput.setText(autoHide.isSelected()
                ? "Flyout open – click outside to close it."
                : "Flyout open – click the button again to close it.");
        Bounds bounds = flyoutButton.localToScreen(flyoutButton.getBoundsInLocal());
        flyout.show(flyoutButton, bounds.getMinX(), bounds.getMaxY() + GAP);
    }

    // --- Example 2 ---------------------------------------------------------------------------------------------

    @FXML
    private void onShowTip() {
        if (tip == null) {
            Label title = new Label("Did you know?");
            title.setStyle("-fx-font-weight: bold;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button close = new Button("✕");
            close.setOnAction(e -> tip.hide());
            HBox header = new HBox(8, title, spacer, close);
            header.setAlignment(Pos.CENTER_LEFT);

            Label text = new Label("Teaching tips point at a specific element. "
                    + "Choose another placement in the options to move this one.");
            text.setWrapText(true);
            Button gotIt = new Button("Got it");
            gotIt.setOnAction(e -> {
                tipOutput.setText("Tip dismissed.");
                tip.hide();
            });
            tipCard = card(header, text, gotIt);
            tipCard.setPrefWidth(280);

            tip = new Popup();
            tip.setAutoHide(true);
            tip.setHideOnEscape(true);
            tip.getContent().add(tipCard);
        }
        tip.hide();
        ThemeManager.get().style(tipCard);

        Side side = placement.getValue() == null ? Side.RIGHT : placement.getValue();
        Bounds b = tipTarget.localToScreen(tipTarget.getBoundsInLocal());
        double x;
        double y;
        switch (side) {
            case TOP -> {
                tip.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);
                x = b.getMinX();
                y = b.getMinY() - GAP;
            }
            case BOTTOM -> {
                tip.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
                x = b.getMinX();
                y = b.getMaxY() + GAP;
            }
            case LEFT -> {
                tip.setAnchorLocation(AnchorLocation.CONTENT_TOP_RIGHT);
                x = b.getMinX() - GAP;
                y = b.getMinY();
            }
            default -> {
                tip.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
                x = b.getMaxX() + GAP;
                y = b.getMinY();
            }
        }
        tip.show(tipTarget, x, y);
        tipOutput.setText("Placed at " + side + " with anchor location " + tip.getAnchorLocation() + ".");
    }

    /** Creates a card styled like the example cards; the "root" style class gives it the theme's base colors. */
    private static VBox card(Node... children) {
        VBox card = new VBox(12, children);
        card.getStyleClass().addAll("root", "example-card");
        card.setStyle("-fx-padding: 16;");
        card.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.35)));
        return card;
    }

    @Override
    public void dispose() {
        if (flyout != null) {
            flyout.hide();
        }
        if (tip != null) {
            tip.hide();
        }
    }
}
