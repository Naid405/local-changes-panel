package isemenov.ide;

import java.nio.file.Path;

public class FileReadingException extends RuntimeException {
    public FileReadingException(Path filePath, Throwable cause) {
        super("Failed to read file " + filePath.toString(), cause);
    }
}
