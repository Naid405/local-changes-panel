package isemenov.ide.plugin.vcs;

import java.nio.file.Path;

public class NotVCSRootException extends VCSException {
    public NotVCSRootException(Path path) {
        super(path.toAbsolutePath().toString() + " is not a VCS root");
    }
}
