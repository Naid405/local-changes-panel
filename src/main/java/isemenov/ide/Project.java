package isemenov.ide;

import isemenov.ide.event.ide.editor.ProjectFileEventsListener;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Project {
    private final ErrorHandler errorHandler;

    private final Path projectDirectoryPath;

    private final FileEditor fileEditor;

    private volatile Set<Path> projectFiles;

    public Project(ErrorHandler errorHandler,
                   Path projectDirectoryPath,
                   FileEditor fileEditor) {
        this.errorHandler = errorHandler;
        Objects.requireNonNull(projectDirectoryPath);
        Objects.requireNonNull(fileEditor);

        this.projectDirectoryPath = projectDirectoryPath;
        this.fileEditor = fileEditor;
        this.projectFiles = ConcurrentHashMap.newKeySet();
    }

    public Path getProjectDirectoryPath() {
        return projectDirectoryPath;
    }

    public FileEditor getFileEditor() {
        return fileEditor;
    }

    public void refreshProjectFiles() {
        Set<Path> deletedFiles;
        Set<Path> projectFiles = new HashSet<>();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(projectDirectoryPath, true);
        HashMap<Path, DefaultMutableTreeNode> directoryNodeMapping = new HashMap<>();
        directoryNodeMapping.put(projectDirectoryPath, root);

        try {
            synchronized (this) {
                Files.walkFileTree(projectDirectoryPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Project.this.projectFiles.remove(dir);

                        if (!projectDirectoryPath.equals(dir)) {
                            DefaultMutableTreeNode parent = directoryNodeMapping.get(dir.getParent());
                            DefaultMutableTreeNode node = new FileTreeNode(dir, true);
                            parent.add(node);

                            directoryNodeMapping.put(dir, node);
                        }
                        projectFiles.add(dir);
                        return Thread.currentThread().isInterrupted() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Project.this.projectFiles.remove(file);

                        DefaultMutableTreeNode parent = directoryNodeMapping.get(file.getParent());
                        DefaultMutableTreeNode node = new FileTreeNode(file, false);
                        parent.add(node);

                        projectFiles.add(file);
                        return Thread.currentThread().isInterrupted() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
                    }
                });
                deletedFiles = this.projectFiles;
                this.projectFiles = projectFiles;
            }
            for (Path deletedFile : deletedFiles) {
                fileEditor.closeOpenedFile(deletedFile);
            }
            fileEditor.setFileTree(root);
        } catch (IOException e) {
            errorHandler.error(new FileTreeReadingException(projectDirectoryPath, e));
        }
    }

    public void addProjectFileEventListener(ProjectFileEventsListener listener) {
        Objects.requireNonNull(listener);

        this.fileEditor.addFileOpenedListener((editor) -> listener.projectFileOpened((editor.getFile())));
        this.fileEditor.addFileClosedListener((editor) -> listener.projectFileClosed((editor.getFile())));
        this.fileEditor.addFileChangedListener((editor) -> listener.projectFileChanged((editor.getFile())));
        this.fileEditor.addFileSavedListener((editor) -> listener.projectFileSaved((editor.getFile())));
    }
}
