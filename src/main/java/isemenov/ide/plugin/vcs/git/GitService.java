package isemenov.ide.plugin.vcs.git;

import isemenov.ide.plugin.vcs.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GitService implements VCSService {
    private static final Logger logger = LogManager.getLogger(GitService.class);

    private final Path workingDirPath;

    public GitService(Path workingDirPath) throws NotVCSRootException {
        this.workingDirPath = workingDirPath;
        if (!Files.exists(workingDirPath.resolve(".git"))) {
            throw new NotVCSRootException(workingDirPath);
        }
    }

    @Override
    public Map<Path, VCSFileStatus> getStatus() throws VCSException {
        return Collections.unmodifiableMap(
                executeGitCommand("git",
                                  "status",
                                  "--untracked-files=normal",
                                  "--porcelain=1",
                                  "--ignored").stream()
                        .collect(Collectors.toMap(statusLine -> workingDirPath.resolve(statusLine.substring(3)),
                                                  statusLine -> GitStatusConverter.convert(statusLine.charAt(0),
                                                                                           statusLine.charAt(1))
                        )));
    }

    @Override
    public VCSFileStatus getStatus(Path filePath) throws VCSException {
        List<String> result = executeGitCommand("git",
                                                "status",
                                                "--untracked-files=normal",
                                                "--porcelain=1",
                                                "--ignored",
                                                getRelativeFilePath(filePath));
        if (result.size() == 0)
            return VCSFileStatus.UNCHANGED;

        String statusLine = result.get(0);

        return GitStatusConverter.convert(statusLine.charAt(0), statusLine.charAt(1));
    }

    @Override
    public void removeFile(Path filePath) throws VCSException {
        executeGitCommand("git",
                          "rm",
                          getRelativeFilePath(filePath));
    }

    @Override
    public void revertFileChanges(Path filePath) throws VCSException {
        executeGitCommand("git",
                          "checkout",
                          "HEAD",
                          getRelativeFilePath(filePath));
    }

    private String getRelativeFilePath(Path path) throws FileNotInWorkingTreeException {
        try {
            return workingDirPath.relativize(path).toString();
        } catch (IllegalArgumentException e) {
            throw new FileNotInWorkingTreeException(path);
        }
    }

    private List<String> executeGitCommand(String... command) throws CannotExecuteVCSOperation {
        try {
            Process cmdProcess = new ProcessBuilder()
                    .directory(workingDirPath.toFile())
                    .command(command)
                    .start();

            CompletableFuture<List<String>> result = getContents(cmdProcess.getInputStream());
            CompletableFuture<List<String>> error = getContents(cmdProcess.getErrorStream());

            int exitValue = cmdProcess.waitFor();

            if (exitValue != 0)
                throw new CannotExecuteVCSOperation(error.get().stream()
                                                            .reduce(String::concat)
                                                            .orElse("Unknown error"));

            return result.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
    }

    private CompletableFuture<List<String>> getContents(InputStream resultStream) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> result = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream))) {
                String line;
                while ((line = reader.readLine()) != null)
                    result.add(line);
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
