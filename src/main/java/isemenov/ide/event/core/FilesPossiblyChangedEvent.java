package isemenov.ide.event.core;

import isemenov.ide.event.IDEEvent;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class FilesPossiblyChangedEvent extends IDEEvent {
    private final Set<Path> files;

    public FilesPossiblyChangedEvent(Set<Path> files) {
        this.files = files;
    }

    public Set<Path> getFiles() {
        return Collections.unmodifiableSet(files);
    }
}
