package isemenov.ide.plugin;

import java.util.Optional;

/**
 * Factory constructing user interface for plugins if it is required (i.e. registered)
 */
public interface PluginUIFactory {
    Optional<PluginUI> constructUIForPlugin(IDEPlugin plugin) throws PluginUIConstructionException;
}
