package isemenov.ide.ui;

import javax.swing.*;

public class ErrorHandlerUI {
    private ErrorHandlerUI() {
    }

    public static void showError(Throwable e) {
        if (!SwingUtilities.isEventDispatchThread())
            SwingUtilities.invokeLater(() -> showError(e));

        JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);

    }

    public static void showWarning(Throwable e) {
        if (!SwingUtilities.isEventDispatchThread())
            SwingUtilities.invokeLater(() -> showWarning(e));

        JOptionPane.showMessageDialog(null, e.getMessage(), "Warning",
                                      JOptionPane.WARNING_MESSAGE);
    }
}
