package isemenov.ide.plugin;

import javax.swing.*;
import java.awt.event.WindowFocusListener;

public interface PluginUI {
    JComponent $$$getRootComponent$$$();

    WindowFocusListener getWindowFocusListener();
}
