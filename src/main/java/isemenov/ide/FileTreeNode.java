package isemenov.ide;

import javax.swing.tree.TreeNode;
import java.util.*;

public final class FileTreeNode implements TreeNode {
    private final FileTreeNode parent;
    private final ProjectFile file;
    private final List<FileTreeNode> children;

    public FileTreeNode(FileTreeNode parent, ProjectFile file) {
        Objects.requireNonNull(file);

        this.parent = parent;
        this.file = file;
        this.children = file.isDirectory() ? new ArrayList<>() : Collections.emptyList();
    }

    public void addChild(FileTreeNode fileTreeNode) {
        if (!this.file.isDirectory())
            throw new NotADirectoryException(file.getFilePath());
        children.add(fileTreeNode);
    }

    public ProjectFile getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getFilePath().getFileName().toString();
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return this.parent;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return file.isDirectory();
    }

    @Override
    public boolean isLeaf() {
        return !file.isDirectory();
    }

    @Override
    public Enumeration children() {
        return new Enumeration() {
            private final Iterator iterator = children.iterator();

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public Object nextElement() {
                return iterator.next();
            }
        };
    }
}
