package isemenov.ide.plugin;

import isemenov.ide.Project;
import isemenov.ide.event.ide.editor.ProjectFileEventsListener;

public interface IDEPlugin {
    String getShortName();

    ProjectFileEventsListener getEditorEventsListener();

    void setProject(Project project);
}
