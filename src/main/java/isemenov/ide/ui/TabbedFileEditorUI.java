package isemenov.ide.ui;

import isemenov.ide.DocumentEditor;
import isemenov.ide.MultipleProjectFileEditor;
import isemenov.ide.ProjectFile;
import isemenov.ide.event.editor.EditorFileClosedEvent;
import isemenov.ide.event.editor.EditorFileOpenedEvent;
import isemenov.ide.ui.component.ApplicationUIMenu;
import isemenov.ide.ui.component.CloseableChangeDisplayingTab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

public class TabbedFileEditorUI {
    private static final Logger logger = LogManager.getLogger(TabbedFileEditorUI.class);

    private final MultipleProjectFileEditor fileEditor;

    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    public TabbedFileEditorUI(ApplicationUIMenu applicationMenu,
                              MultipleProjectFileEditor fileEditor) {
        this.fileEditor = fileEditor;

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
                                fileOpenedEvent.getProjectFile().toString(),
                                documentEditor.getEditorKit(),
                                documentEditor.getDocument(),
                                (e) -> new SwingWorker<Void, Void>() {
                                    @Override
                                    protected Void doInBackground() throws Exception {
                                        fileEditor.closeFile(fileOpenedEvent.getProjectFile());
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
        ProjectFile file = (ProjectFile) document.getProperty(Document.StreamDescriptionProperty);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    fileEditor.saveFile(file);
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
        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
