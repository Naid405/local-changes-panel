package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.UnorderedEventManager;
import isemenov.ide.event.ide.ProjectChangedEvent;
import isemenov.ide.plugin.IDEPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class IDE {
    private final EventManager eventManager;
    private final List<IDEPlugin> plugins;

    private volatile Project project;

    public IDE(List<IDEPlugin> plugins) {
        this.eventManager = new UnorderedEventManager();
        this.plugins = plugins != null ? plugins : new ArrayList<>();
        for (IDEPlugin plugin : this.plugins) {
            this.addProjectChangedListener(e -> plugin.setProject(e.getProject()));
        }
    }

    public boolean isCurrentlyOpenProject(File projectDirectoryPath) {
        Objects.requireNonNull(projectDirectoryPath);

        Path rootPath = projectDirectoryPath.toPath().normalize();
        return this.project != null && this.project.getProjectDirectoryPath().compareTo(rootPath) == 0;
    }

    public void openProject(File projectDirectoryPath) throws IOException {
        Objects.requireNonNull(projectDirectoryPath);

        Path rootPath = projectDirectoryPath.toPath().normalize();

        if (!projectDirectoryPath.isDirectory())
            throw new NotADirectoryException(rootPath);

        MultipleProjectFileEditor editor = new MultipleProjectFileEditor();

        this.project = new Project(rootPath, editor);
        this.project.readFileTree();

        for (IDEPlugin plugin : plugins) {
            this.project.addProjectFileEventListener(plugin.getEditorEventsListener());
        }

        if (!Thread.currentThread().isInterrupted())
            this.eventManager.fireEventListeners(this, new ProjectChangedEvent(project));
    }

    public List<IDEPlugin> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    public void addProjectChangedListener(Consumer<ProjectChangedEvent> listener) {
        Objects.requireNonNull(listener);
        this.eventManager.addEventListener(ProjectChangedEvent.class, listener);
    }
}
