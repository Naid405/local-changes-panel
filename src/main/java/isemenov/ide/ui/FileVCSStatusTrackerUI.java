package isemenov.ide.ui;

import isemenov.ide.ui.action.CommonFileActionsFactory;
import isemenov.ide.ui.component.FileVCSStatus;
import isemenov.ide.ui.component.VCSFileListItemPopupMenu;
import isemenov.ide.ui.component.VSCFileStatusesList;
import isemenov.ide.vcs.StaticVCSPluginRegistry;
import isemenov.ide.vcs.VCSFileStatus;
import isemenov.ide.vcs.VCSFileStatusTracker;
import isemenov.ide.vcs.VCSUIActionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class FileVCSStatusTrackerUI {
    private final VCSFileStatusTracker tracker;

    private JPanel mainPanel;
    private JList<FileVCSStatus> fileList;
    private JButton refreshButton;
    private JToolBar toolbar;

    private VSCFileStatusesList fileStatusesList;

    public void updateFileTrackingList(Map<Path, VCSFileStatus> newFiles, Map<Path, VCSFileStatus> changedFiles,
                                       Set<Path> removedFiles) {
        SwingUtilities.invokeLater(() -> {
            fileStatusesList.removeFiles(removedFiles);
            fileStatusesList.addFiles(newFiles);
            fileStatusesList.updateVCSStatusesForFiles(changedFiles);
        });
    }

    public void updateEditedStatusForFile(Path file, boolean edited) {
        SwingUtilities.invokeLater(() -> fileStatusesList.updateEditedStatusForFile(file, edited));
    }

    public WindowFocusListener getWindowsFocusListener() {
        return new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        tracker.refreshAllTrackedFileStatuses();
                        return null;
                    }
                }.execute();
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

    public FileVCSStatusTrackerUI(CommonFileActionsFactory commonFileActionsFactory,
                                  StaticVCSPluginRegistry vcsPluginRegistry,
                                  VCSFileStatusTracker tracker) {
        this.tracker = tracker;
        this.fileStatusesList = new VSCFileStatusesList();
        fileList.setModel(fileStatusesList);
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

        //Getting directly because at this point we already got the bundle to construct tracker
        @SuppressWarnings("ConstantConditions")
        VCSUIActionFactory actionsFactory = vcsPluginRegistry.getBundleForVCS(tracker.getVcsName())
                                                             .get()
                                                             .getActionsFactory(tracker.getVcsService());

        fileList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    FileVCSStatus fileVCSStatus = fileList.getSelectedValue();
                    new VCSFileListItemPopupMenu(
                            commonFileActionsFactory.getActions(fileVCSStatus.getFile()),
                            actionsFactory.getFileActions(
                                    fileVCSStatus.getFile(),
                                    fileVCSStatus.getVcsFileStatus()
                            )).show(fileList, e.getX(), e.getY());
                }
            }
        });
        refreshButton.addActionListener(e -> new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                tracker.refreshAllTrackedFileStatuses();
                return null;
            }
        }.execute());
        for (Action action : actionsFactory.getCommonActions(tracker)) {
            toolbar.add(new JButton(action));
        }
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
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setOrientation(1);
        toolbar.setRollover(true);
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        mainPanel.add(toolbar, BorderLayout.WEST);
        refreshButton = new JButton();
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh-button.png")));
        refreshButton.setText("");
        toolbar.add(refreshButton);
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
