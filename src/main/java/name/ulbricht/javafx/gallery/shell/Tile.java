package name.ulbricht.javafx.gallery.shell;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A clickable card with an icon, a title and a short description, used on the home and category pages.
 */
public class Tile extends Button {

    private static final double CONTENT_WIDTH = 208;

    public Tile(Node icon, String title, String description, Runnable action) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("tile-title");
        titleLabel.setMaxWidth(CONTENT_WIDTH);

        // A Text with a fixed wrapping width has a deterministic height; a wrapping Label inside a
        // button graphic would be measured without a width and become very tall.
        Text descriptionText = new Text(description);
        descriptionText.getStyleClass().add("tile-description");
        descriptionText.setWrappingWidth(CONTENT_WIDTH);

        VBox content = new VBox(8, icon, titleLabel, descriptionText);
        content.getStyleClass().add("tile-content");
        content.setPrefWidth(CONTENT_WIDTH);

        getStyleClass().setAll("tile");
        setGraphic(content);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAccessibleText(title + ". " + description);
        setOnAction(e -> action.run());
    }
}
