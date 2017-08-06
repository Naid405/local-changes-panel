package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.project.ProjectFileListChangedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Project {
    private final static Logger logger = LogManager.getLogger(Project.class);

    private final Path projectDirectoryPath;
    //File | isDirectory, probably can substitute with set of delegates? TODO: investigate
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

    /**
     * Read file list from FS and determine if anything has changes since the last time
     *
     * @throws FileTreeReadingException if failed to read from FS
     */
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

    /**
     * Delete file from filesystem
     * Will do a recursive delete for directories
     *
     * @param path to delete
     * @throws FileTreeReadingException if failed to collect filetree information
     */
    public synchronized void deleteFile(Path path) throws FileTreeReadingException, CannotDeleteFilesException {
        Boolean isDirectory = projectFiles.get(path);
        if (isDirectory == null)
            return;

        List<Path> filesToDelete = new ArrayList<>();

        if (isDirectory) {
            try {
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
            } catch (IOException e) {
                throw new FileTreeReadingException(projectDirectoryPath, e);
            }
        } else {
            filesToDelete.add(path);
        }

        //Walk in reverse order to delete directories too
        ListIterator<Path> iterator = filesToDelete.listIterator(filesToDelete.size());
        Set<Path> failedToDelete = new HashSet<>();
        while (iterator.hasPrevious()) {
            Path toDelete = iterator.previous();
            try {
                Files.delete(toDelete);
            } catch (IOException e1) {
                logger.warn("Cannot delete file " + toDelete, e1);
                filesToDelete.remove(toDelete);
                failedToDelete.add(toDelete);
            }
            projectFiles.remove(toDelete);
        }
        globalEventManager.fireEventListeners(this,
                                              new ProjectFileListChangedEvent(new LinkedHashMap<>(),
                                                                              new HashSet<>(filesToDelete)));
        if (failedToDelete.size() > 0)
            throw new CannotDeleteFilesException(failedToDelete);
    }
}
