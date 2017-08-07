package isemenov.ide.vcs;

import isemenov.ide.vcs.git.GitBundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Not a "real world" class. Should be substituted by "plugin loader" in real implementation.
 */
public class StaticVCSPluginRegistry {
    public final static String VCS_NAME = "git";
    private final static Map<String, VCSPluginBungle> PLUGIN_BUNGLE_MAP = new HashMap<>();

    static {
        PLUGIN_BUNGLE_MAP.put(VCS_NAME, new GitBundle());
    }

    public StaticVCSPluginRegistry() {
    }

    public Optional<VCSPluginBungle> getBundleForVCS(String vcsName) {
        return Optional.ofNullable(PLUGIN_BUNGLE_MAP.get(vcsName));
    }
}
