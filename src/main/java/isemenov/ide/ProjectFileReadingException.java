package isemenov.ide;

public class ProjectFileReadingException extends RuntimeException {
    public ProjectFileReadingException(ProjectFile file, Throwable cause) {
        super("Failed to read file " + file.getFilePath().toString(), cause);
    }
}
