package name.ulbricht.javafx.gallery.pages.media;

import name.ulbricht.javafx.gallery.shell.Disposable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

/**
 * Controller of the MediaView demo page: plays the bundled example video with play/pause/stop, seeking, volume,
 * mute, loop and playback rate. The {@link MediaPlayer} is prepared when the page opens (without autoplay) and
 * disposed when the page is left.
 */
public class MediaViewController implements Disposable {

    private static final String VIDEO = "/name/ulbricht/javafx/gallery/media/example.mp4";
    private static final Set<String> MEDIA_PROTOCOLS = Set.of("file", "jar", "http", "https");

    @FXML
    private MediaView mediaView;
    @FXML
    private Label placeholder;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Slider seekSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private Label mediaOutput;
    @FXML
    private Label volumeLabel;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ToggleButton muteToggle;
    @FXML
    private Label rateLabel;
    @FXML
    private Slider rateSlider;
    @FXML
    private CheckBox loopCheck;
    @FXML
    private CheckBox preserveRatioCheck;

    @FXML
    private Label statusLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label bufferLabel;
    @FXML
    private Label cycleLabel;

    private MediaPlayer player;
    private Path tempCopy;
    private boolean updatingSlider;
    private int finishedCycles;

    @FXML
    private void initialize() {
        mediaView.setFitHeight(270);
        mediaView.preserveRatioProperty().bind(preserveRatioCheck.selectedProperty());
        mediaView.setOnError(event -> showError(event.getMediaError()));
        placeholder.managedProperty().bind(placeholder.visibleProperty());

        volumeLabel.textProperty().bind(volumeSlider.valueProperty().multiply(100).asString("Volume: %.0f %%"));
        rateLabel.textProperty().bind(rateSlider.valueProperty().asString("Rate: %.2f×"));

        seekSlider.setMin(0);
        seekSlider.setMax(1);
        seekSlider.setDisable(true);
        stopButton.setDisable(true);
        seekSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!updatingSlider && player != null) {
                player.seek(Duration.seconds(newValue.doubleValue()));
            }
        });
        // Prepare the player right away (without autoplay): the first frame is shown and the first click on Play
        // starts playback immediately instead of racing against the still loading media.
        ensurePlayer();
    }

    @FXML
    private void onPlayPause() {
        if (!ensurePlayer()) {
            return;
        }
        switch (player.getStatus()) {
            case PLAYING -> player.pause();
            case HALTED, DISPOSED -> showError(player.getError());
            default -> player.play();
        }
    }

    @FXML
    private void onStop() {
        if (player != null) {
            player.stop();
        }
    }

    /** Creates the media player on first use. Returns {@code false} if the video could not be loaded. */
    private boolean ensurePlayer() {
        if (player != null) {
            return true;
        }
        Media media;
        try {
            media = new Media(resolveMediaUri());
            player = new MediaPlayer(media);
        } catch (IOException | RuntimeException e) {
            showError(e);
            return false;
        }
        MediaPlayer newPlayer = player;
        media.setOnError(() -> showError(media.getError()));
        newPlayer.setOnError(() -> showError(newPlayer.getError()));

        player.volumeProperty().bind(volumeSlider.valueProperty());
        player.muteProperty().bind(muteToggle.selectedProperty());
        player.rateProperty().bind(rateSlider.valueProperty());
        player.cycleCountProperty().bind(
                loopCheck.selectedProperty().map(loop -> loop ? MediaPlayer.INDEFINITE : 1));

        player.setOnReady(() -> {
            Duration total = media.getDuration();
            seekSlider.setMax(Math.max(total.toSeconds(), 0.1));
            seekSlider.setDisable(false);
            stopButton.setDisable(false);
            placeholder.setVisible(false);
            durationLabel.setText("Duration: " + format(total));
            sizeLabel.setText("Video size: " + media.getWidth() + " × " + media.getHeight() + " px");
            updateTime(newPlayer.getCurrentTime());
            mediaOutput.setText("Loaded " + media.getSource());
        });
        player.statusProperty().addListener((obs, oldStatus, newStatus) ->
                playButton.setText(newStatus == Status.PLAYING ? "Pause" : "Play"));
        statusLabel.textProperty().bind(player.statusProperty().asString("Status: %s"));
        player.currentTimeProperty().addListener((obs, oldTime, newTime) -> updateTime(newTime));
        player.bufferProgressTimeProperty().addListener((obs, oldTime, newTime) ->
                bufferLabel.setText("Buffered: " + format(newTime)));
        player.setOnRepeat(() -> {
            finishedCycles++;
            cycleLabel.setText("Finished cycles: " + finishedCycles);
        });
        player.setOnEndOfMedia(() -> {
            finishedCycles++;
            cycleLabel.setText("Finished cycles: " + finishedCycles);
            newPlayer.stop();
        });

        mediaView.setMediaPlayer(player);
        placeholder.setText("Loading…");
        return true;
    }

    private void updateTime(Duration currentTime) {
        if (player == null || currentTime == null) {
            return;
        }
        if (!seekSlider.isValueChanging()) {
            updatingSlider = true;
            try {
                seekSlider.setValue(currentTime.toSeconds());
            } finally {
                updatingSlider = false;
            }
        }
        timeLabel.setText(format(currentTime) + " / " + format(player.getTotalDuration()));
    }

    /**
     * Media only understands file:, jar: and http(s): URLs. Inside a jlink image the resource has a jrt: URL, so it
     * is copied to a temporary file first.
     */
    private String resolveMediaUri() throws IOException {
        URL url = MediaViewController.class.getResource(VIDEO);
        if (url == null) {
            throw new IOException("Video resource not found: " + VIDEO);
        }
        if (MEDIA_PROTOCOLS.contains(url.getProtocol())) {
            return url.toExternalForm();
        }
        if (tempCopy == null) {
            Path copy = Files.createTempFile("javafx-gallery-", ".mp4");
            copy.toFile().deleteOnExit();
            try (InputStream in = url.openStream()) {
                Files.copy(in, copy, StandardCopyOption.REPLACE_EXISTING);
            }
            tempCopy = copy;
        }
        return tempCopy.toUri().toString();
    }

    private void showError(Throwable error) {
        String message = error == null ? "unknown error"
                : error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
        mediaOutput.setText("Media error: " + message);
        placeholder.setText("The video could not be played.");
        placeholder.setVisible(true);
    }

    private static String format(Duration duration) {
        if (duration == null || duration.isUnknown() || duration.isIndefinite()) {
            return "–:––";
        }
        long seconds = (long) Math.floor(duration.toSeconds());
        return "%d:%02d".formatted(seconds / 60, seconds % 60);
    }

    @Override
    public void dispose() {
        if (player != null) {
            statusLabel.textProperty().unbind();
            player.volumeProperty().unbind();
            player.muteProperty().unbind();
            player.rateProperty().unbind();
            player.cycleCountProperty().unbind();
            mediaView.setMediaPlayer(null);
            player.dispose();
            player = null;
        }
        if (tempCopy != null) {
            try {
                Files.deleteIfExists(tempCopy);
            } catch (IOException e) {
                // still in use – deleteOnExit() takes care of it
            }
            tempCopy = null;
        }
    }
}
