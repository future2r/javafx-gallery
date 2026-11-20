package name.ulbricht.javafx.gallery.pages.status;

import javafx.concurrent.Task;

/** Simulates work by sleeping in small steps and reporting the progress. */
final class FakeWorkTask extends Task<Void> {

    private static final int STEPS = 100;

    @Override
    protected Void call() throws Exception {
        updateMessage("Preparing …");
        for (int i = 1; i <= STEPS; i++) {
            if (isCancelled()) {
                updateMessage("Cancelled at " + (i - 1) + " %.");
                return null;
            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                if (isCancelled()) {
                    updateMessage("Cancelled at " + (i - 1) + " %.");
                    return null;
                }
                throw e;
            }
            updateProgress(i, STEPS);
            updateMessage("Processing item " + i + " of " + STEPS);
        }
        updateMessage("Done.");
        return null;
    }
}
