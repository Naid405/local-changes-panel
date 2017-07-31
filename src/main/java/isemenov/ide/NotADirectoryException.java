package isemenov.ide;

import java.nio.file.Path;

public class NotADirectoryException extends Exception {
    public NotADirectoryException(Path path) {
        super(path.toAbsolutePath() + " is not a directory");
    }
}
