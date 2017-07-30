package isemenov.ide.plugin.vcs.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class VCSFileListItemPopupMenu extends JPopupMenu {
    private final JMenuItem deleteMenuItem;
    private final JMenuItem revertMenuItem;
    private final JMenuItem refreshMenuItem;

    public VCSFileListItemPopupMenu(ActionListener deleteSelectedFileActionListener,
                                    ActionListener revertSelectedFileActionListener,
                                    ActionListener refreshSelectedFileActionListener) {
        super("VCS operations");
        deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(deleteSelectedFileActionListener);
        this.add(deleteMenuItem);

        revertMenuItem = new JMenuItem("Revert");
        revertMenuItem.addActionListener(revertSelectedFileActionListener);
        this.add(revertMenuItem);

        refreshMenuItem = new JMenuItem("Refresh");
        refreshMenuItem.addActionListener(refreshSelectedFileActionListener);
        this.add(revertMenuItem);
    }

    public void filePopup() {
        deleteMenuItem.setEnabled(true);
        revertMenuItem.setEnabled(true);
        refreshMenuItem.setEnabled(true);
    }

    public void nonFilePopup() {
        deleteMenuItem.setEnabled(false);
        revertMenuItem.setEnabled(false);
        refreshMenuItem.setEnabled(false);
    }
}
