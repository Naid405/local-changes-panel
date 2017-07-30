package isemenov.ide;

public class ProjectFileSavingException extends RuntimeException {
    public ProjectFileSavingException(ProjectFile file, Throwable cause) {
        super("Failed to save file " + file.getFilePath().toString(), cause);
    }
}
