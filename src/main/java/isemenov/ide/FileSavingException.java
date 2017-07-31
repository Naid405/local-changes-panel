package isemenov.ide;

import java.nio.file.Path;

public class FileSavingException extends Exception {
    public FileSavingException(Path filePath, Throwable cause) {
        super("Failed to save file " + filePath.toString(), cause);
    }
}
