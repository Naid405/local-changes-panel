package isemenov.ide.vcs.git;

import isemenov.ide.vcs.VCSPluginBungle;
import isemenov.ide.vcs.VCSService;
import isemenov.ide.vcs.VCSServiceFactory;
import isemenov.ide.vcs.VCSUIActionFactory;
import isemenov.ide.vcs.git.ui.GitActionProvider;

public class GitBundle implements VCSPluginBungle {
    @Override
    public String getVCSName() {
        return "git";
    }

    @Override
    public VCSServiceFactory getServiceFactory() {
        return new GitServiceFactory();
    }

    @Override
    public VCSUIActionFactory getActionsFactory(VCSService service) {
        return new GitActionProvider((GitService) service);
    }
}
