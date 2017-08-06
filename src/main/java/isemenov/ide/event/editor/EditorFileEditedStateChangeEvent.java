package isemenov.ide.event.editor;

import java.nio.file.Path;

public class EditorFileEditedStateChangeEvent extends EditorFileEvent {
    private final boolean edited;

    public EditorFileEditedStateChangeEvent(Path filePath, boolean edited) {
        super(filePath);
        this.edited = edited;
    }

    public boolean isEdited() {
        return edited;
    }
}
