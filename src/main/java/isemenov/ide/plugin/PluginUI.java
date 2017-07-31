package isemenov.ide.plugin;

import javax.swing.*;
import java.awt.event.WindowFocusListener;
import java.util.Optional;

/**
 * Common interface for IDE plugin UI implementation
 */
public interface PluginUI {
    JComponent $$$getRootComponent$$$();

    Optional<WindowFocusListener> getWindowFocusListener();
}
