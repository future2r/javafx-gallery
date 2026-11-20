package name.ulbricht.javafx.gallery.pages.navigation;

import name.ulbricht.javafx.gallery.pages.data.SampleData;
import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller of the Pagination demo page: page factories, indicator settings, a paged list and an auto-advancing
 * flip view.
 */
public class PaginationController implements Disposable {

    private static final List<String> ALT_CLASSES = List.of("", "alt-1", "alt-2", "alt-3");
    private static final List<String> SLIDE_TITLES = List.of("Sunrise", "Ocean", "Forest", "Desert", "Night sky");
    private static final List<String[]> SLIDE_COLORS = List.of(
            new String[]{"#f7b267", "#f25c54"}, new String[]{"#4facfe", "#00f2fe"}, new String[]{"#43e97b", "#38a169"},
            new String[]{"#f6d365", "#fda085"}, new String[]{"#30cfd0", "#330867"});

    private final List<String> items = SampleData.generateItems("Item", 1000);

    @FXML
    private Pagination basicPagination;
    @FXML
    private Label basicOutput;
    @FXML
    private Spinner<Integer> pageCount;
    @FXML
    private Spinner<Integer> maxIndicators;
    @FXML
    private CheckBox bulletStyle;

    @FXML
    private Pagination listPagination;
    @FXML
    private ComboBox<Integer> itemsPerPage;
    @FXML
    private Label listOutput;

    @FXML
    private Pagination flipPagination;
    @FXML
    private CheckBox autoAdvance;

    private Timeline autoAdvanceTimeline;

    @FXML
    private void initialize() {
        initBasicPagination();
        initListPagination();
        initFlipPagination();
    }

    private void initBasicPagination() {
        basicPagination.setPageFactory(PaginationController::createBasicPage);
        pageCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        maxIndicators.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 10));
        basicPagination.pageCountProperty().bind(pageCount.valueProperty());
        basicPagination.maxPageIndicatorCountProperty().bind(maxIndicators.valueProperty());
        bulletStyle.selectedProperty().addListener((obs, oldValue, bullet) -> {
            if (bullet) {
                basicPagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
            } else {
                basicPagination.getStyleClass().remove(Pagination.STYLE_CLASS_BULLET);
            }
        });
        basicPagination.currentPageIndexProperty().addListener((obs, oldIndex, index) -> updateBasicOutput());
        basicPagination.pageCountProperty().addListener((obs, oldCount, count) -> updateBasicOutput());
        updateBasicOutput();
    }

    private void updateBasicOutput() {
        basicOutput.setText("Page " + (basicPagination.getCurrentPageIndex() + 1) + " of " + basicPagination.getPageCount());
    }

    private static Node createBasicPage(int index) {
        Label box = new Label("Page " + (index + 1));
        box.getStyleClass().add("demo-box");
        String alt = ALT_CLASSES.get(index % ALT_CLASSES.size());
        if (!alt.isEmpty()) {
            box.getStyleClass().add(alt);
        }
        box.setPrefSize(260, 140);
        box.setStyle("-fx-font-size: 22px;");
        StackPane page = new StackPane(box);
        page.setAlignment(Pos.CENTER);
        return page;
    }

    private void initListPagination() {
        itemsPerPage.getItems().setAll(10, 20, 50, 100);
        itemsPerPage.valueProperty().addListener((obs, oldValue, value) -> {
            if (value != null) {
                updateListPagination(value);
            }
        });
        itemsPerPage.setValue(10);
        listPagination.currentPageIndexProperty().addListener((obs, oldIndex, index) -> updateListOutput());
    }

    private void updateListPagination(int perPage) {
        listPagination.setPageCount((items.size() + perPage - 1) / perPage);
        listPagination.setCurrentPageIndex(0);
        // a new factory instance forces the Pagination to recreate the current page
        listPagination.setPageFactory(index -> {
            int from = index * perPage;
            if (from >= items.size()) {
                return null;
            }
            ListView<String> list = new ListView<>();
            list.getItems().setAll(items.subList(from, Math.min(from + perPage, items.size())));
            return list;
        });
        updateListOutput();
    }

    private void updateListOutput() {
        Integer perPage = itemsPerPage.getValue();
        if (perPage == null) {
            return;
        }
        int from = listPagination.getCurrentPageIndex() * perPage;
        int to = Math.min(from + perPage, items.size());
        listOutput.setText("Showing items " + (from + 1) + "–" + to + " of " + items.size() + ".");
    }

    private void initFlipPagination() {
        flipPagination.setPageCount(SLIDE_TITLES.size());
        flipPagination.setPageFactory(PaginationController::createSlide);
        autoAdvanceTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event ->
                flipPagination.setCurrentPageIndex((flipPagination.getCurrentPageIndex() + 1) % flipPagination.getPageCount())));
        autoAdvanceTimeline.setCycleCount(Animation.INDEFINITE);
        autoAdvance.selectedProperty().addListener((obs, oldValue, enabled) -> {
            if (enabled) {
                autoAdvanceTimeline.play();
            } else {
                autoAdvanceTimeline.stop();
            }
        });
    }

    private static Node createSlide(int index) {
        if (index < 0 || index >= SLIDE_TITLES.size()) {
            return null;
        }
        String[] colors = SLIDE_COLORS.get(index);
        Rectangle background = new Rectangle(380, 200);
        background.setArcWidth(16);
        background.setArcHeight(16);
        background.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(colors[0])), new Stop(1, Color.web(colors[1]))));
        Label title = new Label(SLIDE_TITLES.get(index));
        title.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");
        Label caption = new Label("Slide " + (index + 1) + " of " + SLIDE_TITLES.size());
        caption.setStyle("-fx-text-fill: white;");
        VBox text = new VBox(4, title, caption);
        text.setAlignment(Pos.CENTER);
        return new StackPane(background, text);
    }

    @Override
    public void dispose() {
        if (autoAdvanceTimeline != null) {
            autoAdvanceTimeline.stop();
        }
    }
}
