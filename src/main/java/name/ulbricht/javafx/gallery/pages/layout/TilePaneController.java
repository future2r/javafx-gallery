package name.ulbricht.javafx.gallery.pages.layout;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;

/**
 * Controller of the TilePane demo page: uniform tiles, tile alignment, fixed tile sizes and preferred columns.
 */
public class TilePaneController {

    private static final String[] ALT_CLASSES = {"", "alt-1", "alt-2", "alt-3"};
    private static final String[] TEXTS = {"A", "Tile", "Larger tile", "B", "Tile 5", "Wide tile content", "C", "8"};

    @FXML
    private TilePane tiles;
    @FXML
    private ComboBox<Orientation> orientation;
    @FXML
    private ComboBox<Pos> tileAlignment;
    @FXML
    private Label gapLabel;
    @FXML
    private Slider gap;
    @FXML
    private CheckBox fixedTileSize;
    @FXML
    private Slider tileSize;

    @FXML
    private TilePane columnTiles;
    @FXML
    private Spinner<Integer> prefColumns;

    @FXML
    private void initialize() {
        for (int i = 0; i < 6; i++) {
            onAddTile();
        }
        orientation.getItems().setAll(Orientation.values());
        orientation.setValue(Orientation.HORIZONTAL);
        tiles.orientationProperty().bind(orientation.valueProperty());
        tileAlignment.getItems().setAll(Pos.values());
        tileAlignment.setValue(Pos.CENTER);
        tiles.tileAlignmentProperty().bind(tileAlignment.valueProperty());
        tiles.hgapProperty().bind(gap.valueProperty());
        tiles.vgapProperty().bind(gap.valueProperty());
        gapLabel.textProperty().bind(Bindings.format("Horizontal and vertical gap: %.0f", gap.valueProperty()));

        tileSize.disableProperty().bind(fixedTileSize.selectedProperty().not());
        fixedTileSize.selectedProperty().addListener((obs, oldValue, fixed) -> {
            if (fixed) {
                tiles.prefTileWidthProperty().bind(tileSize.valueProperty());
                tiles.prefTileHeightProperty().bind(tileSize.valueProperty().multiply(0.6));
            } else {
                tiles.prefTileWidthProperty().unbind();
                tiles.prefTileHeightProperty().unbind();
                tiles.setPrefTileWidth(Region.USE_COMPUTED_SIZE);
                tiles.setPrefTileHeight(Region.USE_COMPUTED_SIZE);
            }
        });

        for (int i = 1; i <= 12; i++) {
            Label label = new Label(String.valueOf(i));
            label.getStyleClass().add("demo-box");
            String alt = ALT_CLASSES[i % ALT_CLASSES.length];
            if (!alt.isEmpty()) {
                label.getStyleClass().add(alt);
            }
            label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            columnTiles.getChildren().add(label);
        }
        prefColumns.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, 4));
        columnTiles.prefColumnsProperty().bind(prefColumns.valueProperty());
    }

    @FXML
    private void onAddTile() {
        int index = tiles.getChildren().size();
        if (index >= 16) {
            return;
        }
        Label label = new Label(TEXTS[index % TEXTS.length]);
        label.getStyleClass().add("demo-box");
        String alt = ALT_CLASSES[index % ALT_CLASSES.length];
        if (!alt.isEmpty()) {
            label.getStyleClass().add(alt);
        }
        tiles.getChildren().add(label);
    }

    @FXML
    private void onRemoveTile() {
        if (!tiles.getChildren().isEmpty()) {
            tiles.getChildren().removeLast();
        }
    }
}
