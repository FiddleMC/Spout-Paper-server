package spout.server.paper.api.packetmapping.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import spout.server.paper.impl.util.service.SpoutServices;

/**
 * Some utilities for the mapping of items.
 */
public interface ItemMappingUtilities {

    /**
     * @return The {@link ItemMappingUtilities} instance.
     */
    static ItemMappingUtilities get() {
        return SpoutServices.getItemMappingUtilities();
    }

    /**
     * Changes the {@link ItemType} of the given handle's {@link ItemStack},
     * while attempting to keep the client-side appearance the same in most ways.
     *
     * @param handle      The handle being mapped.
     * @param newItemType The new {@link ItemType} for the item stack.
     * @return Whether any changes were made.
     */
    boolean setItemTypeWhilePreservingRest(ItemMappingHandle handle, ItemType newItemType);

}
