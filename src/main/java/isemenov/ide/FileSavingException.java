package isemenov.ide;

import java.nio.file.Path;

public class FileSavingException extends RuntimeException {
    public FileSavingException(Path filePath, Throwable cause) {
        super("Failed to save file " + filePath.toString(), cause);
    }
}
