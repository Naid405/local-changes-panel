package isemenov.ide.ui.action;

import isemenov.ide.IDE;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.UnorderedEventManager;
import isemenov.ide.event.core.LoadingCompletedEvent;
import isemenov.ide.event.core.LoadingStartedEvent;
import isemenov.ide.ui.IDEUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;

public class OpenProjectAction extends AbstractAction {
    private final boolean initial;
    private final JFrame frame;
    private final IDE currentIde;

    public OpenProjectAction() {
        super("Open Project");
        this.currentIde = null;
        this.initial = true;
        this.frame = new JFrame("temp");
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public OpenProjectAction(IDEUI currentUI, IDE currentIde) {
        super("Open Project");
        this.initial = false;
        this.currentIde = currentIde;
        this.frame = currentUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select project directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file == null)
                return;

            if (!file.isDirectory())
                JOptionPane.showMessageDialog(frame,
                                              "Please select directory",
                                              "Not a directory",
                                              JOptionPane.ERROR_MESSAGE);

            Path projectPath = file.toPath().normalize();

            if (currentIde != null && currentIde.isCurrentlyOpenProject(projectPath))
                return;

            EventManager globalEventManager = new UnorderedEventManager();
            IDE ide = new IDE(projectPath, globalEventManager);
            IDEUI ui = new IDEUI(ide);
            globalEventManager.addEventListener(LoadingStartedEvent.class, event -> ui.showLoading());
            globalEventManager.addEventListener(LoadingCompletedEvent.class, event -> ui.initialize());

            //Close previous IDE instance
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            ui.setVisible(true);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    try {
                        ide.start();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ui,
                                                      e.getMessage(),
                                                      "Failed to open project",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                    return null;
                }
            }.execute();
        } else {
            if (initial)
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }
}
