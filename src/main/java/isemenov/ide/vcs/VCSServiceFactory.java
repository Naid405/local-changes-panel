package isemenov.ide.vcs;

import isemenov.ide.Project;
import isemenov.ide.event.EventManager;

/**
 * Should provide VCS implementation depending on project parameters
 */
public interface VCSServiceFactory {
    /**
     * Read project information and provide a suitable service implementation
     *
     * @param project            project to read
     * @param globalEventManager IDE event manager that may be required by service
     * @return service for VCS to which project is bound
     */
    VCSService getServiceForProject(Project project, EventManager globalEventManager) throws VCSException;
}
