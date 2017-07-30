package isemenov.ide.event.editor;

import isemenov.ide.ProjectFile;

public interface ProjectFileEventsListener {
    void projectFileOpened(ProjectFile file);

    void projectFileClosed(ProjectFile file);

    void projectFileChanged(ProjectFile file);

    void projectFileSaved(ProjectFile file);
}
