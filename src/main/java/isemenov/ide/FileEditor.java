package isemenov.ide;

import isemenov.ide.event.ConcurrentEventManager;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.editor.EditorFileChangedEvent;
import isemenov.ide.event.editor.EditorFileClosedEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;
import isemenov.ide.event.editor.EditorFileSavedEvent;

import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class FileEditor {
    private final Map<Path, DocumentEditor> openedFiles;
    private final DefaultTreeModel fileTreeModel;

    private final EventManager eventManager;

    public FileEditor() {
        this.openedFiles = new ConcurrentHashMap<>();
        this.fileTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(), true);
        this.eventManager = new ConcurrentEventManager();
    }

    public TreeModel getFileTreeModel() {
        return fileTreeModel;
    }

    public void setFileTree(DefaultMutableTreeNode treeRoot) {
        Objects.requireNonNull(treeRoot);
        fileTreeModel.setRoot(treeRoot);
    }

    public boolean hasOpenFiles() {
        return !openedFiles.isEmpty();
    }

    public void openFile(Path file) {
        Objects.requireNonNull(file);

        if (openedFiles.containsKey(file))
            return;

        DocumentEditor documentEditor = new DocumentEditor(new StyledEditorKit(), file.toAbsolutePath());

        if (openedFiles.putIfAbsent(file, documentEditor) != documentEditor) {
            this.eventManager.fireEventListeners(this, new EditorFileOpenedEvent(file, documentEditor));
            documentEditor.addDocumentChangedListener(
                    (documentChangedEvent) -> eventManager.fireEventListeners(this, new EditorFileChangedEvent(
                            file,
                            documentEditor
                    )));
        }
    }

    public void readOpenedFileContent(Path file) {
        Objects.requireNonNull(file);

        DocumentEditor editor = openedFiles.get(file);
        if (editor == null)
            return;

        try {
            editor.readDocument();
        } catch (IOException e) {
            throw new FileReadingException(file, e);
        }
    }

    public void saveFile(Path file) {
        Objects.requireNonNull(file);

        DocumentEditor editor = openedFiles.get(file);
        if (editor == null)
            return;

        try {
            editor.writeDocument();
        } catch (IOException e) {
            throw new FileSavingException(file, e);
        }
        eventManager.fireEventListeners(this, new EditorFileSavedEvent(file, editor));
    }

    public void closeFile(Path file) {
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
