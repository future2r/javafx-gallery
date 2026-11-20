package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.catalog.Category;
import name.ulbricht.javafx.gallery.catalog.ControlPage;
import name.ulbricht.javafx.gallery.catalog.GalleryCatalog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

/**
 * The start page: tiles for all categories and all controls.
 */
public class HomeController {

    private final Navigator navigator;

    @FXML
    private Label summary;
    @FXML
    private FlowPane categoryTiles;
    @FXML
    private FlowPane controlTiles;

    public HomeController(Navigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void initialize() {
        summary.setText("%d demo pages in %d categories, built with Java %s and JavaFX %s.".formatted(
                GalleryCatalog.all().size(), Category.values().length,
                Runtime.version().feature(), System.getProperty("javafx.runtime.version", "?")));

        for (Category category : Category.values()) {
            int count = GalleryCatalog.byCategory(category).size();
            categoryTiles.getChildren().add(new Tile(Icons.forCategory(category, 28), category.getTitle(),
                    category.getDescription() + " (" + count + ")",
                    () -> navigator.navigate(new Destination.CategoryOverview(category))));
        }
        for (ControlPage page : GalleryCatalog.search("")) {
            controlTiles.getChildren().add(CategoryController.pageTile(navigator, page));
        }
    }
}
