package isemenov.ide.event.editor;

import isemenov.ide.event.IDEEvent;

import java.nio.file.Path;

public abstract class EditorFileEvent extends IDEEvent {
    private final Path filePath;

    public EditorFileEvent(Path filePath) {
        this.filePath = filePath;
    }

    public Path getFile() {
        return filePath;
    }
}
