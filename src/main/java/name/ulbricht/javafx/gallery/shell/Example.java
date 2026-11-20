package name.ulbricht.javafx.gallery.shell;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

/**
 * One example section of a demo page: a title, an optional description, the example content and an optional
 * options panel on the right side whose controls change the example live.
 *
 * <pre>{@code
 * <Example title="A simple Button with text content">
 *     <Button fx:id="button" text="Standard JavaFX button"/>
 *     <options>
 *         <VBox><CheckBox fx:id="disable" text="Disable button"/></VBox>
 *     </options>
 * </Example>
 * }</pre>
 */
@DefaultProperty("content")
public class Example extends VBox {

    private final Label titleLabel = new Label();
    private final Label descriptionLabel = new Label();
    private final StackPane contentHolder = new StackPane();
    private final StackPane optionsHolder = new StackPane();

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
    private final ObjectProperty<Node> options = new SimpleObjectProperty<>(this, "options");

    public Example() {
        getStyleClass().add("example");
        titleLabel.getStyleClass().add("example-title");
        titleLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("example-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.managedProperty().bind(descriptionLabel.textProperty().isNotEmpty());
        descriptionLabel.visibleProperty().bind(descriptionLabel.managedProperty());

        contentHolder.getStyleClass().add("example-content");
        contentHolder.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(contentHolder, Priority.ALWAYS);
        contentHolder.setMinWidth(0);

        optionsHolder.getStyleClass().add("example-options");
        optionsHolder.setAlignment(Pos.TOP_LEFT);
        optionsHolder.managedProperty().bind(options.isNotNull());
        optionsHolder.visibleProperty().bind(options.isNotNull());

        HBox card = new HBox(contentHolder, optionsHolder);
        card.getStyleClass().add("example-card");

        getChildren().addAll(titleLabel, descriptionLabel, card);

        content.addListener((obs, oldNode, newNode) -> setHolderContent(contentHolder, newNode));
        options.addListener((obs, oldNode, newNode) -> setHolderContent(optionsHolder, newNode));
    }

    private static void setHolderContent(StackPane holder, Node node) {
        if (node == null) {
            holder.getChildren().clear();
        } else {
            holder.getChildren().setAll(node);
        }
    }

    public final StringProperty titleProperty() {
        return titleLabel.textProperty();
    }

    public final String getTitle() {
        return titleLabel.getText();
    }

    public final void setTitle(String value) {
        titleLabel.setText(value);
    }

    public final StringProperty descriptionProperty() {
        return descriptionLabel.textProperty();
    }

    public final String getDescription() {
        return descriptionLabel.getText();
    }

    public final void setDescription(String value) {
        descriptionLabel.setText(value);
    }

    public final ObjectProperty<Node> contentProperty() {
        return content;
    }

    public final Node getContent() {
        return content.get();
    }

    public final void setContent(Node value) {
        content.set(value);
    }

    public final ObjectProperty<Node> optionsProperty() {
        return options;
    }

    public final Node getOptions() {
        return options.get();
    }

    public final void setOptions(Node value) {
        options.set(value);
    }
}
