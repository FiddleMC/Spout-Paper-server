package spout.server.paper.api.packetmapping.item;

import org.bukkit.inventory.ItemStack;
import spout.server.paper.api.util.mapping.MutableMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link ItemMappingBuilder#to}.
 */
public interface ItemMappingHandle extends WithContextMappingFunctionHandle<ItemStack, ItemMappingFunctionContext>, WithOriginalMappingFunctionHandle<ItemStack>, MutableMappingFunctionHandle<ItemStack, ItemStack> {
}
