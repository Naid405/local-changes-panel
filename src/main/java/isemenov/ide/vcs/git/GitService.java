package isemenov.ide.vcs.git;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.core.AllFilesPossiblyChangedEvent;
import isemenov.ide.event.core.FilesPossiblyChangedEvent;
import isemenov.ide.util.CommandExecutionException;
import isemenov.ide.util.ShellCommandExecutor;
import isemenov.ide.vcs.*;

import java.nio.file.Path;
import java.util.*;

public class GitService implements VCSService {
    private final static String GIT_FETCH_UNCHANGED_RESPONSE = "Already up-to-date.";

    private final ShellCommandExecutor commandExecutor;
    private final Path workDirPath;
    private final EventManager globalEventManager;

    private String localBranch;
    private String remoteBranch;

    public GitService(ShellCommandExecutor commandExecutor,
                      Path workDirPath,
                      EventManager globalEventManager) throws VCSException {
        this.commandExecutor = commandExecutor;
        this.workDirPath = workDirPath;
        this.globalEventManager = globalEventManager;
        checkIsWorkDirectory(workDirPath);
        getBranchInfo();
    }

    private void getBranchInfo() throws CannotExecuteVCSOperation {
        try {
            List<String> response = commandExecutor.executeCommand(workDirPath,
                                                                   "git",
                                                                   "for-each-ref",
                                                                   "--format=%(HEAD)%(refname:short)<-%(upstream:short)",
                                                                   "--sort=-HEAD",
                                                                   "refs/heads");

            if (response.size() < 1)
                throw new CannotExecuteVCSOperation("Malformed response from git for-each-ref");

            for (String line : response) {
                if (line.charAt(0) == '*') {
                    String[] branches = line.substring(1).split("<-");
                    localBranch = branches[0];
                    remoteBranch = branches.length > 1 ? branches[1] : null;
                    break;
                }
            }
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
    }

    private void checkIsWorkDirectory(Path path) throws VCSException {
        try {
            List<String> response = commandExecutor.executeCommand(path,
                                                                   "git",
                                                                   "rev-parse",
                                                                   "--is-inside-work-tree");

            if (response.size() < 1)
                throw new CannotExecuteVCSOperation("Malformed response from git rev-parse");

            if (!response.get(0).equals("true"))
                throw new NotVCSRootException(path);
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
    }

    @Override
    public Map<Path, VCSFileStatus> getStatuses(Set<Path> files) throws VCSException {
        Map<Path, VCSFileStatus> map = new HashMap<>();
        for (Path file : files) {
            map.put(file, null);
        }
        try {
            for (String statusLine : commandExecutor.executeCommand(workDirPath,
                                                                    "git",
                                                                    "status",
                                                                    "--untracked-files=all",
                                                                    "--porcelain=1",
                                                                    "--ignored")) {
                GitStatusLine gitStatusLine = GitStatusLine.parse(statusLine);
                Path filePath = workDirPath.resolve(gitStatusLine.getFilePath());
                if (files.contains(filePath))
                    map.put(filePath,
                            GitStatusConverter.convert(gitStatusLine.getIndexStatus(),
                                                       gitStatusLine.getWorkTreeStatus()));
            }
            for (Map.Entry<Path, VCSFileStatus> entry : map.entrySet()) {
                if (entry.getValue() == null)
                    entry.setValue(VCSFileStatus.UNCHANGED);
            }
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
        return Collections.unmodifiableMap(map);
    }

    //TODO: handle error more precisely
    @Override
    public boolean checkExists(Path file) throws VCSException {
        try {
            commandExecutor.executeCommand(workDirPath,
                                           "git",
                                           "rm",
                                           "--cached",
                                           "--ignore-unmatch",
                                           getRelativeFilePath(file));
            globalEventManager.fireEventListenersAsync(this, new FilesPossiblyChangedEvent(
                    Collections.singleton(file.getParent())));
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
        try {
            commandExecutor.executeCommand(workDirPath,
                                           "git",
                                           "cat-file",
                                           "-t",
                                           "HEAD:" + getRelativeFilePath(file).replace('\\', '/'));
            return true;
        } catch (CommandExecutionException e) {
            return false;
        }
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

    public void revertFileChanges(Path filePath) throws VCSException {
        try {
            commandExecutor.executeCommand(workDirPath,
                                           "git",
                                           "checkout",
                                           "HEAD",
                                           getRelativeFilePath(filePath));
            globalEventManager.fireEventListenersAsync(this,
                                                       new FilesPossiblyChangedEvent(
                                                               Collections.singleton(filePath)));
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
    }

    /**
     * Update project files from remote
     *
     * @param updateType how to perform merging of changes
     * @param cleanTree  how to prepare work tree for update if at all
     * @return true if project was updated, false if update was not required
     * @throws NoTrackedBranchException      if there's no remote branch for local
     * @throws MalformedGitResponseException if there's something wrong with response
     * @throws CannotExecuteVCSOperation     if execution failed (exit code != 0)
     */
    public boolean updateProject(UpdateType updateType,
                                 CleanTreeType cleanTree) throws MalformedGitResponseException,
                                                                 CannotExecuteVCSOperation,
                                                                 NoTrackedBranchException {
        try {
            if (remoteBranch == null)
                throw new NoTrackedBranchException(localBranch);

            List<String> result = commandExecutor.executeCommand(workDirPath,
                                                                 "git", "fetch", "--prune");

            if (result.size() < 1)
                throw new MalformedGitResponseException();

            String fetchResponse = result.get(0);
            if (fetchResponse.equals(GIT_FETCH_UNCHANGED_RESPONSE)) {
                return false;
            }

            List<String> command = new ArrayList<>();
            command.add("git");
            command.add("merge");
            command.add(remoteBranch);

            switch (updateType) {
                case MERGE:
                    command.add("--rebase=false");
                    break;
                case REBASE:
                    command.add("--rebase=true");
                    break;
                case DEFAULT:
                    break;
            }

            switch (cleanTree) {
                case STASH:
                    command.add("--autostash");
                    break;
                case NO:
                    break;
            }

            commandExecutor.executeCommand(workDirPath, command.toArray(new String[command.size()]));
            globalEventManager.fireEventListenersAsync(this, new AllFilesPossiblyChangedEvent());
            return true;
        } catch (CommandExecutionException e) {
            throw new CannotExecuteVCSOperation(e);
        }
    }

    public enum UpdateType {
        MERGE,
        REBASE,
        DEFAULT
    }

    public enum CleanTreeType {
        STASH,
        NO
    }

    private String getRelativeFilePath(Path path) throws FileNotInWorkingTreeException {
        try {
            return workDirPath.relativize(path.toAbsolutePath()).toString();
        } catch (IllegalArgumentException e) {
            throw new FileNotInWorkingTreeException(workDirPath, path);
        }
    }
}
