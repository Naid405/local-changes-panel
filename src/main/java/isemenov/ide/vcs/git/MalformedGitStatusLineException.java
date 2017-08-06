package isemenov.ide.vcs.git;

import isemenov.ide.vcs.VCSException;

public class MalformedGitStatusLineException extends VCSException {
    public MalformedGitStatusLineException(String statusLine) {
        super("Result of git status is not in correct format: " + statusLine);
    }
}
