package spout.server.paper.impl.packetmapping;

import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.WithClientViewMappingFunctionContext;

/**
 * A base implementation of {@link WithClientViewMappingFunctionContext}.
 */
public class WithClientViewMappingFunctionContextImpl implements WithClientViewMappingFunctionContext {

    private final ClientView clientView;

    public WithClientViewMappingFunctionContextImpl(ClientView clientView) {
        this.clientView = clientView;
    }

    @Override
    public ClientView getClientView() {
        return this.clientView;
    }

}
