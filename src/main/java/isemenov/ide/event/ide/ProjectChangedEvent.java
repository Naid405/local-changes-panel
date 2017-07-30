package isemenov.ide.event.ide;

import isemenov.ide.Project;

public class ProjectChangedEvent extends IDEEvent {
    private Project project;

    public ProjectChangedEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
