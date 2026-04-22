package spout.server.paper.api.packetmapping.component.nms;

import spout.server.paper.api.packetmapping.component.ComponentMappingsComposeEvent;
import java.util.function.Consumer;

/**
 * An extension to {@link ComponentMappingsComposeEvent} using Minecraft internals.
 */
public interface ComponentMappingsComposeEventNMS<M> extends ComponentMappingsComposeEvent<M> {

    /**
     * @see #register(Consumer)
     */
    void registerNMS(Consumer<ComponentMappingBuilderNMS> builderConsumer);

}
