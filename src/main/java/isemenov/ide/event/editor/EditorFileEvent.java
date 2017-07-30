package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;
import isemenov.ide.event.ide.IDEEvent;

import java.nio.file.Path;

public abstract class EditorFileEvent extends IDEEvent {
    private final Path filePath;
    private final DocumentEditor documentEditor;

    public EditorFileEvent(Path filePath, DocumentEditor documentEditor) {
        this.filePath = filePath;
        this.documentEditor = documentEditor;
    }

    public Path getFile() {
        return filePath;
    }

    public DocumentEditor getDocumentEditor() {
        return documentEditor;
    }
}
