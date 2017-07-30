package isemenov.ide.plugin.vcs.git;

import isemenov.ide.plugin.vcs.VCSException;

public class MalformedGitStatusLineException extends VCSException {
    public MalformedGitStatusLineException(String statusLine) {
        super("Result of git status is not in correct format: " + statusLine);
    }
}
