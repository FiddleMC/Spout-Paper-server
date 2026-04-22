package spout.server.paper.api.resourcepack.construct;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import spout.server.paper.api.util.composable.Composable;
import spout.server.paper.impl.util.java.serviceloader.GenericServiceProvider;
import java.util.ServiceLoader;

/**
 * A service to construct the Spout server resource pack.
 */
public interface ResourcePackConstruction extends Composable<ResourcePackConstructEvent> {

    /**
     * An internal interface to get the {@link ResourcePackConstruction} instance.
     */
    interface ServiceProvider extends GenericServiceProvider<ResourcePackConstruction> {
    }

    /**
     * @return The {@link ResourcePackConstruction} instance.
     */
    static ResourcePackConstruction get() {
        return ServiceLoader.load(ResourcePackConstruction.ServiceProvider.class, ResourcePackConstruction.ServiceProvider.class.getClassLoader()).findFirst().get().get();
    }

    /**
     * @return The {@link LifecycleEventType} for when constructing the resource pack has finished.
     */
    LifecycleEventType<BootstrapContext, ResourcePackConstructFinishEvent, PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext>> finish();

}
