package spout.server.paper.impl.packetmapping.component;

import spout.server.paper.api.packetmapping.component.ComponentMappingBuilder;
import spout.server.paper.api.packetmapping.component.ComponentMappingHandle;

/**
 * The implementation of {@link ComponentMappingBuilder}.
 */
public class ComponentMappingBuilderImpl extends AbstractComponentMappingBuilderImpl<ComponentMappingHandle> implements ComponentMappingBuilder {

    @Override
    protected ComponentMappingsStep createFunctionStep() {
        return new AdventureFunctionComponentMappingsStep(this.function);
    }

}
