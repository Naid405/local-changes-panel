package isemenov.ide.ui.component;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class VCSFileListItemPopupMenu extends JPopupMenu {
    public VCSFileListItemPopupMenu(List<Action> commonActions,
                                    List<Action> vcsActions) {
        super("File operations");
        Objects.requireNonNull(commonActions);
        Objects.requireNonNull(vcsActions);
        for (Action action : commonActions) {
            add(new JMenuItem(action));
        }
        addSeparator();
        for (Action action : vcsActions) {
            add(new JMenuItem(action));
        }
    }
}
