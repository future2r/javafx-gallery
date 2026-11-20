package name.ulbricht.javafx.gallery.pages.collections;

import name.ulbricht.javafx.gallery.pages.data.FileItem;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller of the TreeView demo page: expand/collapse with custom cells, lazy loading and check box trees.
 */
public class TreeViewController {

    private static final String FOLDER_ICON = "M1 3.5A1.5 1.5 0 0 1 2.5 2h3.6l1.5 1.5h5.9A1.5 1.5 0 0 1 15 5v7.5a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 1 12.5z";
    private static final String FILE_ICON = "M3 1.5A1.5 1.5 0 0 1 4.5 0H10l4 4v10.5a1.5 1.5 0 0 1-1.5 1.5h-8A1.5 1.5 0 0 1 3 14.5z";

    @FXML
    private TreeView<FileItem> fileTree;
    @FXML
    private Label fileOutput;
    @FXML
    private CheckBox showRoot;

    @FXML
    private TreeView<String> lazyTree;
    @FXML
    private Label lazyOutput;

    @FXML
    private TreeView<String> checkTree;
    @FXML
    private Label checkOutput;
    @FXML
    private CheckBox independent;

    private int loadedNodes;

    @FXML
    private void initialize() {
        initFileTree();
        initLazyTree();
        initCheckTree();
    }

    private void initFileTree() {
        fileTree.setRoot(FileItem.createSampleTree());
        fileTree.showRootProperty().bind(showRoot.selectedProperty());
        fileTree.setCellFactory(tree -> new FileCell());
        fileTree.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> {
            if (item == null) {
                fileOutput.setText("Select a node.");
                return;
            }
            List<String> path = new ArrayList<>();
            for (TreeItem<FileItem> i = item; i != null; i = i.getParent()) {
                path.addFirst(i.getValue().name());
            }
            fileOutput.setText(String.join(" › ", path) + "  (" + item.getValue().formattedSize() + ")");
        });
    }

    @FXML
    private void onExpandAll() {
        setExpanded(fileTree.getRoot(), true);
    }

    @FXML
    private void onCollapseAll() {
        setExpanded(fileTree.getRoot(), false);
        fileTree.getRoot().setExpanded(true);
    }

    private static <T> void setExpanded(TreeItem<T> item, boolean expanded) {
        if (!item.isLeaf()) {
            item.setExpanded(expanded);
            for (TreeItem<T> child : item.getChildren()) {
                setExpanded(child, expanded);
            }
        }
    }

    private void initLazyTree() {
        LazyTreeItem root = new LazyTreeItem("Root", 0, this::onChildrenLoaded);
        root.setExpanded(true);
        lazyTree.setRoot(root);
    }

    private void onChildrenLoaded(LazyTreeItem item) {
        loadedNodes += item.getChildren().size();
        lazyOutput.setText("Loaded " + item.getChildren().size() + " children of \"" + item.getValue()
                + "\" – " + loadedNodes + " nodes created so far.");
    }

    private void initCheckTree() {
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>("Toppings");
        root.setExpanded(true);
        root.getChildren().add(category("Cheese", "Mozzarella", "Parmesan", "Gorgonzola", "Cheddar"));
        root.getChildren().add(category("Vegetables", "Tomatoes", "Mushrooms", "Onions", "Peppers", "Olives"));
        root.getChildren().add(category("Meat", "Ham", "Salami", "Bacon"));
        root.getChildren().add(category("Herbs", "Basil", "Oregano"));
        ((CheckBoxTreeItem<String>) root.getChildren().getFirst().getChildren().getFirst()).setSelected(true);

        checkTree.setRoot(root);
        checkTree.setCellFactory(CheckBoxTreeCell.forTreeView());
        root.addEventHandler(CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(), event -> updateCheckOutput());
        independent.selectedProperty().addListener((obs, oldValue, value) -> setIndependent(root, value));
        updateCheckOutput();
    }

    private static CheckBoxTreeItem<String> category(String name, String... leaves) {
        CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(name);
        item.setExpanded(true);
        for (String leaf : leaves) {
            item.getChildren().add(new CheckBoxTreeItem<>(leaf));
        }
        return item;
    }

    private static void setIndependent(TreeItem<String> item, boolean independent) {
        if (item instanceof CheckBoxTreeItem<String> checkItem) {
            checkItem.setIndependent(independent);
        }
        for (TreeItem<String> child : item.getChildren()) {
            setIndependent(child, independent);
        }
    }

    private void updateCheckOutput() {
        List<String> selected = new ArrayList<>();
        collectSelectedLeaves(checkTree.getRoot(), selected);
        checkOutput.setText(selected.isEmpty() ? "No toppings selected." : "Selected: " + String.join(", ", selected));
    }

    private static void collectSelectedLeaves(TreeItem<String> item, List<String> result) {
        if (item.isLeaf()) {
            if (item instanceof CheckBoxTreeItem<String> checkItem && checkItem.isSelected()) {
                result.add(item.getValue());
            }
        } else {
            for (TreeItem<String> child : item.getChildren()) {
                collectSelectedLeaves(child, result);
            }
        }
    }

    /** Shows a folder or file icon in front of the name. */
    private static final class FileCell extends TreeCell<FileItem> {

        @Override
        protected void updateItem(FileItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.name());
                setGraphic(icon(item.directory()));
            }
        }

        private static Node icon(boolean directory) {
            SVGPath path = new SVGPath();
            path.setContent(directory ? FOLDER_ICON : FILE_ICON);
            path.setStyle(directory ? "-fx-fill: #e8b84a;" : "-fx-fill: #8aa4c8;");
            return path;
        }
    }

    /** A TreeItem that creates its children on first access. */
    private static final class LazyTreeItem extends TreeItem<String> {

        private static final int MAX_DEPTH = 4;

        private final int depth;
        private final Consumer<LazyTreeItem> loadedCallback;
        private boolean loaded;

        LazyTreeItem(String value, int depth, Consumer<LazyTreeItem> loadedCallback) {
            super(value);
            this.depth = depth;
            this.loadedCallback = loadedCallback;
        }

        @Override
        public boolean isLeaf() {
            return depth >= MAX_DEPTH;
        }

        @Override
        public ObservableList<TreeItem<String>> getChildren() {
            ObservableList<TreeItem<String>> children = super.getChildren();
            if (!loaded && !isLeaf()) {
                loaded = true;
                int count = 3 + (getValue().hashCode() & 3);
                List<TreeItem<String>> created = new ArrayList<>(count);
                for (int i = 1; i <= count; i++) {
                    String name = depth == 0 ? "Node " + i : getValue() + "." + i;
                    created.add(new LazyTreeItem(name, depth + 1, loadedCallback));
                }
                children.setAll(created);
                loadedCallback.accept(this);
            }
            return children;
        }
    }
}
