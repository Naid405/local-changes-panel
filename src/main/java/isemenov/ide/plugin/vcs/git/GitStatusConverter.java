package isemenov.ide.plugin.vcs.git;

import isemenov.ide.plugin.vcs.VCSFileStatus;

/**
 * Utility class providing mapping from Git status to abstract VCS status used by application
 */
public final class GitStatusConverter {
    private GitStatusConverter() {
    }

    //region Git help
    /*
    X          Y     Meaning
    -------------------------------------------------
              [MD]   not updated
    M        [ MD]   updated in index
    A        [ MD]   added to index
    D         [ M]   deleted from index
    R        [ MD]   renamed in index
    C        [ MD]   copied in index
    [MARC]           index and work tree matches
    [ MARC]     M    work tree changed since index
    [ MARC]     D    deleted in work tree
    -------------------------------------------------
    D           D    unmerged, both deleted
    A           U    unmerged, added by us
    U           D    unmerged, deleted by them
    U           A    unmerged, added by them
    D           U    unmerged, deleted by us
    A           A    unmerged, both added
    U           U    unmerged, both modified
    -------------------------------------------------
    ?           ?    untracked
    !           !    ignored
    -------------------------------------------------
     */
    //endregion

    public static VCSFileStatus convert(Character indexStatus, Character workTreeStatus) {
        String combinedStatus = indexStatus.toString() + workTreeStatus.toString();
        switch (combinedStatus) {
            case "  ":
                return VCSFileStatus.UNCHANGED;
            case "DD":
                return VCSFileStatus.CONFLICTING;
            case "AU":
                return VCSFileStatus.CONFLICTING;
            case "UD":
                return VCSFileStatus.CONFLICTING;
            case "UA":
                return VCSFileStatus.CONFLICTING;
            case "DU":
                return VCSFileStatus.CONFLICTING;
            case "AA":
                return VCSFileStatus.CONFLICTING;
            case "UU":
                return VCSFileStatus.CONFLICTING;
            case "??":
                return VCSFileStatus.UNTRACKED;
            case "!!":
                return VCSFileStatus.IGNORED;
            default:
                switch (indexStatus) {
                    case 'M':
                        return VCSFileStatus.MODIFIED;
                    case 'A':
                        return VCSFileStatus.NEW;
                    case 'D':
                        return VCSFileStatus.DELETED;
                    case 'R':
                        return VCSFileStatus.NEW;
                    case 'C':
                        return VCSFileStatus.NEW;
                    default:
                        switch (workTreeStatus) {
                            case 'M':
                                return VCSFileStatus.MODIFIED;
                            case 'D':
                                return VCSFileStatus.DELETED;
                            case ' ':

                            default:
                                return VCSFileStatus.UNKNOWN;
                        }
                }
        }
    }
}
