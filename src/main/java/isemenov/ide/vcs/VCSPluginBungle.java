package isemenov.ide.vcs;

public interface VCSPluginBungle {
    String getVCSName();

    VCSServiceFactory getServiceFactory();

    VCSUIActionFactory getActionsFactory(VCSService service);
}
