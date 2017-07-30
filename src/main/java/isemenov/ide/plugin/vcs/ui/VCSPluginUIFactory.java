package isemenov.ide.plugin.vcs.ui;

import isemenov.ide.plugin.IDEPlugin;
import isemenov.ide.plugin.PluginUI;
import isemenov.ide.plugin.PluginUIConstructionException;
import isemenov.ide.plugin.PluginUIFactory;
import isemenov.ide.plugin.vcs.VCSIntegrationPlugin;

import java.util.Optional;

public class VCSPluginUIFactory implements PluginUIFactory {
    @Override
    public Optional<PluginUI> constructUIForPlugin(IDEPlugin plugin) throws PluginUIConstructionException {
        if (!(plugin instanceof VCSIntegrationPlugin))
            throw new PluginUIConstructionException("Invalid plugin class " + plugin.getClass());

        VCSPluginUI ui = new VCSPluginUI((VCSIntegrationPlugin) plugin);
        return Optional.of(ui);
    }
}
