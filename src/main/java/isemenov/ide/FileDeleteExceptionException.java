package isemenov.ide;

import java.io.IOException;
import java.nio.file.Path;

public class FileDeleteExceptionException extends Exception {
    public FileDeleteExceptionException(Path path, IOException e) {
        super("Cannot delete file " + path, e);
    }
}
