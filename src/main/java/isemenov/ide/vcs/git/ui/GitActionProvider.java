package isemenov.ide.vcs.git.ui;

import isemenov.ide.ui.ErrorHandlerUI;
import isemenov.ide.vcs.VCSException;
import isemenov.ide.vcs.VCSFileStatus;
import isemenov.ide.vcs.VCSFileStatusTracker;
import isemenov.ide.vcs.VCSUIActionFactory;
import isemenov.ide.vcs.git.GitService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitActionProvider implements VCSUIActionFactory {
    private final GitService service;

    public GitActionProvider(GitService service) {
        this.service = service;
    }

    @Override
    public List<Action> getCommonActions(VCSFileStatusTracker tracker) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new AbstractAction(null, new ImageIcon(this.getClass().getResource("/icons/upload-button.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<Path, VCSFileStatus> trackedFiles = tracker.getTrackedFiles();
                Map<Path, VCSFileStatus> filesToCommit = new HashMap<>();
                for (Map.Entry<Path, VCSFileStatus> entry : trackedFiles.entrySet()) {
                    if (entry.getValue() != VCSFileStatus.UNCHANGED
                            && entry.getValue() != VCSFileStatus.UNTRACKED
                            && entry.getValue() != VCSFileStatus.IGNORED
                            && entry.getValue() != VCSFileStatus.UNKNOWN)
                        filesToCommit.put(entry.getKey(), entry.getValue());
                }
                if (filesToCommit.isEmpty()) {
                    ErrorHandlerUI.showWarning("Nothing to commit");
                    return;
                }
                GitCommitDialog commitDialog = new GitCommitDialog(service, filesToCommit);
                commitDialog.setVisible(true);
            }
        });
        actions.add(new AbstractAction(null, new ImageIcon(this.getClass().getResource("/icons/download-button.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                GitUpdateDialog updateDialog = new GitUpdateDialog(service);
                updateDialog.setVisible(true);
            }
        });
        return actions;
    }

    @Override
    public List<Action> getFileActions(Path file,
                                       VCSFileStatus fileStatus) {
        List<Action> actions = new ArrayList<>();

        AbstractAction revertAction = new AbstractAction("Revert") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            service.revertFileChanges(file);
                        } catch (VCSException e1) {
                            ErrorHandlerUI.showError(e1);
                        }
                        return null;
                    }
                }.execute();
            }
        };

        if (fileStatus == VCSFileStatus.UNKNOWN
                || fileStatus == VCSFileStatus.CONFLICTING
                || fileStatus == VCSFileStatus.UNTRACKED
                || fileStatus == VCSFileStatus.IGNORED) {
            revertAction.setEnabled(false);
        }

        actions.add(revertAction);
        return actions;
    }
}
