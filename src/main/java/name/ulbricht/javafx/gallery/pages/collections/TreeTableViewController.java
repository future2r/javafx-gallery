package name.ulbricht.javafx.gallery.pages.collections;

import name.ulbricht.javafx.gallery.pages.data.FileItem;
import name.ulbricht.javafx.gallery.pages.data.Person;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

/**
 * Controller of the TreeTableView demo page: a sortable file system tree table and an editable organization chart.
 */
public class TreeTableViewController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    @FXML
    private TreeTableView<FileItem> fileTable;
    @FXML
    private TreeTableColumn<FileItem, String> nameColumn;
    @FXML
    private TreeTableColumn<FileItem, Long> sizeColumn;
    @FXML
    private TreeTableColumn<FileItem, String> typeColumn;
    @FXML
    private TreeTableColumn<FileItem, LocalDate> modifiedColumn;
    @FXML
    private Label fileOutput;
    @FXML
    private CheckBox showRoot;
    @FXML
    private CheckBox menuButtonVisible;
    @FXML
    private ComboBox<TreeSortMode> sortMode;

    @FXML
    private TreeTableView<Person> orgTable;
    @FXML
    private TreeTableColumn<Person, String> orgFirstName;
    @FXML
    private TreeTableColumn<Person, String> orgLastName;
    @FXML
    private TreeTableColumn<Person, String> orgDepartment;
    @FXML
    private TreeTableColumn<Person, String> orgEmail;
    @FXML
    private Label orgOutput;
    @FXML
    private CheckBox editableCheck;
    @FXML
    private CheckBox cellSelection;

    @FXML
    private void initialize() {
        initFileTable();
        initOrgTable();
    }

    private void initFileTable() {
        nameColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getValue().name()));
        sizeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getValue().size()));
        sizeColumn.setCellFactory(column -> new TreeTableCell<>() {
            {
                setAlignment(Pos.CENTER_RIGHT);
            }

            @Override
            protected void updateItem(Long size, boolean empty) {
                super.updateItem(size, empty);
                setText(empty || size == null ? null : FileItem.formatSize(size));
            }
        });
        typeColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getValue().type()));
        modifiedColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getValue().modified()));
        modifiedColumn.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : DATE_FORMAT.format(date));
            }
        });

        fileTable.setRoot(FileItem.createSampleTree());
        fileTable.showRootProperty().bind(showRoot.selectedProperty());
        fileTable.tableMenuButtonVisibleProperty().bind(menuButtonVisible.selectedProperty());
        sortMode.getItems().setAll(TreeSortMode.values());
        sortMode.setValue(fileTable.getSortMode());
        sortMode.valueProperty().addListener((obs, oldMode, mode) -> {
            if (mode != null) {
                fileTable.setSortMode(mode);
            }
        });
        fileTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> {
            if (item == null) {
                fileOutput.setText("Select a row.");
            } else {
                FileItem file = item.getValue();
                fileOutput.setText("Selected: " + file.name() + " – " + file.type() + ", " + file.formattedSize()
                        + (file.directory() ? " in " + item.getChildren().size() + " items" : ""));
            }
        });
    }

    @FXML
    private void onExpandAll() {
        setExpanded(fileTable.getRoot(), true);
    }

    @FXML
    private void onCollapseAll() {
        setExpanded(fileTable.getRoot(), false);
        fileTable.getRoot().setExpanded(true);
    }

    private static <T> void setExpanded(TreeItem<T> item, boolean expanded) {
        if (!item.isLeaf()) {
            item.setExpanded(expanded);
            for (TreeItem<T> child : item.getChildren()) {
                setExpanded(child, expanded);
            }
        }
    }

    private void initOrgTable() {
        orgFirstName.setCellValueFactory(c -> c.getValue().getValue().firstNameProperty());
        orgFirstName.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        orgLastName.setCellValueFactory(c -> c.getValue().getValue().lastNameProperty());
        orgLastName.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        orgDepartment.setCellValueFactory(c -> c.getValue().getValue().departmentProperty());
        orgDepartment.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(
                FXCollections.observableArrayList(Person.DEPARTMENTS)));
        orgEmail.setCellValueFactory(c -> c.getValue().getValue().emailProperty());
        orgEmail.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        TreeItem<Person> ceo = node("Grace", "Hopper", "Management",
                node("Ada", "Lovelace", "Development",
                        node("Linus", "Torvalds", "Development"),
                        node("Margaret", "Hamilton", "Development"),
                        node("Dennis", "Ritchie", "Development")),
                node("Dieter", "Rams", "Design",
                        node("Paula", "Scher", "Design"),
                        node("Massimo", "Vignelli", "Design")),
                node("Mary", "Kay", "Sales",
                        node("Zig", "Ziglar", "Sales"),
                        node("Estee", "Lauder", "Marketing")));
        expandAll(ceo);
        orgTable.setRoot(ceo);
        orgTable.editableProperty().bind(editableCheck.selectedProperty());
        orgTable.getSelectionModel().cellSelectionEnabledProperty().bind(cellSelection.selectedProperty());

        for (TreeTableColumn<Person, String> column : List.of(orgFirstName, orgLastName, orgDepartment, orgEmail)) {
            column.addEventHandler(TreeTableColumn.<Person, String>editCommitEvent(), event ->
                    orgOutput.setText(event.getTableColumn().getText() + " changed from \"" + event.getOldValue()
                            + "\" to \"" + event.getNewValue() + "\""));
        }
    }

    private static void expandAll(TreeItem<Person> item) {
        item.setExpanded(true);
        item.getChildren().forEach(TreeTableViewController::expandAll);
    }

    @SafeVarargs
    private static TreeItem<Person> node(String first, String last, String department, TreeItem<Person>... reports) {
        String email = (first + "." + last).toLowerCase(java.util.Locale.ROOT) + "@example.com";
        TreeItem<Person> item = new TreeItem<>(new Person(first, last, email, department, 40, true));
        item.getChildren().setAll(java.util.Arrays.asList(reports));
        return item;
    }
}
