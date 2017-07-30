package isemenov.ide;

import isemenov.ide.event.editor.ProjectFileEventsListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Objects;

public final class Project {
    private final Path projectDirectoryPath;
    private final MultipleProjectFileEditor fileEditor;
    private final DefaultTreeModel fileTreeModel;

    public Project(Path projectDirectoryPath,
                   MultipleProjectFileEditor fileEditor) {
        this.projectDirectoryPath = projectDirectoryPath;
        this.fileEditor = fileEditor;
        this.fileTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(), true);
    }

    public Path getProjectDirectoryPath() {
        return projectDirectoryPath;
    }

    public MultipleProjectFileEditor getFileEditor() {
        return fileEditor;
    }

    public TreeModel getFileTreeModel() {
        return fileTreeModel;
    }

    public void readFileTree() {
        Objects.requireNonNull(projectDirectoryPath);

        FileTreeNode fileTree = new FileTreeNode(null, new ProjectFile(projectDirectoryPath, true));
        HashMap<Path, FileTreeNode> pathFileNodeMapping = new HashMap<>();
        pathFileNodeMapping.put(projectDirectoryPath, fileTree);

        try {
            Files.walkFileTree(projectDirectoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!projectDirectoryPath.equals(dir)) {
                        FileTreeNode parent = pathFileNodeMapping.get(dir.getParent());
                        FileTreeNode node = new FileTreeNode(parent,
                                                             new ProjectFile(dir, true));
                        parent.addChild(node);
                        pathFileNodeMapping.put(dir, node);
                    }
                    return Thread.currentThread().isInterrupted() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    FileTreeNode parent = pathFileNodeMapping.get(file.getParent());
                    FileTreeNode node = new FileTreeNode(parent,
                                                         new ProjectFile(file, false));
                    parent.addChild(node);
                    pathFileNodeMapping.put(file, node);

                    return Thread.currentThread().isInterrupted() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new FileTreeReadingException(projectDirectoryPath, e);
        }
        if (!Thread.currentThread().isInterrupted())
            fileTreeModel.setRoot(fileTree);
    }

    public void openFileInEditor(ProjectFile file) {
        Objects.requireNonNull(file);

        if (!fileEditor.isFileOpen(file)) {
            fileEditor.openFile(file);
            fileEditor.readOpenedFileContent(file);
        }
    }

    public void addProjectFileEventListener(ProjectFileEventsListener listener) {
        Objects.requireNonNull(listener);
        this.fileEditor.addFileOpenedListener((editor) -> listener.projectFileOpened((editor.getProjectFile())));
        this.fileEditor.addFileClosedListener((editor) -> listener.projectFileClosed((editor.getProjectFile())));
        this.fileEditor.addFileChangedListener((editor) -> listener.projectFileChanged((editor.getProjectFile())));
        this.fileEditor.addFileSavedListener((editor) -> listener.projectFileSaved((editor.getProjectFile())));
    }
}
