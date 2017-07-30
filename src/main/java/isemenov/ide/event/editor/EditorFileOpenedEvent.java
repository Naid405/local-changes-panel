package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;

import java.nio.file.Path;

public class EditorFileOpenedEvent extends EditorFileEvent {
    public EditorFileOpenedEvent(Path filePath, DocumentEditor documentEditor) {
        super(filePath, documentEditor);
    }
}
