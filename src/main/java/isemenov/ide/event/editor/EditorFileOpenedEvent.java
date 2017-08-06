package isemenov.ide.event.editor;

import isemenov.ide.FileEditor;

import java.nio.file.Path;

public class EditorFileOpenedEvent extends EditorFileEvent {
    private final FileEditor documentEditor;

    public EditorFileOpenedEvent(Path filePath, FileEditor documentEditor) {
        super(filePath);
        this.documentEditor = documentEditor;
    }

    public FileEditor getDocumentEditor() {
        return documentEditor;
    }
}
