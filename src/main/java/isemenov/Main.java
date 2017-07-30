package isemenov;

import isemenov.ide.IDE;
import isemenov.ide.plugin.IDEPlugin;
import isemenov.ide.plugin.StaticRegistryDelegatingPluginUIFactory;
import isemenov.ide.plugin.vcs.VCSIntegrationPlugin;
import isemenov.ide.plugin.vcs.git.OnlyGitServiceFactory;
import isemenov.ide.ui.IDEUI;
import isemenov.ide.ui.component.ApplicationUIMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<IDEPlugin> pluginList = new ArrayList<>();

        pluginList.add(new VCSIntegrationPlugin(new OnlyGitServiceFactory()));

        IDE application = new IDE(pluginList);

        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("My cool IDE with VCS integration");
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            mainFrame.setMinimumSize(new Dimension(500, 500));
            mainFrame.setSize(new Dimension(1000, 700));

            ApplicationUIMenu menu = new ApplicationUIMenu();

            IDEUI ideUI = new IDEUI(menu,
                                    application,
                                    new StaticRegistryDelegatingPluginUIFactory());


            mainFrame.setContentPane(ideUI.$$$getRootComponent$$$());
            mainFrame.setJMenuBar(menu);
            for (WindowFocusListener listener : ideUI.getWindowFocusListeners()) {
                mainFrame.addWindowFocusListener(listener);
            }

            mainFrame.setVisible(true);
        });
    }
}
