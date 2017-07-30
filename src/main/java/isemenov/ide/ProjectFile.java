package isemenov.ide;

import java.nio.file.Path;
import java.util.Objects;

public final class ProjectFile {
    private final Path filePath;
    private final boolean isDirectory;

    public ProjectFile(Path filePath, boolean isDirectory) {
        Objects.requireNonNull(filePath);
        this.filePath = filePath;
        this.isDirectory = isDirectory;
    }

    public Path getFilePath() {
        return filePath;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectFile that = (ProjectFile) o;
        return isDirectory == that.isDirectory &&
                Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, isDirectory);
    }

    @Override
    public String toString() {
        return filePath.getFileName().toString();
    }
}
