package org.fiddlemc.testplugin.data;

import com.google.common.base.Suppliers;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import java.util.function.Supplier;

/**
 * Don't call {@link Supplier#get} on a field of this class before its item type has been registered.
 */
public final class PluginItemTypes {
    public static Supplier<ItemType> GLASS_SHARD = itemType("quark:glass_shard");
    public static Supplier<ItemType> BIRCH_BOOKSHELF = itemType("quark:birch_bookshelf");
    public static Supplier<ItemType> DIORITE_BRICKS = itemType("quark:diorite_bricks");
    public static Supplier<ItemType> DIORITE_BRICK_SLAB = itemType("quark:diorite_brick_slab");
    public static Supplier<ItemType> DIORITE_BRICK_STAIRS = itemType("quark:diorite_brick_stairs");

    private static Supplier<ItemType> itemType(String key) {
        return Suppliers.memoize(() -> Registry.ITEM.get(Key.key(key)));
    }
}
