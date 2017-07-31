package isemenov.ide;

import java.io.IOException;
import java.nio.file.Path;

public class FileTreeReadingException extends Exception {
    public FileTreeReadingException(Path projectDirectoryPath, IOException e) {
        super("Failed to read project file tree at " + projectDirectoryPath.toAbsolutePath().toString(), e);
    }
}
