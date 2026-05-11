package spout.server.paper.api.packetmapping.item.nms;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import spout.server.paper.api.packetmapping.item.ItemMappingUtilities;

/**
 * An extension to {@link ItemMappingUtilities} using Minecraft internals.
 */
public interface ItemMappingUtilitiesNMS extends ItemMappingUtilities {

    /**
     * @return The {@link ItemMappingUtilitiesNMS} instance.
     */
    static ItemMappingUtilitiesNMS get() {
        return (ItemMappingUtilitiesNMS) ItemMappingUtilities.get();
    }

    /**
     * Changes the {@link Item} of the given handle's {@link ItemStack},
     * while attempting to keep the client-side appearance the same in most ways.
     *
     * @param handle  The handle being mapped.
     * @param newItem The new {@link Item} for the item stack.
     * @return Whether any changes were made.
     */
    boolean setItemWhilePreservingRest(ItemMappingHandleNMS handle, Item newItem);

}
