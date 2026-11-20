package name.ulbricht.javafx.gallery.pages.collections;

import name.ulbricht.javafx.gallery.pages.data.Person;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller of the TableView demo page: sorting, resizing, selection, editable cells and filtering.
 */
public class TableViewController {

    @FXML
    private TableView<Person> basicTable;
    @FXML
    private TableColumn<Person, String> basicFirstName;
    @FXML
    private TableColumn<Person, String> basicLastName;
    @FXML
    private TableColumn<Person, Integer> basicAge;
    @FXML
    private TableColumn<Person, String> basicDepartment;
    @FXML
    private TableColumn<Person, String> basicEmail;
    @FXML
    private TableColumn<Person, Boolean> basicActive;
    @FXML
    private Label basicOutput;
    @FXML
    private ComboBox<String> resizePolicy;
    @FXML
    private ComboBox<SelectionMode> selectionMode;
    @FXML
    private CheckBox cellSelection;
    @FXML
    private CheckBox menuButtonVisible;

    @FXML
    private TableView<Person> editTable;
    @FXML
    private TableColumn<Person, String> editFirstName;
    @FXML
    private TableColumn<Person, String> editLastName;
    @FXML
    private TableColumn<Person, Integer> editAge;
    @FXML
    private TableColumn<Person, String> editDepartment;
    @FXML
    private TableColumn<Person, Boolean> editActive;
    @FXML
    private Label editOutput;
    @FXML
    private CheckBox editableCheck;

    @FXML
    private TextField filterField;
    @FXML
    private TableView<Person> filterTable;
    @FXML
    private TableColumn<Person, String> filterName;
    @FXML
    private TableColumn<Person, String> filterDepartment;
    @FXML
    private TableColumn<Person, String> filterEmail;
    @FXML
    private Label filterOutput;
    @FXML
    private CheckBox activeOnly;

    private FilteredList<Person> filteredPersons;

    @FXML
    private void initialize() {
        initBasicTable();
        initEditTable();
        initFilterTable();
    }

