package isemenov.ide.plugin.vcs.git;

import isemenov.ide.Project;
import isemenov.ide.plugin.vcs.NotVCSRootException;
import isemenov.ide.plugin.vcs.VCSService;
import isemenov.ide.plugin.vcs.VCSServiceFactory;
import isemenov.ide.util.ShellCommandExecutor;

import java.util.Objects;

public class OnlyGitServiceFactory implements VCSServiceFactory {
    @Override
    public VCSService getServiceForProject(Project project) throws NotVCSRootException {
        Objects.requireNonNull(project);
        return new GitService(new ShellCommandExecutor(), project.getProjectDirectoryPath());
    }
}
