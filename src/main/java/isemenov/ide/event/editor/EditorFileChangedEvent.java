package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;

import java.nio.file.Path;

public class EditorFileChangedEvent extends EditorFileEvent {
    public EditorFileChangedEvent(Path filePath, DocumentEditor documentEditor) {
        super(filePath, documentEditor);
    }
}
