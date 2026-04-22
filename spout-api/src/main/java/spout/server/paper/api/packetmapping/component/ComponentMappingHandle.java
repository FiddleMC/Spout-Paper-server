package spout.server.paper.api.packetmapping.component;

import net.kyori.adventure.text.Component;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ComponentMappingBuilder#to}.
 */
public interface ComponentMappingHandle extends WithContextMappingFunctionHandle<Component, ComponentMappingFunctionContext>, WithOriginalMappingFunctionHandle<Component> {
}
