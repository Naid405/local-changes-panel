package isemenov.ide.ui.action;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Helper adapter that will only intercept left mouse double clicks on JTree leaves
 */
public class JTreeNodeDoubleClickMouseAdapter extends MouseAdapter {
    private final Consumer<Object> treeNodeConsumer;

    public JTreeNodeDoubleClickMouseAdapter(Consumer<Object> treeNodeConsumer) {
        this.treeNodeConsumer = treeNodeConsumer;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!(e.getComponent() instanceof JTree))
            return;

        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            JTree tree = (JTree) e.getComponent();

            treeNodeConsumer.accept(tree.getLastSelectedPathComponent());
        }
    }
}