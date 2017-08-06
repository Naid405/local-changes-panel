package isemenov.ide.vcs.git;

import isemenov.ide.vcs.VCSException;

public class NoTrackedBranchException extends VCSException {
    public NoTrackedBranchException(String branch) {
        super("Branch " + branch + " is not tracking any remote branch");
    }
}
