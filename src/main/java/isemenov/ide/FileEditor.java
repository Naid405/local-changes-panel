package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.editor.EditorFileEditedStateChangeEvent;

import java.nio.file.Path;

/**
 * "Utility" class to incorporate UI-bound editor into IDE without tying it to specific UI implementation
 */
public class FileEditor {
    private final EventManager globalEventManager;
    private final Path filePath;

    /**
     * Create editor for file
     *
     * @param filePath           path to file
     * @param globalEventManager local event manager to handle model-ui events and export file edit events
     */
    public FileEditor(Path filePath, EventManager globalEventManager) {
        this.filePath = filePath;
        this.globalEventManager = globalEventManager;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setEditedState(Boolean edited) {
        globalEventManager.fireEventListeners(this, new EditorFileEditedStateChangeEvent(filePath, edited));
    }
}
