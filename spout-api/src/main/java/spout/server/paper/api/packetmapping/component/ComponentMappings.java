package spout.server.paper.api.packetmapping.component;

import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.service.SpoutServices;

/**
 * A service for the component mappings that Spout applies.
 */
public interface ComponentMappings<M> extends Composable<ComponentMappingsComposeEvent<M>> {

    /**
     * @return The {@link ComponentMappings} instance.
     */
    static ComponentMappings<?> get() {
        return SpoutServices.getComponentMappings();
    }

}
