package isemenov.ide.event.project;

import isemenov.ide.Project;
import isemenov.ide.ProjectFile;

import java.util.Collections;
import java.util.Set;

public class ProjectFileTreeChangedEvent extends ProjectEvent {
    private final Set<ProjectFile> newFiles;
    private final Set<ProjectFile> removedFiles;

    public ProjectFileTreeChangedEvent(Project project,
                                       Set<ProjectFile> newFiles,
                                       Set<ProjectFile> removedFiles) {
        super(project);
        this.newFiles = newFiles;
        this.removedFiles = removedFiles;
    }

    public Set<ProjectFile> getNewFiles() {
        return Collections.unmodifiableSet(newFiles);
    }

    public Set<ProjectFile> getRemovedFiles() {
        return Collections.unmodifiableSet(removedFiles);
    }
}
