package spout.server.paper.api.packetmapping.component;

import spout.server.paper.api.packetmapping.AwarenessLevelMappingBuilder;
import spout.server.paper.api.util.composable.FromBuilder;
import spout.server.paper.api.util.composable.FunctionBuilder;

/**
 * A builder to define a component mapping.
 */
public interface ComponentMappingBuilder extends AwarenessLevelMappingBuilder, FromBuilder<ComponentTarget>, FunctionBuilder<ComponentMappingHandle> {
}
