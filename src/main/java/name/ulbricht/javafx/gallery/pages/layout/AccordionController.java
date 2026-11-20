package name.ulbricht.javafx.gallery.pages.layout;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

import java.util.List;

/**
 * Controller of the Accordion demo page: expanded pane handling and an accordion sidebar with lists.
 */
public class AccordionController {

    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane generalPane;
    @FXML
    private Label accordionOutput;
    @FXML
    private CheckBox animated;

    @FXML
    private Accordion sidebar;
    @FXML
    private TitledPane mailPane;
    @FXML
    private ListView<String> mailList;
    @FXML
    private ListView<String> calendarList;
    @FXML
    private ListView<String> contactsList;
    @FXML
    private Label sidebarOutput;

    @FXML
    private void initialize() {
        for (TitledPane pane : accordion.getPanes()) {
            pane.animatedProperty().bind(animated.selectedProperty());
        }
        accordion.expandedPaneProperty().addListener((obs, oldPane, pane) ->
                accordionOutput.setText(pane == null ? "All panes are collapsed." : "Expanded pane: " + pane.getText()));
        generalPane.setExpanded(true);
        accordion.setExpandedPane(generalPane);

        mailList.getItems().setAll("Inbox (3)", "Drafts", "Sent", "Junk", "Archive");
        calendarList.getItems().setAll("Today", "This week", "This month", "Birthdays", "Holidays");
        contactsList.getItems().setAll("Ada Lovelace", "Alan Turing", "Grace Hopper", "Linus Torvalds");
        for (ListView<String> list : List.of(mailList, calendarList, contactsList)) {
            list.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> {
                if (item != null) {
                    sidebarOutput.setText("Selected: " + item);
                }
            });
        }
        mailPane.setExpanded(true);
        sidebar.setExpandedPane(mailPane);
    }

    @FXML
    private void onExpandNext() {
        var panes = accordion.getPanes();
        TitledPane current = accordion.getExpandedPane();
        int next = current == null ? 0 : (panes.indexOf(current) + 1) % panes.size();
        panes.get(next).setExpanded(true);
        accordion.setExpandedPane(panes.get(next));
    }

    @FXML
    private void onCollapseAll() {
        TitledPane current = accordion.getExpandedPane();
        if (current != null) {
            current.setExpanded(false);
        }
    }
}
