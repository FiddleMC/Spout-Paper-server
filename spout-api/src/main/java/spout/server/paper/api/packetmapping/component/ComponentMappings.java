package spout.server.paper.api.packetmapping.component;

import java.util.ServiceLoader;

import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.java.serviceloader.GenericServiceProvider;

/**
 * A service for the component mappings that Spout applies.
 */
public interface ComponentMappings<M> extends Composable<ComponentMappingsComposeEvent<M>> {

    /**
     * An internal interface to get the {@link ComponentMappings} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ComponentMappings<?>> {
    }

    /**
     * @return The {@link ComponentMappings} instance.
     */
    static ComponentMappings<?> get() {
        return ServiceLoader.load(ComponentMappings.ServiceProvider.class, ComponentMappings.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

}
