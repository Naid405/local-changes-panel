package isemenov.ide;

import isemenov.ide.event.ConcurrentEventManager;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.ide.editor.EditorFileChangedEvent;
import isemenov.ide.event.ide.editor.EditorFileClosedEvent;
import isemenov.ide.event.ide.editor.EditorFileOpenedEvent;
import isemenov.ide.event.ide.editor.EditorFileSavedEvent;

import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FileEditor {
    private final ErrorHandler errorHandler;
    private final EventManager eventManager;

    private final Map<Path, DocumentEditor> openedFiles;
    private final DefaultTreeModel fileTreeModel;

    public FileEditor(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
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

    /**
     * Open file in editor
     *
     * @param file file to be opened
     * @return true in file was opened, false if file was already open
     */
    public boolean openFile(Path file) {
        Objects.requireNonNull(file);
        // Only need to check it here, since all other methods operate only with opened files
        if (file.toFile().isDirectory())
            throw new IllegalArgumentException("Cannot open directory in editor");

        if (openedFiles.containsKey(file))
            return false;

        DocumentEditor documentEditor = new DocumentEditor(new StyledEditorKit(), file.toAbsolutePath());

        if (openedFiles.putIfAbsent(file, documentEditor) != documentEditor) {
            this.eventManager.fireEventListeners(this, new EditorFileOpenedEvent(file, documentEditor));
            documentEditor.addDocumentChangedListener(
                    (documentChangedEvent) -> eventManager.fireEventListeners(this, new EditorFileChangedEvent(
                            file,
                            documentEditor
                    )));
            return true;
        } else {
            return false;
        }
    }

    public Optional<DocumentEditor> getEditorForFile(Path filePath) {
        return Optional.ofNullable(openedFiles.get(filePath));
    }

    public void readOpenedFileContent(Path file) {
        Objects.requireNonNull(file);

        DocumentEditor editor = openedFiles.get(file);
        if (editor == null)
            return;

        try {
            editor.readDocument();
        } catch (IOException e) {
            errorHandler.error(new FileReadingException(file, e));
            closeOpenedFile(file);
        }
    }

    public void saveOpenedFileContent(Path file) {
        Objects.requireNonNull(file);

        DocumentEditor editor = openedFiles.get(file);
        if (editor == null)
            return;

        try {
            editor.writeDocument();
            eventManager.fireEventListeners(this, new EditorFileSavedEvent(file, editor));
        } catch (IOException e) {
            errorHandler.error(new FileSavingException(file, e));
        }
    }

    public void closeOpenedFile(Path file) {
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
