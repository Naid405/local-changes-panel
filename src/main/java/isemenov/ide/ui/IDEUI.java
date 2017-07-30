package isemenov.ide.ui;

import isemenov.ide.IDE;
import isemenov.ide.plugin.IDEPlugin;
import isemenov.ide.plugin.PluginUIFactory;
import isemenov.ide.ui.component.ApplicationUIMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IDEUI {
    private static final Logger logger = LogManager.getLogger(IDEUI.class);

    private final IDE ide;
    private final List<WindowFocusListener> windowFocusListeners;
    private volatile SwingWorker lastInvokedOpenProjectWorker;
    private JPanel mainPanel;
    private JPanel projectPanel;
    private JTabbedPane pluginPane;
    private JFileChooser chooser = new JFileChooser();

    public IDEUI(ApplicationUIMenu applicationMenu,
                 IDE ide,
                 PluginUIFactory pluginUIFactory) {
        this.ide = ide;

        applicationMenu.setOpenProjectMenuItemActionListener(this::openProject);

        windowFocusListeners = new ArrayList<>();
        for (IDEPlugin plugin : ide.getPlugins()) {
            pluginUIFactory.constructUIForPlugin(plugin)
                    .ifPresent(pluginUI -> {
                        pluginPane.addTab(plugin.getShortName(), pluginUI.$$$getRootComponent$$$());
                        windowFocusListeners.add(pluginUI.getWindowFocusListener());
                    });
        }


        ide.addProjectChangedListener((event -> SwingUtilities.invokeLater(() -> {
            ProjectUI projectUI = new ProjectUI(applicationMenu, event.getProject());
            setProjectView(projectUI.$$$getRootComponent$$$());
        })));
    }

    private void setProjectView(JComponent component) {
        projectPanel.removeAll();
        projectPanel.add(component);
        projectPanel.revalidate();
    }

    private void openProject(ActionEvent actionEvent) {
        chooser.setDialogTitle("Select project directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file == null)
                return;

            SwingWorker worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        if (!ide.isCurrentlyOpenProject(file))
                            this.process(null);
                        ide.openProject(file);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    return null;
                }

                @Override
                protected void process(List<Void> chunks) {
                    JLabel label = new JLabel();
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setText("Loading project");
                    setProjectView(label);
                }
            };

            if (lastInvokedOpenProjectWorker != null)
                lastInvokedOpenProjectWorker.cancel(true);
            lastInvokedOpenProjectWorker = worker;
            lastInvokedOpenProjectWorker.execute();
        }
    }

    public List<WindowFocusListener> getWindowFocusListeners() {
        return windowFocusListeners;
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
        splitPane1.setOrientation(0);
        mainPanel.add(splitPane1, BorderLayout.CENTER);
        projectPanel = new JPanel();
        projectPanel.setLayout(new BorderLayout(0, 0));
        projectPanel.setPreferredSize(new Dimension(0, 500));
        splitPane1.setLeftComponent(projectPanel);
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(0);
        label1.setText("Use \"File\" menu to open a project");
        projectPanel.add(label1, BorderLayout.CENTER);
        pluginPane = new JTabbedPane();
        splitPane1.setRightComponent(pluginPane);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}