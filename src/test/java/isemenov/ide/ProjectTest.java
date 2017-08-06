package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.UnorderedEventManager;
import isemenov.ide.event.project.ProjectFileListChangedEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;

public class ProjectTest {
    private Project project;
    private ChangeEventValidator eventValidator;

    private HashMap<Path, Boolean> correctMap;
    private Path rootDirectory;
    private Path file1;
    private Path file2;
    private Path nestedDir;
    private Path nestedFile1;
    private Path nestedFile2;

    @Before
    public void setUp() throws Exception {
        rootDirectory = Files.createTempDirectory(Paths.get("target"), "projectTest");
        EventManager eventManager = new UnorderedEventManager();
        project = new Project(rootDirectory, eventManager);
        eventValidator = new ChangeEventValidator();
        correctMap = new LinkedHashMap<>();

        correctMap.put(file1 = Files.createTempFile(rootDirectory, "", ""), false);
        correctMap.put(file2 = Files.createTempFile(rootDirectory, "", ""), false);
        correctMap.put(nestedDir = Files.createTempDirectory(rootDirectory, ""), true);
        correctMap.put(nestedFile1 = Files.createTempFile(nestedDir, "", ""), false);
        correctMap.put(nestedFile2 = Files.createTempFile(nestedDir, "", ""), false);

        eventManager.addEventListener(ProjectFileListChangedEvent.class, eventValidator);
    }

    @After
    public void tearDown() throws Exception {
        List<Path> filesToDelete = new ArrayList<>();
        Files.walkFileTree(rootDirectory, new SimpleFileVisitor<Path>() {
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

        //Walk in reverse order to delete directories too
        ListIterator<Path> iterator = filesToDelete.listIterator(filesToDelete.size());
        while (iterator.hasPrevious()) {
            Files.delete(iterator.previous());
        }
    }

    @Test
    public void readFileTree() throws Exception {
        //region initial read
        project.readFileTree();
        Map<Path, Boolean> newFiles = new HashMap<>();
        newFiles.put(file1, false);
        newFiles.put(file2, false);
        newFiles.put(nestedDir, true);
        newFiles.put(nestedFile1, false);
        newFiles.put(nestedFile2, false);

        Assert.assertEquals(newFiles, eventValidator.newFiles);
        Assert.assertEquals(new HashSet<>(), eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);
        //endregion

        //region consequent calls
        for (int i = 0; i < 5; i++) {
            project.readFileTree();

            Assert.assertEquals(new HashMap<>(), eventValidator.newFiles);
            Assert.assertEquals(new HashSet<>(), eventValidator.removedFiles);
            Assert.assertEquals(project.getProjectFiles(), correctMap);
        }
        //endregion

        Path filePath;
        //region new file
        filePath = Files.createTempFile(rootDirectory, "", "");
        correctMap.put(filePath, false);
        project.readFileTree();

        Assert.assertEquals(Collections.singletonMap(filePath, false), eventValidator.newFiles);
        Assert.assertEquals(new HashSet<>(), eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);
        //endregion

        //region new file in nested dir
        filePath = Files.createTempFile(nestedDir, "", "");
        correctMap.put(filePath, false);
        project.readFileTree();

        Assert.assertEquals(Collections.singletonMap(filePath, false), eventValidator.newFiles);
        Assert.assertEquals(new HashSet<>(), eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);
        //endregion

        Path dirPath;
        //region new directory with file inside
        dirPath = Files.createTempDirectory(rootDirectory, "");
        filePath = Files.createTempFile(dirPath, "", "");
        correctMap.put(dirPath, true);
        correctMap.put(filePath, false);
        project.readFileTree();

        newFiles = new LinkedHashMap<>();
        newFiles.put(dirPath, true);
        newFiles.put(filePath, false);

        Assert.assertEquals(newFiles, eventValidator.newFiles);
        Assert.assertEquals(new HashSet<>(), eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);

        //Validate ordering (parent directory first)
        Iterator<Map.Entry<Path, Boolean>> correctIterator = newFiles.entrySet().iterator();
        Iterator<Map.Entry<Path, Boolean>> iteratorToTest = eventValidator.newFiles.entrySet().iterator();
        for (; correctIterator.hasNext(); ) {
            Assert.assertEquals(correctIterator.next(), iteratorToTest.next());
        }
        //endregion

        //region deleted directory with file inside
        Files.delete(filePath);
        Files.delete(dirPath);
        correctMap.remove(dirPath);
        correctMap.remove(filePath);
        project.readFileTree();

        Assert.assertEquals(new HashMap<>(), eventValidator.newFiles);
        Assert.assertEquals(new HashSet<>(Arrays.asList(filePath, dirPath)), eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);
        //endregion

        //region delete two files, create two files
        Files.delete(file1);
        Files.delete(file2);
        correctMap.remove(file1);
        correctMap.remove(file2);
        Set<Path> deleted = new HashSet<>(Arrays.asList(file1, file2));

        file1 = Files.createTempFile(rootDirectory, "", "");
        file2 = Files.createTempFile(rootDirectory, "", "");
        newFiles = new LinkedHashMap<>();
        newFiles.put(file1, false);
        newFiles.put(file2, false);
        correctMap.putAll(newFiles);
        project.readFileTree();

        Assert.assertEquals(newFiles, eventValidator.newFiles);
        Assert.assertEquals(deleted, eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);
        //endregion
    }

    @Test
    public void deleteFile() throws Exception {
        project.readFileTree();
        //region delete file
        project.deleteFile(file1);
        correctMap.remove(file1);

        Assert.assertTrue(!Files.exists(file1));
        Assert.assertEquals(new HashMap<>(), eventValidator.newFiles);
        Assert.assertEquals(Collections.singleton(file1), eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);
        //endregion

        //region delete directory with two files
        project.deleteFile(nestedDir);
        correctMap.remove(nestedDir);
        correctMap.remove(nestedFile1);
        correctMap.remove(nestedFile2);

        Assert.assertTrue(!Files.exists(nestedDir));
        Assert.assertTrue(!Files.exists(nestedFile1));
        Assert.assertTrue(!Files.exists(nestedFile2));
        Assert.assertEquals(new HashMap<>(), eventValidator.newFiles);
        Assert.assertEquals(new HashSet<>(Arrays.asList(nestedDir, nestedFile1, nestedFile2)),
                            eventValidator.removedFiles);
        Assert.assertEquals(project.getProjectFiles(), correctMap);
        //endregion
    }

    private class ChangeEventValidator implements Consumer<ProjectFileListChangedEvent> {
        Map<Path, Boolean> newFiles;
        Set<Path> removedFiles;

        @Override
        public void accept(ProjectFileListChangedEvent projectFileListChangedEvent) {
            newFiles = projectFileListChangedEvent.getNewFiles();
            removedFiles = projectFileListChangedEvent.getRemovedFiles();
        }
    }
}