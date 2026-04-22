package spout.server.paper.impl.packetmapping.component;

import spout.server.paper.api.packetmapping.component.nms.ComponentMappingBuilderNMS;
import spout.server.paper.api.packetmapping.component.nms.ComponentMappingHandleNMS;

/**
 * The implementation of {@link ComponentMappingBuilderNMS}.
 */
public class ComponentMappingBuilderNMSImpl extends AbstractComponentMappingBuilderImpl<ComponentMappingHandleNMS> implements ComponentMappingBuilderNMS {

    @Override
    protected ComponentMappingsStep createFunctionStep() {
        return new MinecraftFunctionComponentMappingsStep(this.function);
    }

}
