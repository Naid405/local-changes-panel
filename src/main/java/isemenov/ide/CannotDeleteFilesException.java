package isemenov.ide;

import java.nio.file.Path;
import java.util.Set;

public class CannotDeleteFilesException extends Exception {
    public CannotDeleteFilesException(Set<Path> paths) {
        super("Cannot delete files " + paths);
    }
}
