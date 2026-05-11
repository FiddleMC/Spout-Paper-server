package spout.server.paper.api.resourcepack.plugin.discover;

import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.service.SpoutServices;

/**
 * A service to discover Spout plugin resource pack content.
 */
public interface PluginResourcePackDiscovery extends Composable<PluginResourcePackDiscoverEvent> {

    /**
     * @return The {@link PluginResourcePackDiscovery} instance.
     */
    static PluginResourcePackDiscovery get() {
        return SpoutServices.getPluginResourcePackDiscovery();
    }

    String DEFAULT_PATH = "resource_pack";

}
