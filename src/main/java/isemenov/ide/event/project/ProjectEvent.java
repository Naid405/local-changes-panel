package isemenov.ide.event.project;

import isemenov.ide.Project;
import isemenov.ide.event.ide.IDEEvent;

public abstract class ProjectEvent extends IDEEvent {
    private final Project project;

    public ProjectEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
