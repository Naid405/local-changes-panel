package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;

import java.nio.file.Path;

public class EditorFileClosedEvent extends EditorFileEvent {
    public EditorFileClosedEvent(Path filePath, DocumentEditor documentEditor) {
        super(filePath, documentEditor);
    }
}
