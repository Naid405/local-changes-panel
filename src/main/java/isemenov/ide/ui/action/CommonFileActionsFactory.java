package isemenov.ide.ui.action;

import isemenov.ide.CannotDeleteFilesException;
import isemenov.ide.FileTreeReadingException;
import isemenov.ide.Project;
import isemenov.ide.ui.ErrorHandlerUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommonFileActionsFactory {
    private final Project project;

    public CommonFileActionsFactory(Project project) {
        this.project = project;
    }

    public List<Action> getActions(Path file) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            project.deleteFile(file);
                        } catch (CannotDeleteFilesException | FileTreeReadingException ex) {
                            ErrorHandlerUI.showError(ex);
                        }
                        return null;
                    }
                }.execute();

            }
        });
        return actions;
    }
}
