package isemenov.ide.event.vcs;

import isemenov.ide.vcs.VCSFileStatus;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class VCSTrackingListChangedEvent extends VCSTrackingEvent {
    private final Map<Path, VCSFileStatus> addedFiles;
    private final Map<Path, VCSFileStatus> changedFiles;
    private final Set<Path> removedFiles;

    public VCSTrackingListChangedEvent(
            Map<Path, VCSFileStatus> addedFiles,
            Map<Path, VCSFileStatus> changedFiles,
            Set<Path> removedFiles) {
        this.addedFiles = addedFiles;
        this.changedFiles = changedFiles;
        this.removedFiles = removedFiles;
    }

    public Map<Path, VCSFileStatus> getAddedFiles() {
        return Collections.unmodifiableMap(addedFiles);
    }

    public Map<Path, VCSFileStatus> getChangedFiles() {
        return Collections.unmodifiableMap(changedFiles);
    }

    public Set<Path> getRemovedFiles() {
        return Collections.unmodifiableSet(removedFiles);
    }
}
