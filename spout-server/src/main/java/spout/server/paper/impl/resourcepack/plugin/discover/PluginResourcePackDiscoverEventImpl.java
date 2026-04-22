package spout.server.paper.impl.resourcepack.plugin.discover;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import it.unimi.dsi.fastutil.Pair;
import spout.server.paper.api.resourcepack.plugin.discover.PluginResourcePackDiscoverEvent;
import spout.server.paper.impl.util.io.JarFileUtil;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The implementation for {@link PluginResourcePackDiscoverEvent}.
 */
public final class PluginResourcePackDiscoverEventImpl implements PluginResourcePackDiscoverEvent, PaperLifecycleEvent {

    PluginResourcePackDiscoverEventImpl() {
    }

    @Override
    public void register(PluginBootstrap bootstrap, BootstrapContext context, String path) {
        // Store the resource pack path of the plugin
        PluginResourcePackDiscoveryImpl.get().resourcePackPathsByPlugin.put(bootstrap, Pair.of(context.getPluginSource(), path));
        // Include all resource pack files
        try {
            JarFileUtil.forEachFileBelowDirectory(context.getPluginSource().toFile(), path, (entry, jar, relativePath) -> PluginResourcePackDiscoveryImpl.get().providingPluginsByResourcePackFileRelativePath.computeIfAbsent(relativePath, $ -> new ArrayList<>(1)).add(Pair.of(bootstrap, path)));
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading resource pack contents of plugin with bootstrap " + bootstrap.getClass().getName(), e);
        }
    }

}
