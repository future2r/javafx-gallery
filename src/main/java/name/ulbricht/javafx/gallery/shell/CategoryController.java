package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.catalog.Category;
import name.ulbricht.javafx.gallery.catalog.ControlPage;
import name.ulbricht.javafx.gallery.catalog.GalleryCatalog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

/**
 * Tile overview of the pages of one category.
 */
public class CategoryController {

    private final Navigator navigator;

    @FXML
    private StackPane iconHolder;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private FlowPane tiles;

    public CategoryController(Navigator navigator) {
        this.navigator = navigator;
    }

    public void setCategory(Category category) {
        iconHolder.getChildren().setAll(Icons.forCategory(category, 32));
        title.setText(category.getTitle());
        description.setText(category.getDescription());
        tiles.getChildren().clear();
        for (ControlPage page : GalleryCatalog.byCategory(category)) {
            tiles.getChildren().add(pageTile(navigator, page));
        }
    }

    static Tile pageTile(Navigator navigator, ControlPage page) {
        return new Tile(Icons.forCategory(page.primaryCategory(), 24), page.title(), page.description(),
                () -> navigator.navigate(new Destination.Demo(page)));
    }
}
