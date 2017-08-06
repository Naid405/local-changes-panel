package isemenov.ide.ui;

import isemenov.ide.FileTreeReadingException;
import isemenov.ide.Project;
import isemenov.ide.ui.action.CommonFileActionsFactory;
import isemenov.ide.ui.action.JTreeNodeDoubleClickMouseAdapter;
import isemenov.ide.ui.component.FileTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class ProjectViewUI {
    private final DefaultTreeModel treeModel;
    private final Map<Path, FileTreeNode> pathTreeNodeMapping;
    private JPanel mainPanel;
    private JButton refreshTreeButton;
    private JTree fileTree;

    public ProjectViewUI(Project project, CommonFileActionsFactory fileActionsFactory,
                         TabbedFileEditorUI fileEditorUI) {
        refreshTreeButton.setAction(new AbstractAction("Refresh file list") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            project.readFileTree();
                        } catch (FileTreeReadingException ex) {
                            ErrorHandlerUI.showError(ex);
                        }
                        return null;
                    }
                }.execute();
            }
        });

        fileTree.setShowsRootHandles(true);

        FileTreeNode root = new FileTreeNode(project.getProjectDirectoryPath(), true);
        treeModel = new DefaultTreeModel(root,
                                         true);
        fileTree.setModel(treeModel);

        pathTreeNodeMapping = new HashMap<>();
        pathTreeNodeMapping.put(project.getProjectDirectoryPath(), root);
        handleNewFiles(project.getProjectFiles());

        fileTree.addMouseListener(new JTreeNodeDoubleClickMouseAdapter(treeNode -> {
            DefaultMutableTreeNode fileTreeNode = (DefaultMutableTreeNode) treeNode;

            if (fileTreeNode == null || fileTreeNode.getAllowsChildren()) return;
            fileEditorUI.getOpenFileAction((Path) fileTreeNode.getUserObject()).actionPerformed(null);
        }));

        fileTree.addMouseListener(new JTreeNodeDoubleClickMouseAdapter(treeNode -> {
            DefaultMutableTreeNode fileTreeNode = (DefaultMutableTreeNode) treeNode;

            if (fileTreeNode == null || fileTreeNode.getAllowsChildren()) return;
            fileEditorUI.getOpenFileAction((Path) fileTreeNode.getUserObject()).actionPerformed(null);
        }));
    }

    public void updateFileTree(Set<Path> removedFiles, Map<Path, Boolean> newFiles) {
        SwingUtilities.invokeLater(() -> {
            handleRemovedFiles(removedFiles);
            handleNewFiles(newFiles);
        });
    }

    private void handleRemovedFiles(Set<Path> files) {
        Map<FileTreeNode, Map<FileTreeNode, Integer>> removedNodes = new HashMap<>();
        for (Path path : files) {
            FileTreeNode node = pathTreeNodeMapping.get(path);
            FileTreeNode parent = pathTreeNodeMapping.get(path.getParent());
            int index = parent.getIndex(node);
            if (index >= 0) {
                parent.remove(index);
                removedNodes.computeIfAbsent(parent, (k) -> new HashMap<>()).putIfAbsent(node, index);
            }
        }
        for (Map.Entry<FileTreeNode, Map<FileTreeNode, Integer>> entry : removedNodes.entrySet()) {
            FileTreeNode parent = entry.getKey();
            int[] childrenIndices = entry.getValue().values().stream().mapToInt(i -> i).toArray();
            Object[] children = entry.getValue().keySet().toArray();
            treeModel.nodesWereRemoved(parent, childrenIndices, children);
        }
    }

    private void handleNewFiles(Map<Path, Boolean> fileMap) {
        Map<FileTreeNode, List<Integer>> changedNodes = new HashMap<>();
        for (Map.Entry<Path, Boolean> entry : fileMap.entrySet()) {
            Path path = entry.getKey();
            FileTreeNode node = new FileTreeNode(entry.getKey(), entry.getValue());
            FileTreeNode parent = pathTreeNodeMapping.get(path.getParent());
            parent.add(node);

            pathTreeNodeMapping.put(path, node);
            changedNodes.computeIfAbsent(parent, (k) -> new ArrayList<>()).add(parent.getChildCount() - 1);
        }
        for (Map.Entry<FileTreeNode, List<Integer>> entry : changedNodes.entrySet()) {
            int[] childrenIndices = entry.getValue().stream().mapToInt(i -> i).toArray();
            treeModel.nodesWereInserted(entry.getKey(), childrenIndices);
        }
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
        mainPanel.add(toolBar1, BorderLayout.NORTH);
        refreshTreeButton = new JButton();
        refreshTreeButton.setFocusable(false);
        refreshTreeButton.setText("Refresh File Tree");
        toolBar1.add(refreshTreeButton);
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, BorderLayout.CENTER);
        fileTree = new JTree();
        scrollPane1.setViewportView(fileTree);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
