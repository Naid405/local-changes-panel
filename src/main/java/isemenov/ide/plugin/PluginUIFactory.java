package isemenov.ide.plugin;

import java.util.Optional;

public interface PluginUIFactory {
    Optional<PluginUI> constructUIForPlugin(IDEPlugin plugin) throws PluginUIConstructionException;
}