    @SuppressWarnings("rawtypes")
    private void initBasicTable() {
        basicFirstName.setCellValueFactory(c -> c.getValue().firstNameProperty());
        basicLastName.setCellValueFactory(c -> c.getValue().lastNameProperty());
        basicAge.setCellValueFactory(c -> c.getValue().ageProperty().asObject());
        basicDepartment.setCellValueFactory(c -> c.getValue().departmentProperty());
        basicEmail.setCellValueFactory(c -> c.getValue().emailProperty());
        basicActive.setCellValueFactory(c -> c.getValue().activeProperty());
        basicActive.setCellFactory(CheckBoxTableCell.forTableColumn(basicActive));
        basicTable.setItems(Person.createSamples(40));

        Map<String, Callback<TableView.ResizeFeatures, Boolean>> policies = new LinkedHashMap<>();
        policies.put("Unconstrained", TableView.UNCONSTRAINED_RESIZE_POLICY);
        policies.put("Constrained: all columns", TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        policies.put("Constrained: last column", TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        policies.put("Constrained: next column", TableView.CONSTRAINED_RESIZE_POLICY_NEXT_COLUMN);
        policies.put("Constrained: subsequent columns", TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        policies.put("Constrained: flex last column", TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        policies.put("Constrained: flex next column", TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
        resizePolicy.getItems().setAll(policies.keySet());
        resizePolicy.setValue("Unconstrained");
        resizePolicy.valueProperty().addListener((obs, oldValue, name) -> {
            if (name != null) {
                basicTable.setColumnResizePolicy(policies.get(name));
            }
        });

        selectionMode.getItems().setAll(SelectionMode.values());
        selectionMode.setValue(SelectionMode.SINGLE);
        selectionMode.valueProperty().addListener((obs, oldMode, mode) -> {
            if (mode != null) {
                basicTable.getSelectionModel().setSelectionMode(mode);
            }
        });
        basicTable.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelection.selectedProperty());
        basicTable.tableMenuButtonVisibleProperty().bind(menuButtonVisible.selectedProperty());

        InvalidationListener selectionListener = obs -> updateBasicOutput();
        basicTable.getSelectionModel().getSelectedItems().addListener(selectionListener);
        basicTable.getSelectionModel().getSelectedCells().addListener(selectionListener);
        basicTable.getSortOrder().addListener(selectionListener);
    }

    private void updateBasicOutput() {
        var selection = basicTable.getSelectionModel();
        String selected;
        if (selection.getSelectedItems().isEmpty()) {
            selected = "Nothing selected.";
        } else if (selection.isCellSelectionEnabled()) {
            selected = selection.getSelectedCells().size() + " cell(s) selected.";
        } else {
            selected = "Selected: " + selection.getSelectedItems().stream()
                    .map(Person::getFullName)
                    .collect(Collectors.joining(", "));
        }
        String sort = basicTable.getSortOrder().isEmpty() ? "" : "  Sorted by: " + basicTable.getSortOrder().stream()
                .map(column -> column.getText() + (column.getSortType() == TableColumn.SortType.ASCENDING ? " ↑" : " ↓"))
                .collect(Collectors.joining(", "));
        basicOutput.setText(selected + sort);
    }

    private void initEditTable() {
        ObservableList<Person> persons = Person.createSamples(12);
        editTable.setItems(persons);
        editTable.editableProperty().bind(editableCheck.selectedProperty());

        editFirstName.setCellValueFactory(c -> c.getValue().firstNameProperty());
        editFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        editLastName.setCellValueFactory(c -> c.getValue().lastNameProperty());
        editLastName.setCellFactory(TextFieldTableCell.forTableColumn());

        editAge.setCellValueFactory(c -> c.getValue().ageProperty().asObject());
        editAge.setCellFactory(TextFieldTableCell.forTableColumn(new LenientIntegerConverter()));
        editAge.setOnEditCommit(event -> {
            Integer age = event.getNewValue();
            if (age == null || age < 0 || age > 150) {
                editOutput.setText("Please enter an age between 0 and 150.");
                editTable.refresh();
            } else {
                event.getRowValue().setAge(age);
            }
        });

        editDepartment.setCellValueFactory(c -> c.getValue().departmentProperty());
        editDepartment.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(Person.DEPARTMENTS)));

        editActive.setCellValueFactory(c -> c.getValue().activeProperty());
        editActive.setCellFactory(CheckBoxTableCell.forTableColumn(editActive));

        for (TableColumn<Person, String> column : List.of(editFirstName, editLastName, editDepartment)) {
            column.addEventHandler(TableColumn.<Person, String>editCommitEvent(), event ->
                    editOutput.setText(event.getTableColumn().getText() + " of row " + (event.getTablePosition().getRow() + 1)
                            + " changed to \"" + event.getNewValue() + "\""));
        }
        editAge.addEventHandler(TableColumn.<Person, Integer>editCommitEvent(), event -> {
            if (event.getNewValue() != null) {
                editOutput.setText("Age of " + event.getRowValue().getFullName() + " changed to " + event.getNewValue());
            }
        });
        for (Person person : persons) {
            person.activeProperty().addListener((obs, oldValue, active) ->
                    editOutput.setText(person.getFullName() + " is now " + (active ? "active." : "inactive.")));
        }
    }

    private void initFilterTable() {
        filterName.setCellValueFactory(c -> Bindings.concat(
                c.getValue().firstNameProperty(), " ", c.getValue().lastNameProperty()));
        filterDepartment.setCellValueFactory(c -> c.getValue().departmentProperty());
        filterEmail.setCellValueFactory(c -> c.getValue().emailProperty());
        filterTable.setPlaceholder(new Label("No matching persons."));

        ObservableList<Person> persons = Person.createSamples(60);
        filteredPersons = new FilteredList<>(persons, person -> true);
        SortedList<Person> sortedPersons = new SortedList<>(filteredPersons);
        sortedPersons.comparatorProperty().bind(filterTable.comparatorProperty());
        filterTable.setItems(sortedPersons);

        filterField.textProperty().addListener((obs, oldText, text) -> updateFilter());
        activeOnly.selectedProperty().addListener((obs, oldValue, value) -> updateFilter());
        updateFilter();
    }

    private void updateFilter() {
        String text = filterField.getText() == null ? "" : filterField.getText().trim().toLowerCase(Locale.ROOT);
        boolean onlyActive = activeOnly.isSelected();
        filteredPersons.setPredicate(person -> {
            if (onlyActive && !person.isActive()) {
                return false;
            }
            return text.isEmpty()
                    || person.getFullName().toLowerCase(Locale.ROOT).contains(text)
                    || person.getDepartment().toLowerCase(Locale.ROOT).contains(text)
                    || person.getEmail().toLowerCase(Locale.ROOT).contains(text);
        });
        filterOutput.setText(filteredPersons.size() + " of " + filteredPersons.getSource().size() + " persons shown.");
    }

    /** Converts text to Integer and returns {@code null} instead of throwing for invalid input. */
    private static final class LenientIntegerConverter extends StringConverter<Integer> {

        @Override
        public String toString(Integer value) {
            return value == null ? "" : value.toString();
        }

        @Override
        public Integer fromString(String text) {
            try {
                return text == null ? null : Integer.valueOf(text.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
