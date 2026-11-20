package name.ulbricht.javafx.gallery.shell;

import name.ulbricht.javafx.gallery.catalog.ControlPage;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Root element of every demo page FXML. Shows a header (category, title, description and a link to the API
 * documentation) followed by the {@link Example}s declared as children in FXML.
 * <p>
 * The header is filled from the {@link ControlPage} catalog entry by the {@link Navigator}, so the catalog is the
 * single source of truth for titles and descriptions.
 */
public class DemoPage extends VBox {

    private final Label category = new Label();
    private final Label title = new Label();
    private final Label description = new Label();
    private final Hyperlink docsLink = new Hyperlink();
    private ControlPage page;

    public DemoPage() {
        getStyleClass().addAll("page", "demo-page");
        category.getStyleClass().add("page-category");
        title.getStyleClass().add("page-title");
        description.getStyleClass().add("page-description");
        description.setWrapText(true);
        docsLink.getStyleClass().add("docs-link");

        VBox header = new VBox(4, category, title, description, docsLink);
        header.getStyleClass().add("page-header");
        getChildren().add(header);
    }

    /** Fills the header from the catalog entry. */
    public void setPage(ControlPage page, Consumer<String> linkOpener) {
        this.page = page;
        category.setText(page.primaryCategory().getTitle());
        title.setText(page.title());
        description.setText(page.description());
        docsLink.setText(page.apiDisplayName());
        docsLink.setOnAction(e -> linkOpener.accept(page.javadocUrl()));
    }

    public ControlPage getPage() {
        return page;
    }
}
