package spout.server.paper.api.packetmapping.item.nms;

import net.minecraft.world.item.ItemStack;
import spout.server.paper.api.packetmapping.item.ItemMappingFunctionContext;
import spout.server.paper.api.util.mapping.MutableMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ItemMappingBuilderNMS#to}.
 */
public interface ItemMappingHandleNMS extends WithContextMappingFunctionHandle<ItemStack, ItemMappingFunctionContext>, WithOriginalMappingFunctionHandle<ItemStack>, MutableMappingFunctionHandle<ItemStack, ItemStack> {
}
