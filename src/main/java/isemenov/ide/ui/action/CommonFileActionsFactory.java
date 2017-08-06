package isemenov.ide.ui.action;

import isemenov.ide.FileDeleteExceptionException;
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
                new SwingWorker<Void, Void>(){
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            project.deleteFile(file);
                        } catch (FileDeleteExceptionException e1) {
                            ErrorHandlerUI.showError(e1);
                        }
                        return null;
                    }
                }.execute();

            }
        });
        return actions;
    }
}
