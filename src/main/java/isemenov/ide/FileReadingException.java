package isemenov.ide;

import java.nio.file.Path;

public class FileReadingException extends Exception {
    public FileReadingException(Path filePath, Throwable cause) {
        super("Failed to read file " + filePath.toString(), cause);
    }
}
