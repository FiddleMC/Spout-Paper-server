package spout.server.paper.api.packetmapping.item;

import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.service.SpoutServices;

/**
 * A service for the item mappings that Spout applies.
 */
public interface ItemMappings<M> extends Composable<ItemMappingsComposeEvent<M>> {

    /**
     * @return The {@link ItemMappings} instance.
     */
    static ItemMappings<?> get() {
        return SpoutServices.getItemMappings();
    }

}
