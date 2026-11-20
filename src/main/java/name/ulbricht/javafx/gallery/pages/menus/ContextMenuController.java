package name.ulbricht.javafx.gallery.pages.menus;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Locale;

/**
 * Controller of the ContextMenu demo page: a custom context menu for a TextArea, a context menu on a plain node and
 * a programmatically shown context menu.
 */
public class ContextMenuController {

    private static final String[][] COLORS = {
            {"Blue", "-gallery-demo-fill"},
            {"Orange", "-gallery-demo-fill-2"},
            {"Green", "-gallery-demo-fill-3"},
            {"Purple", "-gallery-demo-fill-4"}
    };

    @FXML
    private TextArea textArea;
    @FXML
    private ContextMenu textContextMenu;
    @FXML
    private MenuItem cutItem;
    @FXML
    private MenuItem copyItem;
    @FXML
    private Menu caseMenu;
    @FXML
    private Label textOutput;
    @FXML
    private CheckBox useCustomMenu;

    @FXML
    private Label target;
    @FXML
    private Label targetOutput;
    @FXML
    private CheckBox leftClickToo;

    @FXML
    private Button showButton;
    @FXML
    private Label eventOutput;
    @FXML
    private ComboBox<Side> sideChoice;
    @FXML
    private CheckBox autoHide;

    private final ToggleGroup colorGroup = new ToggleGroup();
    private final CheckMenuItem roundedItem = new CheckMenuItem("Rounded corners");
    private final CheckMenuItem largeTextItem = new CheckMenuItem("Large text");
    private String fillColor = COLORS[0][1];
    private ContextMenu programmaticMenu;

    @FXML
    private void initialize() {
        // Example 1
        cutItem.disableProperty().bind(textArea.selectedTextProperty().isEmpty());
        copyItem.disableProperty().bind(textArea.selectedTextProperty().isEmpty());
        caseMenu.disableProperty().bind(textArea.selectedTextProperty().isEmpty());
        useCustomMenu.selectedProperty().addListener((obs, oldValue, newValue) -> {
            textArea.setContextMenu(newValue ? textContextMenu : null);
            textOutput.setText(newValue ? "Custom context menu active." : "Built-in context menu active.");
        });

        // Example 2
        target.setContextMenu(createTargetMenu());
        target.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (leftClickToo.isSelected() && event.getButton() == MouseButton.PRIMARY) {
                target.getContextMenu().show(target, event.getScreenX(), event.getScreenY());
            }
        });
        updateTarget();

        // Example 3
        sideChoice.getItems().setAll(Side.values());
        sideChoice.setValue(Side.BOTTOM);
    }

    // --- Example 1 ---------------------------------------------------------------------------------------------

    @FXML
    private void onCut() {
        textArea.cut();
        textOutput.setText("Cut to clipboard.");
    }

    @FXML
    private void onCopy() {
        textArea.copy();
        textOutput.setText("Copied to clipboard.");
    }

    @FXML
    private void onPaste() {
        if (Clipboard.getSystemClipboard().hasString()) {
            textArea.paste();
            textOutput.setText("Pasted from clipboard.");
        } else {
            textOutput.setText("The clipboard contains no text.");
        }
    }

    @FXML
    private void onUpperCase() {
        textArea.replaceSelection(textArea.getSelectedText().toUpperCase(Locale.ROOT));
        textOutput.setText("Converted to upper case.");
    }

    @FXML
    private void onLowerCase() {
        textArea.replaceSelection(textArea.getSelectedText().toLowerCase(Locale.ROOT));
        textOutput.setText("Converted to lower case.");
    }

    @FXML
    private void onSelectAll() {
        textArea.selectAll();
        textOutput.setText("Selected all text.");
    }

    // --- Example 2 ---------------------------------------------------------------------------------------------

    private ContextMenu createTargetMenu() {
        Menu colorMenu = new Menu("Color");
        for (String[] color : COLORS) {
            RadioMenuItem item = new RadioMenuItem(color[0]);
            item.setToggleGroup(colorGroup);
            item.setSelected(color[1].equals(fillColor));
            item.setOnAction(e -> {
                fillColor = color[1];
                updateTarget();
                targetOutput.setText("Color: " + color[0]);
            });
            colorMenu.getItems().add(item);
        }
        roundedItem.setOnAction(e -> {
            updateTarget();
            targetOutput.setText("Rounded corners: " + (roundedItem.isSelected() ? "on" : "off"));
        });
        largeTextItem.setOnAction(e -> {
            updateTarget();
            targetOutput.setText("Large text: " + (largeTextItem.isSelected() ? "on" : "off"));
        });
        MenuItem reset = new MenuItem("Reset");
        reset.setOnAction(e -> {
            fillColor = COLORS[0][1];
            colorGroup.selectToggle(colorGroup.getToggles().getFirst());
            roundedItem.setSelected(false);
            largeTextItem.setSelected(false);
            updateTarget();
            targetOutput.setText("Reset to defaults.");
        });
        return new ContextMenu(colorMenu, roundedItem, largeTextItem, new SeparatorMenuItem(), reset);
    }

    private void updateTarget() {
        target.setStyle("-fx-background-color: %s; -fx-background-radius: %d; -fx-font-size: %s;".formatted(
                fillColor, roundedItem.isSelected() ? 40 : 4, largeTextItem.isSelected() ? "1.6em" : "1em"));
    }

    // --- Example 3 ---------------------------------------------------------------------------------------------

    @FXML
    private void onShowMenu() {
        if (programmaticMenu == null) {
            programmaticMenu = new ContextMenu();
            for (String text : new String[] {"Share", "Rename", "Duplicate", "Delete"}) {
                MenuItem item = new MenuItem(text);
                item.setOnAction(e -> eventOutput.setText("Chose: " + text));
                programmaticMenu.getItems().add(item);
            }
            programmaticMenu.setOnShowing(this::logEvent);
            programmaticMenu.setOnShown(this::logEvent);
            programmaticMenu.setOnHiding(this::logEvent);
            programmaticMenu.setOnHidden(this::logEvent);
        }
        if (programmaticMenu.isShowing()) {
            programmaticMenu.hide();
        }
        programmaticMenu.setAutoHide(autoHide.isSelected());
        programmaticMenu.show(showButton, sideChoice.getValue(), 0, 0);
    }

    private void logEvent(Event event) {
        String previous = eventOutput.getText();
        String line = "Event: " + event.getEventType().getName();
        eventOutput.setText(previous != null && previous.startsWith("Event:") && !previous.contains("WINDOW_HIDDEN")
                ? previous + " → " + event.getEventType().getName()
                : line);
    }
}
