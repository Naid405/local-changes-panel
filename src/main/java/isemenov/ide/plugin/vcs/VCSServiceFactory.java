package isemenov.ide.plugin.vcs;

import isemenov.ide.Project;

public interface VCSServiceFactory {
    VCSService getServiceForProject(Project project) throws NotVCSRootException;
}
