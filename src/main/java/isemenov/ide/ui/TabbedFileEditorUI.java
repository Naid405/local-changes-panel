package isemenov.ide.ui;

import isemenov.ide.FileEditor;
import isemenov.ide.FileReadingException;
import isemenov.ide.MultipleFileEditor;
import isemenov.ide.ui.component.FileEditorTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.Objects;

public class TabbedFileEditorUI {
    private final MultipleFileEditor fileEditor;

    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private AbstractAction saveCurrentTabFileAction;

    public TabbedFileEditorUI(MultipleFileEditor fileEditor) {
        this.fileEditor = fileEditor;

        saveCurrentTabFileAction = new AbstractAction("Save file") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!fileEditor.hasOpenFiles())
                    return;
                FileEditorTab editorTab = (FileEditorTab) tabbedPane.getSelectedComponent();
                editorTab.getSaveFileAction().actionPerformed(e);
            }
        };
    }

    public void createEditorTab(FileEditor editor) {
        SwingUtilities.invokeLater(
                () -> {
                    FileEditorTab editorTab = new FileEditorTab(
                            editor,
                            (e) -> new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    fileEditor.closeOpenedFile(editor.getFilePath());
                                    return null;
                                }
                            }.execute());

                    try {
                        editorTab.readFile();

                        tabbedPane.addTab(editor.getFilePath().getFileName().toString(), editorTab);
                        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, editorTab.getTabHeader());
                        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                    } catch (FileReadingException e) {
                        ErrorHandlerUI.showError(e);
                    }
                }
        );
    }

    public void closeEditorTabForFile(Path filePath) {
        SwingUtilities.invokeLater(() -> {
            int index = this.indexOfFileTab(filePath);
            if (index >= 0)
                tabbedPane.removeTabAt(index);
        });
    }

    private int indexOfFileTab(Path filePath) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (Objects.equals(filePath, ((FileEditorTab) tabbedPane.getComponentAt(i)).getFilePath()))
                return i;
        }
        return -1;
    }

    public void refreshFileContent(Path file) {
        SwingUtilities.invokeLater(() -> {
            int index = indexOfFileTab(file);
            if (index >= 0) {
                FileEditorTab editorTab = (FileEditorTab) tabbedPane.getComponentAt(index);
                try {
                    editorTab.readFile();
                } catch (FileReadingException ex) {
                    ErrorHandlerUI.showError(ex);
                }
            }
        });
    }

    public void refreshAllFilesContent() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                FileEditorTab editorTab = (FileEditorTab) tabbedPane.getComponentAt(i);
                try {
                    editorTab.readFile();
                } catch (FileReadingException ex) {
                    ErrorHandlerUI.showError(ex);
                }
            }
        });
    }

    public Action getOpenFileAction(Path filePath) {
        return new AbstractAction("Open file") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        if (!fileEditor.openFile(filePath)) {
                            SwingUtilities.invokeLater(() -> tabbedPane.setSelectedIndex(
                                    indexOfFileTab(filePath)));
                        }
                        return null;
                    }
                }.execute();
            }
        };
    }

    public Action getSaveCurrentFileAction() {
        return saveCurrentTabFileAction;
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
