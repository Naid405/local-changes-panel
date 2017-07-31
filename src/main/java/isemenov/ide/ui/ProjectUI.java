package isemenov.ide.ui;

import isemenov.ide.Project;
import isemenov.ide.ui.component.ApplicationUIMenu;

import javax.swing.*;
import java.awt.*;

public class ProjectUI {
    private JPanel mainPanel;
    private JButton refreshTreeButton;

    public ProjectUI(ApplicationUIMenu applicationMenu, Project project) {
        TabbedFileEditorUI editorView = new TabbedFileEditorUI(applicationMenu, project.getFileEditor());
        mainPanel.add(editorView.$$$getRootComponent$$$(), BorderLayout.CENTER);
        refreshTreeButton.addActionListener(e -> project.refreshProjectFiles());
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
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
