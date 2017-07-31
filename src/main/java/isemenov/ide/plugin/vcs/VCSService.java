package isemenov.ide.plugin.vcs;

import java.nio.file.Path;
import java.util.Map;

public interface VCSService {
    Map<Path, VCSFileStatus> getStatus() throws VCSException;

    VCSFileStatus getStatus(Path filePath) throws VCSException;

    void removeFile(Path filePath) throws VCSException;

    void revertFileChanges(Path filePath) throws VCSException;
}
