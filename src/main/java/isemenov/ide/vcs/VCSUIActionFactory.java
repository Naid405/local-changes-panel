package isemenov.ide.vcs;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Should provide actions that will be added to UI
 */
public interface VCSUIActionFactory {
    /**
     * Actions for left toolbar
     *
     * @return list of actions
     */
    List<Action> getCommonActions();

    /**
     * Actions for context menu
     *
     * @param file file for which to produce actions
     * @return list of actions available for file
     */
    List<Action> getFileActions(Path file,
                                VCSFileStatus fileStatus);
}
