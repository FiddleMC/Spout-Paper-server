package spout.server.paper.api.packetmapping.block;

import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service for the block mappings that Spout applies.
 */
public interface BlockMappings extends Composable<BlockMappingsComposeEvent> {

    /**
     * An internal interface to get the {@link BlockMappings} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<BlockMappings> {
    }

    /**
     * @return The {@link BlockMappings} instance.
     */
    static BlockMappings get() {
        return ServiceLoader.load(BlockMappings.ServiceProvider.class, BlockMappings.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
