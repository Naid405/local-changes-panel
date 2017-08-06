package isemenov.ide.vcs.git.ui;

import isemenov.ide.ui.ErrorHandlerUI;
import isemenov.ide.ui.IDEUI;
import isemenov.ide.vcs.VCSException;
import isemenov.ide.vcs.VCSFileStatus;
import isemenov.ide.vcs.VCSUIActionFactory;
import isemenov.ide.vcs.git.GitService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GitActionProvider implements VCSUIActionFactory {
    private final GitService service;

    public GitActionProvider(GitService service) {
        this.service = service;
    }

    @Override
    public List<Action> getCommonActions() {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new AbstractAction(null, new ImageIcon(this.getClass().getResource("/icons/upload-button.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Commit
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
