package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.OrderedEventManager;
import isemenov.ide.event.core.AllFilesPossiblyChangedEvent;
import isemenov.ide.event.core.FilesPossiblyChangedEvent;
import isemenov.ide.event.core.LoadingCompletedEvent;
import isemenov.ide.event.core.LoadingStartedEvent;
import isemenov.ide.event.editor.EditorFileEditedStateChangeEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;
import isemenov.ide.event.project.ProjectFileListChangedEvent;
import isemenov.ide.vcs.StaticVCSPluginRegistry;
import isemenov.ide.vcs.VCSException;
import isemenov.ide.vcs.VCSFileStatusTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static isemenov.ide.vcs.StaticVCSPluginRegistry.VCS_NAME;

public class IDE {
    private final static Logger logger = LogManager.getLogger(IDE.class);

    private final EventManager globalIdeEventManager;
    private final StaticVCSPluginRegistry vcsPluginRegistry;
    private final Path projectPath;

    private volatile boolean started;

    private volatile Project project;
    private volatile MultipleFileEditor fileEditor;
    private volatile VCSFileStatusTracker fileStatusTracker;

    public IDE(Path projectPath, EventManager globalIdeEventManager, StaticVCSPluginRegistry vcsPluginRegistry) {
        Objects.requireNonNull(projectPath);
        Objects.requireNonNull(globalIdeEventManager);
        Objects.requireNonNull(vcsPluginRegistry);

        this.projectPath = projectPath;
        this.globalIdeEventManager = globalIdeEventManager;
        this.vcsPluginRegistry = vcsPluginRegistry;
        this.started = false;
    }

    public void start() throws FileTreeReadingException {
        globalIdeEventManager.fireEventListeners(this, new LoadingStartedEvent());
        project = new Project(projectPath, globalIdeEventManager);
        project.readFileTree();
        globalIdeEventManager.addEventListener(FilesPossiblyChangedEvent.class, e -> {
            try {
                project.readFileTree();
            } catch (FileTreeReadingException e1) {
                logger.error(e1.getMessage(), e1);
            }
        });
        globalIdeEventManager.addEventListener(AllFilesPossiblyChangedEvent.class, e -> {
            try {
                project.readFileTree();
            } catch (FileTreeReadingException e1) {
                logger.error(e1.getMessage(), e1);
            }
        });

        fileEditor = new MultipleFileEditor(globalIdeEventManager);
        globalIdeEventManager.addEventListener(ProjectFileListChangedEvent.class, e -> {
            for (Path path : e.getRemovedFiles()) {
                fileEditor.closeOpenedFile(path);
            }
        });

        vcsPluginRegistry.getBundleForVCS(VCS_NAME).ifPresent(bundle -> {
            try {
                fileStatusTracker = new VCSFileStatusTracker(VCS_NAME, project, bundle.getServiceFactory(),
                                                             new OrderedEventManager(), globalIdeEventManager);
                globalIdeEventManager.addEventListener(EditorFileOpenedEvent.class,
                                                       e -> fileStatusTracker.startTrackingFile(e.getFile()));
                globalIdeEventManager.addEventListener(EditorFileEditedStateChangeEvent.class,
                                                       e -> fileStatusTracker.refreshFileStatus(e.getFile()));
                globalIdeEventManager.addEventListener(FilesPossiblyChangedEvent.class,
                                                       e -> fileStatusTracker.refreshTrackedFileStatuses(e.getFiles()));
                globalIdeEventManager.addEventListener(ProjectFileListChangedEvent.class,
                                                       e -> fileStatusTracker.checkRemovedFiles(e.getRemovedFiles()));
            } catch (VCSException e) {
                logger.warn("Could not initialize vcs file status tracker", e);
            }
        });

        started = true;
        globalIdeEventManager.fireEventListeners(this, new LoadingCompletedEvent());
    }

    public EventManager getGlobalIdeEventManager() {
        return globalIdeEventManager;
    }

    public StaticVCSPluginRegistry getVcsPluginRegistry() {
        return vcsPluginRegistry;
    }

    public boolean isCurrentlyOpenProject(Path projectDirectoryPath) {
        Objects.requireNonNull(projectDirectoryPath);
        return getProject().getProjectDirectoryPath().compareTo(projectDirectoryPath) == 0;
    }

    public Project getProject() {
        if (!started)
            throw new IDENotStartedException();
        return project;
    }

    public MultipleFileEditor getFileEditor() {
        if (!started)
            throw new IDENotStartedException();
        return fileEditor;
    }

    //Project may not be bound to VCS
    public Optional<VCSFileStatusTracker> getFileStatusTracker() {
        if (!started)
            throw new IDENotStartedException();
        return Optional.ofNullable(fileStatusTracker);
    }
}
