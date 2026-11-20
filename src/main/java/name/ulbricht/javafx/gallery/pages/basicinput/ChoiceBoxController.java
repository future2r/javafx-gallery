package name.ulbricht.javafx.gallery.pages.basicinput;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Demo page for {@link ChoiceBox}: basic usage, a {@link StringConverter} and separators.
 */
public class ChoiceBoxController {

    @FXML
    private ChoiceBox<String> simpleChoice;
    @FXML
    private Label simpleOutput;
    @FXML
    private CheckBox disableChoice;
    @FXML
    private ChoiceBox<Locale> localeChoice;
    @FXML
    private Label localeOutput;
    @FXML
    private ChoiceBox<Object> separatorChoice;
    @FXML
    private Label separatorOutput;

    @FXML
    private void initialize() {
        simpleChoice.getItems().setAll("Small", "Medium", "Large", "Extra large");
        simpleChoice.disableProperty().bind(disableChoice.selectedProperty());
        simpleChoice.valueProperty().addListener((obs, oldValue, value) ->
                simpleOutput.setText("You selected: " + value));
        simpleChoice.setValue("Medium");

        localeChoice.getItems().setAll(Locale.US, Locale.UK, Locale.GERMANY, Locale.FRANCE, Locale.ITALY,
                Locale.of("es", "ES"), Locale.JAPAN, Locale.KOREA);
        localeChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Locale locale) {
                return locale == null ? "" : locale.getDisplayName(Locale.ENGLISH);
            }

            @Override
            public Locale fromString(String text) {
                return null; // not editable
            }
        });
        localeChoice.valueProperty().addListener((obs, oldValue, locale) -> {
            if (locale != null) {
                localeOutput.setText("Today: " + DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                        .withLocale(locale).format(LocalDate.now()));
            }
        });
        localeChoice.setValue(Locale.US);

        separatorChoice.getItems().setAll("New", "Open…", new Separator(), "Save", "Save as…",
                new Separator(), "Close");
        separatorChoice.valueProperty().addListener((obs, oldValue, value) ->
                separatorOutput.setText(value == null ? "Nothing selected." : "You selected: " + value));
        separatorChoice.setValue("New");
    }
}
