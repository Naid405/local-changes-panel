package isemenov.ide.ui;

import javax.swing.*;

public class ErrorHandlerUI {
    private final JFrame applicationFrame;

    public ErrorHandlerUI(JFrame applicationFrame) {
        this.applicationFrame = applicationFrame;
    }

    public void showError(Exception e) {
        JOptionPane.showMessageDialog(applicationFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showWarning(Exception e) {
        JOptionPane.showMessageDialog(applicationFrame, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
