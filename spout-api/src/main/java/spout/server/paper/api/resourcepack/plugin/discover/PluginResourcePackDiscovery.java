package spout.server.paper.api.resourcepack.plugin.discover;

import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service to discover Spout plugin resource pack content.
 */
public interface PluginResourcePackDiscovery extends Composable<PluginResourcePackDiscoverEvent> {

    /**
     * An internal interface to get the {@link PluginResourcePackDiscovery} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<PluginResourcePackDiscovery> {
    }

    /**
     * @return The {@link PluginResourcePackDiscovery} instance.
     */
    static PluginResourcePackDiscovery get() {
        return ServiceLoader.load(PluginResourcePackDiscovery.ServiceProvider.class, PluginResourcePackDiscovery.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    String DEFAULT_PATH = "resource_pack";

}
