package spout.server.paper.api.packetmapping.item;

import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service for the item mappings that Spout applies.
 */
public interface ItemMappings<M> extends Composable<ItemMappingsComposeEvent<M>> {

    /**
     * An internal interface to get the {@link ItemMappings} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ItemMappings<?>> {
    }

    /**
     * @return The {@link ItemMappings} instance.
     */
    static ItemMappings<?> get() {
        return ServiceLoader.load(ItemMappings.ServiceProvider.class, ItemMappings.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
