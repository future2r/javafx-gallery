package name.ulbricht.javafx.gallery.pages.collections;

import name.ulbricht.javafx.gallery.pages.data.Person;
import name.ulbricht.javafx.gallery.pages.data.SampleData;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Controller of the ListView demo page: selection modes, custom cells, editing and horizontal orientation.
 */
public class ListViewController {

    private static final Color[] AVATAR_COLORS = {
            Color.web("#3d8fd1"), Color.web("#e5734b"), Color.web("#56b16a"), Color.web("#b06ad1"),
            Color.web("#d1a33d"), Color.web("#3dbbd1")};

    private static final List<String> COLOR_NAMES = List.of(
            "Crimson", "Coral", "Gold", "YellowGreen", "SeaGreen", "Teal", "DodgerBlue", "RoyalBlue",
            "SlateBlue", "Orchid", "HotPink", "Sienna", "SlateGray");

    @FXML
    private ListView<String> basicList;
    @FXML
    private Label basicOutput;
    @FXML
    private ComboBox<SelectionMode> selectionMode;
    @FXML
    private CheckBox disableBasic;

    @FXML
    private ListView<Person> personList;
    @FXML
    private Label personOutput;
    @FXML
    private CheckBox fixedCellSize;

    @FXML
    private ListView<String> editableList;
    @FXML
    private Button removeButton;
    @FXML
    private Label editOutput;
    @FXML
    private CheckBox editableCheck;

    @FXML
    private ListView<String> colorList;
    @FXML
    private Label colorOutput;
    @FXML
    private ComboBox<Orientation> orientation;

    private int newItemCounter;

    @FXML
    private void initialize() {
        initBasicList();
        initPersonList();
        initEditableList();
        initColorList();
    }

    private void initBasicList() {
        basicList.getItems().setAll(SampleData.FRUITS);
        selectionMode.getItems().setAll(SelectionMode.values());
        selectionMode.setValue(SelectionMode.SINGLE);
        selectionMode.valueProperty().addListener((obs, oldMode, newMode) -> {
            if (newMode != null) {
                basicList.getSelectionModel().setSelectionMode(newMode);
            }
        });
        basicList.disableProperty().bind(disableBasic.selectedProperty());
        basicList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) change -> {
            List<String> selected = List.copyOf(basicList.getSelectionModel().getSelectedItems());
            basicOutput.setText(selected.isEmpty() ? "Nothing selected." : "Selected: " + String.join(", ", selected));
        });
    }

    private void initPersonList() {
        personList.getItems().setAll(Person.createSamples(25));
        personList.setCellFactory(list -> new PersonCell());
        personList.getSelectionModel().selectedItemProperty().addListener((obs, oldPerson, person) ->
                personOutput.setText(person == null ? "Select a person."
                        : "Selected: " + person.getFullName() + " (" + person.getDepartment() + ")"));
        fixedCellSize.selectedProperty().addListener((obs, oldValue, fixed) ->
                personList.setFixedCellSize(fixed ? 52 : -1));
    }

    private void initEditableList() {
        editableList.getItems().setAll("Buy milk", "Call grandma", "Write report", "Book flights", "Water plants");
        editableList.setCellFactory(TextFieldListCell.forListView());
        editableList.editableProperty().bind(editableCheck.selectedProperty());
        removeButton.disableProperty().bind(editableList.getSelectionModel().selectedItemProperty().isNull());
        // addEventHandler keeps the default commit handler, which writes the new value into the items list
        editableList.addEventHandler(ListView.<String>editCommitEvent(), event ->
                editOutput.setText("Item " + (event.getIndex() + 1) + " changed to \"" + event.getNewValue() + "\""));
        editableList.addEventHandler(ListView.<String>editCancelEvent(), event ->
                editOutput.setText("Edit of item " + (event.getIndex() + 1) + " cancelled."));
    }

    private void initColorList() {
        colorList.getItems().setAll(COLOR_NAMES);
        colorList.setOrientation(Orientation.HORIZONTAL);
        colorList.setCellFactory(list -> new ColorCell());
        orientation.getItems().setAll(Orientation.values());
        orientation.setValue(Orientation.HORIZONTAL);
        orientation.valueProperty().addListener((obs, oldValue, value) -> {
            if (value == null) {
                return;
            }
            colorList.setOrientation(value);
            if (value == Orientation.HORIZONTAL) {
                colorList.setPrefSize(480, 110);
            } else {
                colorList.setPrefSize(220, 260);
            }
        });
        colorList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, color) ->
                colorOutput.setText(color == null ? "Select a color." : "Selected color: " + color));
    }

    @FXML
    private void onAddItem() {
        newItemCounter++;
        String item = "New item " + newItemCounter;
        editableList.getItems().add(item);
        int index = editableList.getItems().size() - 1;
        editableList.getSelectionModel().clearAndSelect(index);
        editableList.scrollTo(index);
        editOutput.setText("Added \"" + item + "\"");
        if (editableList.isEditable()) {
            // start editing after the new cell has been laid out
            Platform.runLater(() -> {
                editableList.requestFocus();
                editableList.edit(index);
            });
        }
    }

    @FXML
    private void onRemoveItem() {
        int index = editableList.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            String removed = editableList.getItems().remove(index);
            editOutput.setText("Removed \"" + removed + "\"");
        }
    }

    /** Renders a person with an avatar circle, the name and the e-mail address. */
    private static final class PersonCell extends ListCell<Person> {

        private final Circle avatar = new Circle(18);
        private final Label initials = new Label();
        private final Label name = new Label();
        private final Label email = new Label();
        private final HBox graphic;

        PersonCell() {
            initials.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            name.setStyle("-fx-font-weight: bold;");
            email.setOpacity(0.7);
            VBox text = new VBox(2, name, email);
            text.setAlignment(Pos.CENTER_LEFT);
            graphic = new HBox(10, new StackPane(avatar, initials), text);
            graphic.setAlignment(Pos.CENTER_LEFT);
            graphic.setPadding(new Insets(2, 0, 2, 0));
        }

        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);
            setText(null);
            if (empty || person == null) {
                setGraphic(null);
            } else {
                avatar.setFill(AVATAR_COLORS[Math.floorMod(person.getFullName().hashCode(), AVATAR_COLORS.length)]);
                initials.setText(person.getInitials());
                name.setText(person.getFullName());
                email.setText(person.getEmail());
                setGraphic(graphic);
            }
        }
    }

    /** Renders a color name with a color swatch above the text. */
    private static final class ColorCell extends ListCell<String> {

        private final Rectangle swatch = new Rectangle(56, 40);

        ColorCell() {
            swatch.setArcWidth(8);
            swatch.setArcHeight(8);
            setContentDisplay(ContentDisplay.TOP);
            setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(String color, boolean empty) {
            super.updateItem(color, empty);
            if (empty || color == null) {
                setText(null);
                setGraphic(null);
            } else {
                swatch.setFill(Color.web(color));
                setText(color);
                setGraphic(swatch);
            }
        }
    }
}
