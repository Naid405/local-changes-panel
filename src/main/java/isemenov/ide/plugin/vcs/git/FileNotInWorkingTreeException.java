package isemenov.ide.plugin.vcs.git;

import isemenov.ide.plugin.vcs.VCSException;

import java.nio.file.Path;

public class FileNotInWorkingTreeException extends VCSException {
    public FileNotInWorkingTreeException(Path filePath) {
    }
}
