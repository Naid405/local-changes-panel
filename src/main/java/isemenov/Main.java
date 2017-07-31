package isemenov;

import isemenov.ide.ErrorHandler;
import isemenov.ide.IDE;
import isemenov.ide.plugin.IDEPlugin;
import isemenov.ide.plugin.StaticRegistryDelegatingPluginUIFactory;
import isemenov.ide.plugin.vcs.VCSIntegrationPlugin;
import isemenov.ide.plugin.vcs.git.OnlyGitServiceFactory;
import isemenov.ide.ui.ErrorHandlerUI;
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
        ErrorHandler errorHandler = new ErrorHandler();

        pluginList.add(new VCSIntegrationPlugin(errorHandler, new OnlyGitServiceFactory()));

        IDE application = new IDE(errorHandler, pluginList);

        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("My cool IDE with VCS integration");
            mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            mainFrame.setMinimumSize(new Dimension(500, 500));
            mainFrame.setSize(new Dimension(1000, 700));

            ErrorHandlerUI errorHandlerUI = new ErrorHandlerUI(mainFrame);
            errorHandler.addErrorListener(event -> SwingUtilities.invokeLater(() -> {
                switch (event.getErrorLevel()) {
                    case ERROR:
                        errorHandlerUI.showError(event.getException());
                        break;
                    case WARN:
                        errorHandlerUI.showWarning(event.getException());
                        break;
                }
            }));

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
