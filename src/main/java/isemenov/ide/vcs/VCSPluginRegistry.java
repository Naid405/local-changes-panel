package isemenov.ide.vcs;

import isemenov.ide.vcs.git.GitBundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VCSPluginRegistry {
    public final static String VCS_NAME = "git";

    private VCSPluginRegistry() {
    }

    private final static Map<String, VCSPluginBungle> PLUGIN_BUNGLE_MAP = new HashMap<>();

    static {
        PLUGIN_BUNGLE_MAP.put(VCS_NAME, new GitBundle());
    }

    public static Optional<VCSPluginBungle> getBundleForVCS(String vcsName) {
        return Optional.ofNullable(PLUGIN_BUNGLE_MAP.get(vcsName));
    }
}
