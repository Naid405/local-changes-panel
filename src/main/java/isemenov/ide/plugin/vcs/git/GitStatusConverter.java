package isemenov.ide.plugin.vcs.git;

import isemenov.ide.plugin.vcs.VCSFileStatus;

public final class GitStatusConverter {
    private GitStatusConverter() {
    }

    //region Git help
    /*
    ' ' = unmodified
    M = modified
    A = added
    D = deleted
    R = renamed
    C = copied
    U = updated but unmerged
    */

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
        switch (workTreeStatus) {
            case 'M':
                return VCSFileStatus.MODIFIED;
            case 'D':
                return VCSFileStatus.DELETED;
            case '?':
                return VCSFileStatus.UNTRACKED;
            case '!':
                return VCSFileStatus.IGNORED;
            case ' ':
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
                        return VCSFileStatus.UNKNOWN;
                }
            default:
                return VCSFileStatus.UNKNOWN;
        }
    }
}
