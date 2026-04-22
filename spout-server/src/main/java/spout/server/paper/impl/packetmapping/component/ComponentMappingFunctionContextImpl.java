package spout.server.paper.impl.packetmapping.component;

import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.component.ComponentMappingFunctionContext;
import spout.server.paper.impl.packetmapping.WithClientViewMappingFunctionContextImpl;

/**
 * The implementation of {@link ComponentMappingFunctionContext}.
 */
public class ComponentMappingFunctionContextImpl extends WithClientViewMappingFunctionContextImpl implements ComponentMappingFunctionContext {
    public ComponentMappingFunctionContextImpl(ClientView clientView) {
        super(clientView);
    }
}
