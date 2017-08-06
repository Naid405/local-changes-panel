package isemenov.ide.vcs.git;

import isemenov.ide.Project;
import isemenov.ide.event.EventManager;
import isemenov.ide.util.ShellCommandExecutor;
import isemenov.ide.vcs.VCSException;
import isemenov.ide.vcs.VCSService;
import isemenov.ide.vcs.VCSServiceFactory;

public class GitServiceFactory implements VCSServiceFactory {
    @Override
    public VCSService getServiceForProject(Project project, EventManager globalEventManager) throws VCSException {
        GitService gitService = new GitService(new ShellCommandExecutor(), project.getProjectDirectoryPath(),
                                               globalEventManager);
        return gitService;
    }
}
