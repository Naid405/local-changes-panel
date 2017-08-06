package isemenov.ide.vcs;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Service for core VCS functionality
 */
public interface VCSService {
    VCSFileStatus getStatus(Path filePath) throws VCSException;

    Map<Path, VCSFileStatus> getStatuses(Set<Path> paths) throws VCSException;

    /**
     * Check that file exist in VCS
     *
     * @param file file to check
     * @return true if exists
     * @throws VCSException if something went wrong
     */
    boolean checkExists(Path file) throws VCSException;
}
