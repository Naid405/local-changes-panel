package isemenov.ide.plugin.vcs;

import isemenov.ide.Project;
import isemenov.ide.event.editor.ProjectFileEventsListener;
import isemenov.ide.plugin.IDEPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VCSIntegrationPlugin implements IDEPlugin {
    private static final Logger logger = LogManager.getLogger(VCSIntegrationPlugin.class);

    private final VCSServiceFactory vcsServiceFactory;
    private final VSCFileStatusesList fileStatusesList;
    private final ReadWriteLock projectChangeLock;
    private VCSService vcsService;
    private Project project;

    public VCSIntegrationPlugin(VCSServiceFactory vcsServiceFactory) {
        Objects.requireNonNull(vcsServiceFactory);

        this.vcsServiceFactory = vcsServiceFactory;
        this.fileStatusesList = new VSCFileStatusesList();
        this.projectChangeLock = new ReentrantReadWriteLock();
    }

    public void setProject(Project project) {
        projectChangeLock.writeLock().lock();
        try {
            fileStatusesList.clear();
            this.project = project;
            this.vcsService = null;
            try {
                this.vcsService = vcsServiceFactory.getServiceForProject(project);
            } catch (NotVCSRootException e) {
                logger.warn(e);
            }
        } finally {
            projectChangeLock.writeLock().unlock();
        }
    }

    public VSCFileStatusesList getFileStatusesList() {
        return fileStatusesList;
    }

    public void refreshAllFileStatuses() throws VCSException {
        projectChangeLock.readLock().lock();
        try {
            if (vcsService == null)
                return;

            fileStatusesList.updateVCSStatusesForPaths(vcsService.getStatus());
        } finally {
            this.projectChangeLock.readLock().unlock();
        }

    }

    public void refreshFileStatus(Path file) throws VCSException {
        projectChangeLock.readLock().lock();
        try {
            if (vcsService == null)
                return;

            fileStatusesList.updateVCSStatusForFile(file, vcsService.getStatus(file));
        } finally {
            projectChangeLock.readLock().unlock();
        }

    }

    public void removeFile(Path file) throws VCSException {
        projectChangeLock.readLock().lock();
        try {
            if (vcsService == null)
                return;

            project.getFileEditor().closeOpenedFile(file);
            vcsService.removeFile(file);
            project.refreshProjectFiles();
        } finally {
            projectChangeLock.readLock().unlock();
        }
    }

    public void revertFileChanges(Path file) throws VCSException {
        projectChangeLock.readLock().lock();
        try {
            if (vcsService == null)
                return;

            vcsService.revertFileChanges(file);
            project.refreshProjectFiles();
            project.getFileEditor().readOpenedFileContent(file);
        } finally {
            projectChangeLock.readLock().unlock();
        }
        this.refreshFileStatus(file);
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
                if (vcsService == null)
                    return;

                fileStatusesList.addProjectFileStatus(file, VCSFileStatus.UNKNOWN, false);
                try {
                    refreshFileStatus(file);
                } catch (VCSException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void projectFileClosed(Path file) {
                if (vcsService == null)
                    return;

                fileStatusesList.updateUnsavedStatusForFile(file, false);
                try {
                    refreshFileStatus(file);
                } catch (VCSException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void projectFileChanged(Path file) {
                if (vcsService == null)
                    return;

                fileStatusesList.updateUnsavedStatusForFile(file, true);
                try {
                    refreshFileStatus(file);
                } catch (VCSException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void projectFileSaved(Path file) {
                if (vcsService == null)
                    return;

                fileStatusesList.updateUnsavedStatusForFile(file, false);
                try {
                    refreshFileStatus(file);
                } catch (VCSException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
