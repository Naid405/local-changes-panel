package isemenov.ide.event.ide.editor;

import java.nio.file.Path;

public interface ProjectFileEventsListener {
    void projectFileOpened(Path file);

    void projectFileClosed(Path file);

    void projectFileChanged(Path file);

    void projectFileSaved(Path file);
}
