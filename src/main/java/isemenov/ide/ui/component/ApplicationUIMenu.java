package isemenov.ide.ui.component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ApplicationUIMenu extends JMenuBar {
    private JMenuItem openProjectMenuItem;
    private JMenuItem saveFileMenuItem;

    public ApplicationUIMenu() {
        super();

        JMenu fileMenu = new JMenu("File");

        fileMenu.add(openProjectMenuItem = new JMenuItem("Open project"));
        openProjectMenuItem.setEnabled(false);

        fileMenu.addSeparator();

        fileMenu.add(saveFileMenuItem = new JMenuItem("Save file"));
        saveFileMenuItem.setEnabled(false);

        this.add(fileMenu);
    }

    public void setOpenProjectMenuItemActionListener(ActionListener listener) {
        openProjectMenuItem.setAction(new AbstractAction("Open project") {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        });
        openProjectMenuItem.setEnabled(true);
    }

    public void setSaveFileMenuItemActionListener(ActionListener listener) {
        saveFileMenuItem.setAction(new AbstractAction("Save file") {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        });
        saveFileMenuItem.setEnabled(true);
    }
}
