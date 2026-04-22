package spout.server.paper.api.packetmapping.component;

import it.unimi.dsi.fastutil.Pair;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.util.composable.BuilderComposeEvent;
import spout.server.paper.api.util.composable.ChangeRegisteredComposeEvent;
import spout.server.paper.api.util.composable.GetRegisteredComposeEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides functionality to register mappings to the {@link ComponentMappings}.
 *
 * <p>
 * Be aware that a lot of components are sent to the client,
 * so only use this functionality if absolutely necessary,
 * and make sure any registered functions run very fast.
 * </p>
 *
 * <p>
 * Casting this instance to {@code ComponentMappingsComposeEventNMS} and using its methods
 * with Minecraft internals gives <i>significantly</i> better performance.
 * </p>
 */
public interface ComponentMappingsComposeEvent<M> extends BuilderComposeEvent<ComponentMappingBuilder>, GetRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, ComponentTarget>, M>, ChangeRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, ComponentTarget>, M> {

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegistered(ClientView.AwarenessLevel awarenessLevel, ComponentTarget from) {
        return this.getRegistered(Pair.of(awarenessLevel, from));
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegistered(ClientView.AwarenessLevel awarenessLevel, ComponentTarget from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(Pair.of(awarenessLevel, from), listConsumer);
    }

}
