package isemenov.ide.plugin.vcs.git;

import isemenov.ide.plugin.vcs.*;
import isemenov.ide.util.CommandExecutionException;
import isemenov.ide.util.ShellCommandExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitService implements VCSService {
    private final ShellCommandExecutor commandExecutor;
    private final Path workDirPath;

    public GitService(ShellCommandExecutor commandExecutor, Path workDirPath) throws NotVCSRootException {
        this.commandExecutor = commandExecutor;
        this.workDirPath = workDirPath;
        if (!Files.exists(workDirPath.resolve(".git"))) {
            throw new NotVCSRootException(workDirPath);
        }
    }

    @Override
    public Map<Path, VCSFileStatus> getStatus() throws VCSException {
        Map<Path, VCSFileStatus> map = new HashMap<>();
        try {
            for (String statusLine : commandExecutor.executeCommand(workDirPath,
                                                                    "git",
                                                                    "status",
                                                                    "--untracked-files=all",
                                                                    "--porcelain=1",
                                                                    "--ignored")) {

                GitStatusLine gitStatusLine = GitStatusLine.parse(statusLine);
                map.put(workDirPath.resolve(gitStatusLine.getFilePath()),
                        GitStatusConverter.convert(gitStatusLine.getIndexStatus(),
                                                   gitStatusLine.getWorkTreeStatus()));
            }
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public VCSFileStatus getStatus(Path filePath) throws VCSException {
        List<String> result;
        try {
            result = commandExecutor.executeCommand(workDirPath,
                                                    "git",
                                                    "status",
                                                    "--untracked-files=all",
                                                    "--porcelain=1",
                                                    "--ignored",
                                                    getRelativeFilePath(filePath));
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
        return getStatusFromString(result.size() > 0 ? result.get(0) : null);
    }

    private VCSFileStatus getStatusFromString(String statusLine) {
        Character indexStatus;
        Character workTreeStatus;
        if (statusLine == null) {
            indexStatus = ' ';
            workTreeStatus = ' ';
        } else {
            indexStatus = statusLine.charAt(0);
            workTreeStatus = statusLine.charAt(1);
        }
        return GitStatusConverter.convert(indexStatus, workTreeStatus);
    }

    @Override
    public void removeFile(Path filePath) throws VCSException {
        try {
            commandExecutor.executeCommand(workDirPath,
                                           "git",
                                           "rm",
                                           "--force",
                                           getRelativeFilePath(filePath));
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
    }

    @Override
    public void revertFileChanges(Path filePath) throws VCSException {
        try {
            commandExecutor.executeCommand(workDirPath,
                                           "git",
                                           "checkout",
                                           "HEAD",
                                           getRelativeFilePath(filePath));
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
    }

    private String getRelativeFilePath(Path path) throws FileNotInWorkingTreeException {
        try {
            return workDirPath.relativize(path.toAbsolutePath()).toString();
        } catch (IllegalArgumentException e) {
            throw new FileNotInWorkingTreeException(workDirPath, path);
        }
    }
}
