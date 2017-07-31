package isemenov.ide.plugin.vcs;

import isemenov.ide.ErrorHandler;
import isemenov.ide.Project;
import isemenov.ide.event.ide.editor.ProjectFileEventsListener;
import isemenov.ide.plugin.IDEPlugin;

import java.nio.file.Path;
import java.util.Objects;

public class VCSIntegrationPlugin implements IDEPlugin {
    private final ErrorHandler errorHandler;

    private final VCSServiceFactory vcsServiceFactory;
    private final VSCFileStatusesList fileStatusesList;

    private volatile boolean changingProject;

    private VCSService vcsService;
    private Project project;

    public VCSIntegrationPlugin(ErrorHandler errorHandler, VCSServiceFactory vcsServiceFactory) {
        this.errorHandler = errorHandler;
        Objects.requireNonNull(vcsServiceFactory);

        this.vcsServiceFactory = vcsServiceFactory;
        this.fileStatusesList = new VSCFileStatusesList();
        this.changingProject = false;
    }

    public void setProject(Project project) {
        try {
            changingProject = true;
            fileStatusesList.clear();
            this.project = project;
            this.vcsService = null;
            try {
                this.vcsService = vcsServiceFactory.getServiceForProject(project);
            } catch (NotVCSRootException e) {
                errorHandler.warn(e);
            }
        } finally {
            changingProject = false;
        }
    }

    private boolean isPluginInactive() {
        return changingProject || vcsService == null;
    }

    public VSCFileStatusesList getTrackedFileStatusesList() {
        return fileStatusesList;
    }

    public void startTrackingFile(Path file) {
        if (isPluginInactive())
            return;

        try {
            fileStatusesList.addProjectFileStatus(file,
                                                  vcsService.getStatus(file),
                                                  false);
        } catch (VCSException e) {
            errorHandler.error(e);
        }
    }

    public void updateProject() {
        if (isPluginInactive())
            return;

        project.refreshProjectFiles();
    }

    public void commitTrackedFiles() {
        if (isPluginInactive())
            return;

        refreshTrackedFileStatuses();
    }

    public void updateUnsavedStatusForTrackedFile(Path file, boolean unsaved) {
        if (isPluginInactive())
            return;

        try {
            fileStatusesList.updateUnsavedStatusForFile(file, unsaved);
            fileStatusesList.updateVCSStatusForFile(file,
                                                    vcsService.getStatus(file));
        } catch (VCSException e) {
            errorHandler.error(e);
        }
    }

    public void refreshTrackedFileStatuses() {
        if (isPluginInactive())
            return;

        try {
            fileStatusesList.updateVCSStatusesForFiles(
                    vcsService.getStatus());
        } catch (VCSException e) {
            errorHandler.error(e);
        }
    }

    public void refreshFileStatus(Path file) {
        if (isPluginInactive())
            return;

        try {
            fileStatusesList.updateVCSStatusForFile(file,
                                                    vcsService.getStatus(file));
        } catch (VCSException e) {
            errorHandler.error(e);
        }
    }

    public void removeFile(Path file) {
        if (isPluginInactive())
            return;

        try {
            vcsService.removeFile(file);
            project.refreshProjectFiles();
        } catch (VCSException e) {
            errorHandler.error(e);
        }
    }

    public void revertFileChanges(Path file) {
        if (isPluginInactive())
            return;

        try {
            vcsService.revertFileChanges(file);
            project.refreshProjectFiles();
            project.getFileEditor().readOpenedFileContent(file);
            refreshFileStatus(file);
        } catch (VCSException e) {
            errorHandler.error(e);
        }
    }

    @Override
    public String getShortName() {
        return "Version Control";
    }

    @Override
    public ProjectFileEventsListener getEditorEventsListener() {
        return new ProjectFileEventsListener() {
            @Override
            public void projectFileOpened(Path file) {
                startTrackingFile(file);
            }

            @Override
            public void projectFileClosed(Path file) {
                updateUnsavedStatusForTrackedFile(file, false);
            }

            @Override
            public void projectFileChanged(Path file) {
                updateUnsavedStatusForTrackedFile(file, true);
            }

            @Override
            public void projectFileSaved(Path file) {
                updateUnsavedStatusForTrackedFile(file, false);
            }
        };
    }
}
