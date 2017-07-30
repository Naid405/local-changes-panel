package isemenov.ide.plugin;

import isemenov.ide.plugin.vcs.VCSIntegrationPlugin;
import isemenov.ide.plugin.vcs.ui.VCSPluginUIFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StaticRegistryDelegatingPluginUIFactory implements PluginUIFactory {
    private final static Map<Class<? extends IDEPlugin>, PluginUIFactory> DELEGATE_MAP = new HashMap<>();

    static {
        DELEGATE_MAP.put(VCSIntegrationPlugin.class, new VCSPluginUIFactory());
    }

    @Override
    public Optional<PluginUI> constructUIForPlugin(IDEPlugin plugin) {
        PluginUIFactory factory = DELEGATE_MAP.get(plugin.getClass());
        if (factory == null)
            return Optional.empty();

        return factory.constructUIForPlugin(plugin);
    }
}
