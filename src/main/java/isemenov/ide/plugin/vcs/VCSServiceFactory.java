package isemenov.ide.plugin.vcs;

import isemenov.ide.Project;

/**
 * Should provide VCS implementation depending on project parameters
 */
public interface VCSServiceFactory {
    VCSService getServiceForProject(Project project) throws NotVCSRootException;
}
