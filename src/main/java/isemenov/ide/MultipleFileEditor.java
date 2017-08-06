package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.editor.EditorFileClosedEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MultipleFileEditor {
    private final EventManager globalEventManager;

    private final ConcurrentMap<Path, FileEditor> openedFiles;

    public MultipleFileEditor(EventManager globalEventManager) {
        Objects.requireNonNull(globalEventManager);
        this.globalEventManager = globalEventManager;
        this.openedFiles = new ConcurrentHashMap<>();
    }

    public boolean hasOpenFiles() {
        return !openedFiles.isEmpty();
    }

    /**
     * Open file in editor
     *
     * @param file to open
     * @return true in file was opened, false if file was already open
     */
    public boolean openFile(Path file) {
        Objects.requireNonNull(file);
        if (file.toFile().isDirectory())
            throw new IllegalArgumentException("Cannot open directory in editor");

        if (openedFiles.containsKey(file))
            return false;

        FileEditor editor = new FileEditor(file.toAbsolutePath(), globalEventManager);
        return openedFiles.computeIfAbsent(file, path -> {
            globalEventManager.fireEventListeners(this, new EditorFileOpenedEvent(file, editor));
            return editor;
        }) == editor;
    }

    /**
     * Close file if it is open
     *
     * @param file to close
     */
    public void closeOpenedFile(Path file) {
        Objects.requireNonNull(file);
        openedFiles.computeIfPresent(file, (path, editor) -> {
            editor.setEditedState(false);
            this.globalEventManager.fireEventListeners(this, new EditorFileClosedEvent(file));
            return null;
        });
    }
}
