package isemenov.ide.vcs;

/**
 * Through this interface various VCS integration providers can be added to application
 */
public interface VCSPluginBungle {
    String getVCSName();

    VCSServiceFactory getServiceFactory();

    VCSUIActionFactory getActionsFactory(VCSService service);
}
