package isemenov.ide.event.editor;

import isemenov.ide.DocumentEditor;

import java.nio.file.Path;

public class EditorFileSavedEvent extends EditorFileEvent {
    public EditorFileSavedEvent(Path filePath, DocumentEditor documentEditor) {
        super(filePath, documentEditor);
    }
}
