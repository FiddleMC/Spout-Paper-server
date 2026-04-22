package spout.server.paper.impl.packetmapping.item;

import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.packetmapping.item.ItemMappingFunctionContext;
import spout.server.paper.impl.packetmapping.WithClientViewMappingFunctionContextImpl;

/**
 * The implementation of {@link ItemMappingFunctionContext}.
 */
public class ItemMappingFunctionContextImpl extends WithClientViewMappingFunctionContextImpl implements ItemMappingFunctionContext {

    private final boolean isItemStackInItemFrame;
    private final boolean isStonecutterRecipeResult;

    public ItemMappingFunctionContextImpl(ClientView clientView, boolean isItemStackInItemFrame, boolean isStonecutterRecipeResult) {
        super(clientView);
        this.isItemStackInItemFrame = isItemStackInItemFrame;
        this.isStonecutterRecipeResult = isStonecutterRecipeResult;
    }

    @Override
    public boolean isItemStackInItemFrame() {
        return this.isItemStackInItemFrame;
    }

    @Override
    public boolean isStonecutterRecipeResult() {
        return this.isStonecutterRecipeResult;
    }

}
