package name.ulbricht.javafx.gallery.pages.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller of the FileChooser &amp; DirectoryChooser demo page: opening files, saving files and picking folders.
 * The last used directory is remembered as the initial directory of the next dialog.
 */
public class FileChooserController {

    @FXML
    private CheckBox multiple;
    @FXML
    private CheckBox useFilters;
    @FXML
    private Label openOutput;
    @FXML
    private TextField initialFileName;
    @FXML
    private Label saveOutput;
    @FXML
    private Label directoryOutput;

    private File lastDirectory;

    @FXML
    private void initialize() {
        File home = new File(System.getProperty("user.home"));
        lastDirectory = home.isDirectory() ? home : null;
    }

    @FXML
    private void onOpen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(multiple.isSelected() ? "Open files" : "Open file");
        chooser.setInitialDirectory(validDirectory());
        if (useFilters.isSelected()) {
            chooser.getExtensionFilters().addAll(
                    new ExtensionFilter("All files", "*.*"),
                    new ExtensionFilter("Text files", "*.txt", "*.md", "*.csv"),
                    new ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                    new ExtensionFilter("Videos", "*.mp4", "*.m4v"));
        }

        Window owner = owner(event);
        if (multiple.isSelected()) {
            List<File> files = chooser.showOpenMultipleDialog(owner);
            if (files == null || files.isEmpty()) {
                openOutput.setText("Cancelled.");
            } else {
                remember(files.getFirst());
                openOutput.setText(files.size() + " file(s): "
                        + files.stream().map(File::getName).collect(Collectors.joining(", ")));
            }
        } else {
            File file = chooser.showOpenDialog(owner);
            if (file == null) {
                openOutput.setText("Cancelled.");
            } else {
                remember(file);
                openOutput.setText("Picked: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
            }
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as");
        chooser.setInitialDirectory(validDirectory());
        chooser.setInitialFileName(initialFileName.getText());
        ExtensionFilter text = new ExtensionFilter("Plain text", "*.txt");
        chooser.getExtensionFilters().addAll(
                text,
                new ExtensionFilter("Markdown", "*.md"),
                new ExtensionFilter("Rich text", "*.rtf"));
        chooser.setSelectedExtensionFilter(text);

        File file = chooser.showSaveDialog(owner(event));
        if (file == null) {
            saveOutput.setText("Cancelled.");
        } else {
            remember(file);
            ExtensionFilter selected = chooser.getSelectedExtensionFilter();
            saveOutput.setText("Would save to: " + file.getAbsolutePath()
                    + (selected == null ? "" : " as " + selected.getDescription()));
        }
    }

    @FXML
    private void onChooseDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a folder");
        chooser.setInitialDirectory(validDirectory());

        File directory = chooser.showDialog(owner(event));
        if (directory == null) {
            directoryOutput.setText("Cancelled.");
        } else {
            lastDirectory = directory;
            String[] entries = directory.list();
            directoryOutput.setText("Picked: " + directory.getAbsolutePath()
                    + (entries == null ? "" : " (" + entries.length + " entries)"));
        }
    }

    private void remember(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            lastDirectory = parent;
        }
    }

    /** The initial directory must exist, otherwise the native dialog may fail to open. */
    private File validDirectory() {
        return lastDirectory != null && lastDirectory.isDirectory() ? lastDirectory : null;
    }

    private static Window owner(ActionEvent event) {
        return ((Node) event.getSource()).getScene().getWindow();
    }
}
