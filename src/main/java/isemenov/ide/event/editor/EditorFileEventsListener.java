package isemenov.ide.event.editor;

import java.nio.file.Path;

/**
 * Convenience interface to subscribe to all file events at once
 */
public interface EditorFileEventsListener {
    default void fileOpened(Path file) {
    }

    default void fileClosed(Path file) {
    }

    default void fileEditedStatusChanged(Path file, Boolean edited) {
    }
}
