package spout.server.paper.api.packetmapping.component.nms;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import spout.server.paper.api.packetmapping.component.ComponentMappingFunctionContext;
import spout.server.paper.api.util.mapping.MutableMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ComponentMappingBuilderNMS#to}.
 */
public interface ComponentMappingHandleNMS extends WithContextMappingFunctionHandle<Component, ComponentMappingFunctionContext>, WithOriginalMappingFunctionHandle<Component>, MutableMappingFunctionHandle<Component, MutableComponent> {
}
