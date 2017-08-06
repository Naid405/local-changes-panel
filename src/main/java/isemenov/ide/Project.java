package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.project.ProjectFileListChangedEvent;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Project {
    private final Path projectDirectoryPath;
    //File | isDirectory
    private final Map<Path, Boolean> projectFiles;

    private final EventManager globalEventManager;

    public Project(Path projectDirectoryPath, EventManager globalEventManager) {
        Objects.requireNonNull(projectDirectoryPath);
        Objects.requireNonNull(globalEventManager);

        this.projectDirectoryPath = projectDirectoryPath;
        this.globalEventManager = globalEventManager;
        this.projectFiles = new LinkedHashMap<>();
    }

    public Path getProjectDirectoryPath() {
        return projectDirectoryPath;
    }

    public synchronized Map<Path, Boolean> getProjectFiles() {
        return Collections.unmodifiableMap(projectFiles);
    }

    public synchronized void readFileTree() throws FileTreeReadingException {
        final Set<Path> missingFiles = new HashSet<>(projectFiles.keySet());

        final LinkedHashMap<Path, Boolean> newFiles = new LinkedHashMap<>();

        try {
            Files.walkFileTree(projectDirectoryPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                    if (projectDirectoryPath.compareTo(path) == 0)
                        return FileVisitResult.CONTINUE;

                    if (!missingFiles.remove(path))
                        newFiles.put(path, true);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    if (!missingFiles.remove(path))
                        newFiles.put(path, false);
                    return FileVisitResult.CONTINUE;
                }
            });

            for (Path file : missingFiles) {
                projectFiles.remove(file);
            }
            projectFiles.putAll(newFiles);

            globalEventManager.fireEventListeners(this,
                                                  new ProjectFileListChangedEvent(newFiles, missingFiles));
        } catch (IOException e) {
            throw new FileTreeReadingException(projectDirectoryPath, e);
        }
    }

    public synchronized void deleteFile(Path path) throws FileDeleteExceptionException {
        Boolean isDirectory = projectFiles.get(path);
        if (isDirectory == null)
            return;

        List<Path> filesToDelete = new ArrayList<>();
        try {
            if (isDirectory) {
                Files.walkFileTree(projectDirectoryPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                        filesToDelete.add(path);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        filesToDelete.add(path);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                filesToDelete.add(path);
            }

            ListIterator<Path> iterator = filesToDelete.listIterator(filesToDelete.size());
            while (iterator.hasPrevious()) {
                Path toRemove = iterator.previous();
                Files.delete(toRemove);
                projectFiles.remove(toRemove);
            }
            globalEventManager.fireEventListeners(this,
                                                  new ProjectFileListChangedEvent(new LinkedHashMap<>(),
                                                                                  new HashSet<>(filesToDelete)));
        } catch (IOException e) {
            throw new FileDeleteExceptionException(projectDirectoryPath, e);
        }
    }
}
