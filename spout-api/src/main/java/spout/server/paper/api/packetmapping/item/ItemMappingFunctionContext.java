package spout.server.paper.api.packetmapping.item;

import spout.server.paper.api.packetmapping.WithClientViewMappingFunctionContext;
import spout.server.paper.api.util.mapping.MappingFunctionContext;

/**
 * A {@link MappingFunctionContext} for the {@link ItemMappingHandle}s.
 */
public interface ItemMappingFunctionContext extends WithClientViewMappingFunctionContext {

    /**
     * @return Whether the item stack on which this mapping is being applied
     * is an item stack in an item frame.
     */
    boolean isItemStackInItemFrame();

    /**
     * @return Whether the item stack on which this mapping is being applied
     * is the result of a stonecutter recipe.
     */
    boolean isStonecutterRecipeResult();

}
