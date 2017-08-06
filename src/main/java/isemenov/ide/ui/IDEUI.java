package isemenov.ide.ui;

import isemenov.ide.IDE;
import isemenov.ide.MultipleFileEditor;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.core.AllFilesPossiblyChangedEvent;
import isemenov.ide.event.core.FilesPossiblyChangedEvent;
import isemenov.ide.event.editor.EditorFileClosedEvent;
import isemenov.ide.event.editor.EditorFileEditedStateChangeEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;
import isemenov.ide.event.project.ProjectFileListChangedEvent;
import isemenov.ide.event.vcs.VCSTrackingListChangedEvent;
import isemenov.ide.ui.action.CommonFileActionsFactory;
import isemenov.ide.ui.action.OpenProjectAction;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class IDEUI extends JFrame {
    private final IDE ide;

    private JPanel mainPanel;
    private JPanel projectViewPanel;
    private JPanel fileStatusTrackerPanel;
    private JPanel fileEditorPanel;

    private ProjectViewUI projectViewUI;
    private FileVCSStatusTrackerUI fileStatusTrackerUI;
    private TabbedFileEditorUI fileEditorUI;

    public IDEUI(IDE ide) {
        super("My cool IDE with VCS integration");
        this.ide = ide;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(500, 500));
        setSize(new Dimension(1000, 700));
    }

    public void showLoading() {
        SwingUtilities.invokeLater(() -> {
            JPanel loadingPanel = new JPanel(new BorderLayout());
            loadingPanel.add(new JLabel("Loading...", SwingConstants.CENTER), BorderLayout.CENTER);
            setContentPane(loadingPanel);
            revalidate();
        });
    }

    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            EventManager globalEventManager = ide.getGlobalIdeEventManager();

            MultipleFileEditor editor = ide.getFileEditor();
            fileEditorUI = new TabbedFileEditorUI(editor);
            fileEditorPanel.add(fileEditorUI.$$$getRootComponent$$$());
            globalEventManager.addEventListener(EditorFileOpenedEvent.class,
                                                e -> fileEditorUI.createEditorTab(e.getDocumentEditor()));
            globalEventManager.addEventListener(EditorFileClosedEvent.class,
                                                e -> fileEditorUI.closeEditorTabForFile(e.getFile()));
            globalEventManager.addEventListener(FilesPossiblyChangedEvent.class,
                                                e -> {
                                                    for (Path path : e.getFiles()) {
                                                        fileEditorUI.refreshFileContent(path);
                                                    }
                                                });
            globalEventManager.addEventListener(AllFilesPossiblyChangedEvent.class,
                                                e -> fileEditorUI.refreshAllFilesContent());

            CommonFileActionsFactory fileActionsFactory = new CommonFileActionsFactory(ide.getProject());
            projectViewUI = new ProjectViewUI(ide.getProject(), fileActionsFactory, fileEditorUI);
            projectViewPanel.add(projectViewUI.$$$getRootComponent$$$());
            globalEventManager.addEventListener(ProjectFileListChangedEvent.class,
                                                e -> projectViewUI
                                                        .updateFileTree(e.getRemovedFiles(), e.getNewFiles()));

            ide.getFileStatusTracker().ifPresent(tracker -> {
                fileStatusTrackerUI = new FileVCSStatusTrackerUI(fileActionsFactory, tracker);
                tracker.getEventManager().addEventListener(VCSTrackingListChangedEvent.class,
                                                           e -> fileStatusTrackerUI.updateFileTrackingList(
                                                                   e.getAddedFiles(),
                                                                   e.getChangedFiles(),
                                                                   e.getRemovedFiles()
                                                           ));
                globalEventManager.addEventListener(EditorFileEditedStateChangeEvent.class,
                                                    e -> fileStatusTrackerUI
                                                            .updateEditedStatusForFile(e.getFile(), e.isEdited()));
                fileStatusTrackerPanel.add(fileStatusTrackerUI.$$$getRootComponent$$$());
            });

            JMenuBar applicationMenu = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            fileMenu.add(new JMenuItem(new OpenProjectAction(this, ide)));
            fileMenu.addSeparator();
            fileMenu.add(fileEditorUI.getSaveCurrentFileAction());
            applicationMenu.add(fileMenu);
            setJMenuBar(applicationMenu);
            setContentPane(IDEUI.this.$$$getRootComponent$$$());
            revalidate();
        });
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
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(400);
        splitPane1.setOrientation(0);
        mainPanel.add(splitPane1, BorderLayout.CENTER);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane1.setLeftComponent(splitPane2);
        projectViewPanel = new JPanel();
        projectViewPanel.setLayout(new BorderLayout(0, 0));
        splitPane2.setLeftComponent(projectViewPanel);
        fileEditorPanel = new JPanel();
        fileEditorPanel.setLayout(new BorderLayout(0, 0));
        splitPane2.setRightComponent(fileEditorPanel);
        fileStatusTrackerPanel = new JPanel();
        fileStatusTrackerPanel.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(fileStatusTrackerPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
