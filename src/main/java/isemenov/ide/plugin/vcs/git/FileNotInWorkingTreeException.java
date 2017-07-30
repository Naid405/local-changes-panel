package isemenov.ide.plugin.vcs.git;

import isemenov.ide.plugin.vcs.VCSException;

import java.nio.file.Path;

public class FileNotInWorkingTreeException extends VCSException {
    public FileNotInWorkingTreeException(Path workTreePath, Path filePath) {
        super(filePath.toAbsolutePath().toString() + " does not belong to work tree " + workTreePath.toAbsolutePath().toString());
    }
}
