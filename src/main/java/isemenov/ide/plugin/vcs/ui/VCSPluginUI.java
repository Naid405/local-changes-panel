package isemenov.ide.plugin.vcs.ui;

import isemenov.ide.plugin.PluginUI;
import isemenov.ide.plugin.vcs.ProjectFileVCSStatus;
import isemenov.ide.plugin.vcs.VCSIntegrationPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class VCSPluginUI implements PluginUI {
    private static final Logger logger = LogManager.getLogger(VCSPluginUI.class);

    private final VCSIntegrationPlugin integrationPlugin;

    private JPanel mainPanel;
    private JList<ProjectFileVCSStatus> fileList;
    private JButton refreshButton;

    public VCSPluginUI(VCSIntegrationPlugin integrationPlugin) {
        this.integrationPlugin = integrationPlugin;

        refreshButton.addActionListener(e -> refreshAllFiles());

        fileList.setModel(integrationPlugin.getFileStatusesList());
        //So that it is selected via right click too
        fileList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JList list = (JList) e.getSource();
                    int row = list.locationToIndex(e.getPoint());
                    list.setSelectedIndex(row);
                }
            }
        });

        VCSFileListItemPopupMenu listItemPopupMenu =
                new VCSFileListItemPopupMenu(e -> deleteSelectedFile(),
                                             e -> revertSelectedFile(),
                                             e -> refreshSelectedFile());

        fileList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    if (fileList.getSelectedIndex() >= 0)
                        listItemPopupMenu.filePopup();
                    else
                        listItemPopupMenu.nonFilePopup();
                    listItemPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void refreshAllFiles() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    integrationPlugin.refreshAllFileStatuses();
                } catch (Exception e) {
                    logger.warn("Error occured while refreshing VCS status for tracked files", e);
                }
                return null;
            }
        }.execute();
    }

    private void deleteSelectedFile() {
        ProjectFileVCSStatus file = fileList.getSelectedValue();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    integrationPlugin.removeFile(file.getFile());
                    integrationPlugin.refreshFileStatus(file.getFile());
                } catch (Exception e) {
                    logger.warn("Error occured while removing file " + file.getFile(),
                                e);
                }
                return null;
            }
        }.execute();
    }

    private void revertSelectedFile() {
        ProjectFileVCSStatus file = fileList.getSelectedValue();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    integrationPlugin.revertFileChanges(file.getFile());
                    integrationPlugin.refreshFileStatus(file.getFile());
                } catch (Exception e) {
                    logger.warn("Error occured while reverting changes for file " + file.getFile(),
                                e);
                }
                return null;
            }
        }.execute();
    }

    private void refreshSelectedFile() {
        ProjectFileVCSStatus file = fileList.getSelectedValue();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    integrationPlugin.refreshFileStatus(file.getFile());
                } catch (Exception e) {
                    logger.warn("Error occured while refreshing status for file " + file.getFile(),
                                e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public WindowFocusListener getWindowFocusListener() {
        return new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                refreshAllFiles();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        };
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        toolBar1.setOrientation(1);
        toolBar1.setRollover(true);
        toolBar1.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        mainPanel.add(toolBar1, BorderLayout.WEST);
        refreshButton = new JButton();
        refreshButton.setFocusable(false);
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh-button.png")));
        refreshButton.setText("");
        refreshButton.setToolTipText("Refresh");
        toolBar1.add(refreshButton);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(0, 0));
        mainPanel.add(scrollPane1, BorderLayout.CENTER);
        fileList = new JList();
        fileList.setSelectionMode(0);
        scrollPane1.setViewportView(fileList);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
