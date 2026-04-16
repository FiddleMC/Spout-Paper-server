package org.fiddlemc.fiddle.api.packetmapping.item;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.util.composable.BuilderComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.ChangeRegisteredComposeEvent;
import org.fiddlemc.fiddle.api.util.composable.GetRegisteredComposeEvent;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides functionality to register mappings to the {@link ItemMappings}.
 *
 * <p>
 * Mapping from {@linkplain ItemStack#isEmpty empty} to non-empty, or the other way around, will lead to glitches.
 * </p>
 *
 * <p>
 * Casting this instance to {@code ItemMappingsComposeEventNMS} and using its methods
 * with Minecraft internals gives <i>significantly</i> better performance.
 * </p>
 */
public interface ItemMappingsComposeEvent<M> extends BuilderComposeEvent<ItemMappingBuilder>, GetRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, ItemType>, M>, ChangeRegisteredComposeEvent<Pair<ClientView.AwarenessLevel, ItemType>, M> {

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegistered(ClientView.AwarenessLevel awarenessLevel, ItemType from) {
        return this.getRegistered(Pair.of(awarenessLevel, from));
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegistered(ClientView.AwarenessLevel awarenessLevel, ItemType from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(Pair.of(awarenessLevel, from), listConsumer);
    }

    default void registerAutomatic(ItemType from, ItemType fallback) {
        this.registerAutomatic(from, fallback, fallback);
    }

    default void registerAutomatic(ItemType from, ItemType proxy, ItemType fallback) {
        this.registerAutomatic(from, proxy, fallback, from.getKey());
    }

    default void registerAutomatic(ItemType from, ItemType fallback, @Nullable NamespacedKey itemModel) {
        this.registerAutomatic(from, fallback, fallback, itemModel);
    }

    void registerAutomatic(ItemType from, ItemType proxy, ItemType fallback, @Nullable NamespacedKey itemModel);

}
