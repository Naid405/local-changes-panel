package isemenov.ide.ui;

import isemenov.ide.DocumentEditor;
import isemenov.ide.FileEditor;
import isemenov.ide.FileReadingException;
import isemenov.ide.event.editor.EditorFileClosedEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;
import isemenov.ide.ui.action.JTreeNodeDoubleClickMouseAdapter;
import isemenov.ide.ui.component.ApplicationUIMenu;
import isemenov.ide.ui.component.CloseableChangeDisplayingTab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.Objects;

public class TabbedFileEditorUI {
    private static final Logger logger = LogManager.getLogger(TabbedFileEditorUI.class);

    private final FileEditor fileEditor;

    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JTree fileTree;

    public TabbedFileEditorUI(ApplicationUIMenu applicationMenu,
                              FileEditor fileEditor) {
        this.fileEditor = fileEditor;

        fileTree.addMouseListener(new JTreeNodeDoubleClickMouseAdapter(treeNode -> {
            DefaultMutableTreeNode fileTreeNode = (DefaultMutableTreeNode) treeNode;

            if (fileTreeNode == null || fileTreeNode.getAllowsChildren()) return;
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        Path filePath = (Path) fileTreeNode.getUserObject();
                        fileEditor.openFile(filePath);
                        fileEditor.readOpenedFileContent(filePath);
                    } catch (FileReadingException e) {
                        logger.error(e.getMessage(), e);
                    }
                    return null;
                }
            }.execute();
        }));
        fileTree.setModel(fileEditor.getFileTreeModel());

        fileEditor.addFileOpenedListener(this::handleFileEditorOpened);
        fileEditor.addFileClosedListener(this::handleFileEditorClosed);

        applicationMenu.setSaveFileMenuItemActionListener(e -> saveCurrentlyOpenFile());
    }

    private void handleFileEditorOpened(EditorFileOpenedEvent fileOpenedEvent) {
        DocumentEditor documentEditor = fileOpenedEvent.getDocumentEditor();

        documentEditor.addDocumentChangedListener(
                (event) ->
                        SwingUtilities.invokeLater(() -> {
                            int tabIndex = this.indexOfTabWithDocument(
                                    event.getDocument());
                            if (tabIndex >= 0) {
                                ((CloseableChangeDisplayingTab) tabbedPane
                                        .getTabComponentAt(tabIndex)).setChanged();
                            }
                        }));

        documentEditor.addDocumentBeingSavedListener(
                (event) ->
                        SwingUtilities.invokeLater(() -> {
                            int tabIndex = this.indexOfTabWithDocument(
                                    event.getDocument());
                            if (tabIndex >= 0)
                                ((JScrollPane) tabbedPane.getComponentAt(
                                        tabIndex)).getViewport().getView().setEnabled(
                                        false);
                        }));

        documentEditor.addDocumentSavedListener(
                (event) ->
                        SwingUtilities.invokeLater(() -> {
                            int tabIndex = this.indexOfTabWithDocument(
                                    event.getDocument());
                            if (tabIndex >= 0) {
                                ((CloseableChangeDisplayingTab) tabbedPane
                                        .getTabComponentAt(tabIndex)).setSaved();
                                ((JScrollPane) tabbedPane.getComponentAt(tabIndex))
                                        .getViewport().getView().setEnabled(true);
                            }
                        }));

        SwingUtilities.invokeLater(
                () ->
                        this.openEditorTab(
                                fileOpenedEvent.getFile().getFileName().toString(),
                                documentEditor.getEditorKit(),
                                documentEditor.getDocument(),
                                (e) -> new SwingWorker<Void, Void>() {
                                    @Override
                                    protected Void doInBackground() throws Exception {
                                        fileEditor.closeFile(fileOpenedEvent.getFile());
                                        return null;
                                    }
                                }.execute()
                        ));
    }

    private void handleFileEditorClosed(EditorFileClosedEvent fileClosedEvent) {
        SwingUtilities.invokeLater(() -> {
            int index = this.indexOfTabWithDocument(fileClosedEvent.getDocumentEditor().getDocument());
            if (index >= 0)
                tabbedPane.removeTabAt(index);
        });
    }

    public void saveCurrentlyOpenFile() {
        if (!fileEditor.hasOpenFiles())
            return;
        Document document = ((JTextPane) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView()).getDocument();
        Path file = (Path) document.getProperty(Document.StreamDescriptionProperty);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    fileEditor.saveOpenedFileContent(file);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return null;
            }
        }.execute();
    }

    public void openEditorTab(String title, EditorKit editorKit, Document document, ActionListener closeTabListener) {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(true);
        textPane.setEditorKit(editorKit);
        textPane.setDocument(document);
        tabbedPane.addTab(title, new JScrollPane(textPane));

        CloseableChangeDisplayingTab tabHeader = new CloseableChangeDisplayingTab(title);
        tabHeader.addCrossButtonActionListener(closeTabListener);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabHeader);

        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public int indexOfTabWithDocument(Document document) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (Objects.equals(document,
                               ((JTextPane) ((JScrollPane) tabbedPane.getComponentAt(
                                       i)).getViewport().getView()).getDocument()))
                return i;
        }
        return -1;
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
        mainPanel.add(splitPane1, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setPreferredSize(new Dimension(150, 0));
        splitPane1.setLeftComponent(panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, BorderLayout.CENTER);
        fileTree = new JTree();
        scrollPane1.setViewportView(fileTree);
        tabbedPane = new JTabbedPane();
        splitPane1.setRightComponent(tabbedPane);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
