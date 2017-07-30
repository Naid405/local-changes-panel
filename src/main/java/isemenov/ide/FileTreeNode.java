package isemenov.ide;

import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Path;

public class FileTreeNode extends DefaultMutableTreeNode {
    public FileTreeNode(Path absolutePath, boolean isDirectory) {
        super(absolutePath, isDirectory);
    }

    @Override
    public String toString() {
        if (super.userObject == null)
            return "";
        return ((Path) super.userObject).getFileName().toString();
    }
}
