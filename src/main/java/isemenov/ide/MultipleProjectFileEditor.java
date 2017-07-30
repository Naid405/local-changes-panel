package isemenov.ide;

import isemenov.ide.event.ConcurrentEventManager;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.editor.EditorFileChangedEvent;
import isemenov.ide.event.editor.EditorFileClosedEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;
import isemenov.ide.event.editor.EditorFileSavedEvent;

import javax.swing.text.StyledEditorKit;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class MultipleProjectFileEditor {
    private final Map<ProjectFile, DocumentEditor> openedFiles;

    private final EventManager eventManager;

    public MultipleProjectFileEditor() {
        this.openedFiles = new ConcurrentHashMap<>();
        this.eventManager = new ConcurrentEventManager();
    }

    public boolean hasOpenFiles() {
        return !openedFiles.isEmpty();
    }

    public boolean isFileOpen(ProjectFile file) {
        Objects.requireNonNull(file);
        return openedFiles.containsKey(file);
    }

    public void openFile(ProjectFile file) {
        Objects.requireNonNull(file);

        if (openedFiles.containsKey(file))
            return;

        DocumentEditor documentEditor = new DocumentEditor(new StyledEditorKit(), file);

        if (openedFiles.putIfAbsent(file, documentEditor) != documentEditor) {
            this.eventManager.fireEventListeners(this, new EditorFileOpenedEvent(file, documentEditor));
            documentEditor.addDocumentChangedListener(
                    (documentChangedEvent) -> eventManager.fireEventListeners(this, new EditorFileChangedEvent(
                            file,
                            documentEditor
                    )));
        }
    }

    public void readOpenedFileContent(ProjectFile file) {
        Objects.requireNonNull(file);

        DocumentEditor editor = openedFiles.get(file);
        if (editor == null)
            return;

        try {
            editor.readDocument();
        } catch (IOException e) {
            throw new ProjectFileReadingException(file, e);
        }
    }

    public void saveFile(ProjectFile file) {
        Objects.requireNonNull(file);

        DocumentEditor editor = openedFiles.get(file);
        if (editor == null)
            return;

        try {
            editor.writeDocument();
        } catch (IOException e) {
            throw new ProjectFileSavingException(file, e);
        }
        eventManager.fireEventListeners(this, new EditorFileSavedEvent(file, editor));
    }

    public void closeFile(ProjectFile file) {
        Objects.requireNonNull(file);

        DocumentEditor closed = openedFiles.remove(file);
        if (closed != null)
            this.eventManager.fireEventListeners(this, new EditorFileClosedEvent(file, closed));
    }


    public void addFileOpenedListener(Consumer<EditorFileOpenedEvent> listener) {
        Objects.requireNonNull(listener);
        eventManager.addEventListener(EditorFileOpenedEvent.class, listener);
    }

    public void addFileClosedListener(Consumer<EditorFileClosedEvent> listener) {
        Objects.requireNonNull(listener);
        eventManager.addEventListener(EditorFileClosedEvent.class, listener);
    }

    public void addFileChangedListener(Consumer<EditorFileChangedEvent> listener) {
        Objects.requireNonNull(listener);
        eventManager.addEventListener(EditorFileChangedEvent.class, listener);
    }

    public void addFileSavedListener(Consumer<EditorFileSavedEvent> listener) {
        Objects.requireNonNull(listener);
        eventManager.addEventListener(EditorFileSavedEvent.class, listener);
    }
}
