package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.catalog.Category;
import name.ulbricht.javafx.gallery.catalog.ControlPage;
import name.ulbricht.javafx.gallery.catalog.GalleryCatalog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * Controller of the main window: navigation tree with search, back button, settings and content area.
 */
public class ShellController {

    private final Navigator navigator;

    @FXML
    private Button backButton;
    @FXML
    private Label pageTitle;
    @FXML
    private TextField searchField;
    @FXML
    private TreeView<Destination> navTree;
    @FXML
    private Button settingsButton;
    @FXML
    private StackPane contentHost;

    private boolean syncingSelection;

    public ShellController(Navigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void initialize() {
        backButton.setGraphic(Icons.create(Icons.BACK, 14));
        backButton.disableProperty().bind(navigator.canGoBackProperty().not());
        settingsButton.setGraphic(Icons.create(Icons.SETTINGS, 16));

        navTree.setCellFactory(tree -> new NavCell());
        navTree.setRoot(new TreeItem<>());
        navTree.setShowRoot(false);
        rebuildTree("");

        navTree.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (!syncingSelection && newItem != null && newItem.getValue() != null) {
                navigator.navigate(newItem.getValue());
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> rebuildTree(newText));
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                GalleryCatalog.search(searchField.getText()).stream().findFirst()
                        .ifPresent(page -> navigator.navigate(new Destination.Demo(page)));
            } else if (event.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
            }
        });

        navigator.viewProperty().addListener((obs, oldView, newView) -> {
            if (newView == null) {
                contentHost.getChildren().clear();
            } else {
                contentHost.getChildren().setAll(newView);
            }
        });
        navigator.currentProperty().addListener((obs, oldDest, newDest) -> {
            pageTitle.setText(newDest == null ? "" : newDest.title());
            selectInTree(newDest);
        });
    }

    @FXML
    private void goBack() {
        navigator.goBack();
    }

    @FXML
    private void openSettings() {
        navigator.navigate(new Destination.Settings());
    }

    /** Focuses the search field (bound to Ctrl+F / Ctrl+E by the application). */
    public void focusSearch() {
        searchField.requestFocus();
        searchField.selectAll();
    }

    private void rebuildTree(String query) {
        TreeItem<Destination> root = navTree.getRoot();
        boolean searching = query != null && !query.isBlank();
        syncingSelection = true;
        try {
            root.getChildren().clear();
            if (!searching) {
                root.getChildren().add(new TreeItem<>(new Destination.Home()));
            }
            for (Category category : Category.values()) {
                List<ControlPage> pages = GalleryCatalog.byCategory(category).stream()
                        .filter(p -> p.matches(query))
                        .toList();
                if (pages.isEmpty()) {
                    continue;
                }
                TreeItem<Destination> categoryItem = new TreeItem<>(new Destination.CategoryOverview(category));
                for (ControlPage page : pages) {
                    categoryItem.getChildren().add(new TreeItem<>(new Destination.Demo(page)));
                }
                categoryItem.setExpanded(searching);
                root.getChildren().add(categoryItem);
            }
        } finally {
            syncingSelection = false;
        }
        selectInTree(navigator.getCurrent());
    }

    private void selectInTree(Destination destination) {
        TreeItem<Destination> selected = navTree.getSelectionModel().getSelectedItem();
        if (selected != null && destination != null && destination.equals(selected.getValue())) {
            // keep the item the user clicked, e.g. TabPane listed under Navigation and under Layout
            return;
        }
        syncingSelection = true;
        try {
            TreeItem<Destination> item = find(navTree.getRoot(), destination);
            if (item == null) {
                navTree.getSelectionModel().clearSelection();
                return;
            }
            for (TreeItem<Destination> parent = item.getParent(); parent != null; parent = parent.getParent()) {
                parent.setExpanded(true);
            }
            navTree.getSelectionModel().select(item);
            int row = navTree.getRow(item);
            Platform.runLater(() -> {
                if (row >= 0) {
                    navTree.scrollTo(Math.max(0, row - 3));
                }
            });
        } finally {
            syncingSelection = false;
        }
    }

    private static TreeItem<Destination> find(TreeItem<Destination> item, Destination destination) {
        if (destination == null) {
            return null;
        }
        if (destination.equals(item.getValue())) {
            return item;
        }
        for (TreeItem<Destination> child : item.getChildren()) {
            TreeItem<Destination> found = find(child, destination);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static final class NavCell extends TreeCell<Destination> {
        @Override
        protected void updateItem(Destination item, boolean empty) {
            super.updateItem(item, empty);
            getStyleClass().removeAll("nav-category", "nav-page");
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            setText(item.title());
            switch (item) {
                case Destination.Home ignored -> setGraphic(Icons.create(Icons.HOME, 16));
                case Destination.Settings ignored -> setGraphic(Icons.create(Icons.SETTINGS, 16));
                case Destination.CategoryOverview overview -> {
                    getStyleClass().add("nav-category");
                    setGraphic(Icons.forCategory(overview.category(), 16));
                }
                case Destination.Demo ignored -> {
                    getStyleClass().add("nav-page");
                    setGraphic(null);
                }
            }
        }
    }
}
