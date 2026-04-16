package org.fiddlemc.fiddle.api.packetmapping.item.nms;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemType;
import org.fiddlemc.fiddle.api.clientview.ClientView;
import org.fiddlemc.fiddle.api.packetmapping.item.ItemMappingsComposeEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * An extension to {@link ItemMappingsComposeEvent} using Minecraft internals.
 */
public interface ItemMappingsComposeEventNMS<M> extends ItemMappingsComposeEvent<M> {

    /**
     * @see #register(Consumer)
     */
    void registerNMS(Consumer<ItemMappingBuilderNMS> builderConsumer);

    /**
     * @see #getRegistered(Object)
     */
    default List<M> getRegisteredNMS(Pair<ClientView.AwarenessLevel, Item> key) {
        return this.getRegisteredNMS(key.left(), key.right());
    }

    /**
     * @see #getRegistered(ClientView.AwarenessLevel, ItemType)
     */
    default List<M> getRegisteredNMS(ClientView.AwarenessLevel awarenessLevel, Item from) {
        return this.getRegistered(awarenessLevel, Registry.ITEM.get(Key.key(from.keyInItemRegistry.getNamespace(), from.keyInItemRegistry.getPath())));
    }

    /**
     * @see #changeRegistered(Object, Consumer)
     */
    default void changeRegisteredNMS(Pair<ClientView.AwarenessLevel, Item> key, Consumer<List<M>> listConsumer) {
        this.changeRegisteredNMS(key.left(), key.right(), listConsumer);
    }

    /**
     * @see #changeRegistered(ClientView.AwarenessLevel, ItemType, Consumer)
     */
    default void changeRegisteredNMS(ClientView.AwarenessLevel awarenessLevel, Item from, Consumer<List<M>> listConsumer) {
        this.changeRegistered(awarenessLevel, Registry.ITEM.get(Key.key(from.keyInItemRegistry.getNamespace(), from.keyInItemRegistry.getPath())), listConsumer);
    }

    @Override
    default void registerAutomatic(ItemType from, ItemType proxy, ItemType fallback, @Nullable NamespacedKey itemModel) {
        this.registerAutomaticNMS(CraftItemType.bukkitToMinecraftNew(from), CraftItemType.bukkitToMinecraftNew(proxy), CraftItemType.bukkitToMinecraftNew(fallback), itemModel == null ? null : CraftNamespacedKey.toMinecraft(itemModel));
    }

    default void registerAutomaticNMS(Item from, Item fallback) {
        this.registerAutomaticNMS(from, fallback, fallback);
    }

    default void registerAutomaticNMS(Item from, Item proxy, Item fallback) {
        this.registerAutomaticNMS(from, proxy, fallback, from.keyInItemRegistry);
    }

    default void registerAutomaticNMS(Item from, Item fallback, @Nullable Identifier itemModel) {
        this.registerAutomaticNMS(from, fallback, fallback, itemModel);
    }

    void registerAutomaticNMS(Item from, Item proxy, Item fallback, @Nullable Identifier itemModel);

}
