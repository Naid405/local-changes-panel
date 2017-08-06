package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.editor.EditorFileClosedEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;

import java.nio.file.Path;
import java.util.*;

public class MultipleFileEditor {
    private final EventManager globalEventManager;

    private final Map<Path, FileEditor> openedFiles;

    public MultipleFileEditor(EventManager globalEventManager) {
        this.globalEventManager = globalEventManager;
        this.openedFiles = new HashMap<>();
    }

    public boolean hasOpenFiles() {
        return !openedFiles.isEmpty();
    }

    /**
     * Open file in editor
     *
     * @param file file to be opened
     * @return true in file was opened, false if file was already open
     */
    public synchronized boolean openFile(Path file) {
        Objects.requireNonNull(file);
        if (file.toFile().isDirectory())
            throw new IllegalArgumentException("Cannot open directory in editor");

        if (openedFiles.containsKey(file))
            return false;

        FileEditor editor = new FileEditor(file.toAbsolutePath(), globalEventManager);

        if (openedFiles.putIfAbsent(file, editor) != editor) {
            globalEventManager.fireEventListeners(this, new EditorFileOpenedEvent(file, editor));
            return true;
        } else {
            return false;
        }
    }

    public synchronized void closeOpenedFile(Path file) {
        Objects.requireNonNull(file);

        FileEditor editor = openedFiles.remove(file);
        if (editor != null) {
            editor.setEditedState(false);
            this.globalEventManager.fireEventListeners(this, new EditorFileClosedEvent(file));
        }
    }
}
