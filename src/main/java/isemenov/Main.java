package isemenov;

import isemenov.ide.ui.action.OpenProjectAction;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OpenProjectAction().actionPerformed(null));
    }
}
