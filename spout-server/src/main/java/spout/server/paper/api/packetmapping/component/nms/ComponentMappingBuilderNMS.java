package spout.server.paper.api.packetmapping.component.nms;

import spout.server.paper.api.packetmapping.AwarenessLevelMappingBuilder;
import spout.server.paper.api.packetmapping.component.ComponentMappingBuilder;
import spout.server.paper.api.packetmapping.component.ComponentTarget;
import spout.server.paper.api.util.composable.FromBuilder;
import spout.server.paper.api.util.composable.FunctionBuilder;

/**
 * An alternative to {@link ComponentMappingBuilder} that uses Minecraft internals.
 */
public interface ComponentMappingBuilderNMS extends AwarenessLevelMappingBuilder, FromBuilder<ComponentTarget>, FunctionBuilder<ComponentMappingHandleNMS> {
}
