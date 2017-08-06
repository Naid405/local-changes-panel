package isemenov.ide.event.project;

import isemenov.ide.event.IDEEvent;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ProjectFileListChangedEvent extends IDEEvent {
    private final LinkedHashMap<Path, Boolean> newFiles;
    private final Set<Path> removedFiles;

    /**
     * Create event
     *
     * @param newFiles     map of "filePath, isDirectory", must be ordered by path depth
     * @param removedFiles
     */
    public ProjectFileListChangedEvent(LinkedHashMap<Path, Boolean> newFiles, Set<Path> removedFiles) {
        this.newFiles = newFiles;
        this.removedFiles = removedFiles;
    }

    public Map<Path, Boolean> getNewFiles() {
        return Collections.unmodifiableMap(newFiles);
    }

    public Set<Path> getRemovedFiles() {
        return Collections.unmodifiableSet(removedFiles);
    }
}
