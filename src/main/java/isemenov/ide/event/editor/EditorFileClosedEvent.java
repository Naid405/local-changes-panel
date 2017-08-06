package isemenov.ide.event.editor;

import isemenov.ide.FileEditor;

import java.nio.file.Path;

public class EditorFileClosedEvent extends EditorFileEvent {
    public EditorFileClosedEvent(Path filePath) {
        super(filePath);
    }
}
