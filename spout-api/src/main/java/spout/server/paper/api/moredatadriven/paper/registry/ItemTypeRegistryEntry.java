package spout.server.paper.api.moredatadriven.paper.registry;

import io.papermc.paper.registry.RegistryBuilder;
import org.bukkit.FeatureFlag;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.ApiStatus;

/**
 * A data-centric version-specific registry entry for the {@link ItemType} type.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface ItemTypeRegistryEntry {

    /**
     * A mutable builder for the {@link ItemTypeRegistryEntry},
     * that plugins may change in applicable registry events.
     *
     * <p>
     * Additional functionality is available by casting this instance to {@code ItemRegistryEntryBuilderNMS}.
     * </p>
     */
    @ApiStatus.Experimental
    @ApiStatus.NonExtendable
    interface Builder extends ItemTypeRegistryEntry, RegistryBuilder<ItemType>, KeyAwareRegistryEntry {

        /**
         * Sets the type of item to a regular item.
         *
         * <p>
         * This is the default, so normally you don't need to call this.
         * </p>
         */
        ItemTypeRegistryEntry.Builder inheritsFromItem();

        /**
         * Sets the type of item to a block item,
         * for the block with the {@linkplain #getKey same} {@link NamespacedKey},
         *
         * <p>
         * This also calls {@link #useBlockDescriptionPrefix()}.
         * </p>
         */
        ItemTypeRegistryEntry.Builder inheritsFromBlock();

        /**
         * Sets the type of item to a block item,
         * for the block with the given {@link NamespacedKey}.
         *
         * <p>
         * This also calls {@link #useBlockDescriptionPrefix()}.
         * </p>
         *
         * @param blockKey The {@link NamespacedKey} of the block.
         */
        ItemTypeRegistryEntry.Builder inheritsFromBlock(NamespacedKey blockKey);

        /**
         * Sets the type f item to an egg item.
         */
        ItemTypeRegistryEntry.Builder inheritsFromEgg();

        // Missing: a lot

        /**
         * Sets the item that this item is converted to after usage.
         *
         * <p>
         * This is typically used for the remainder of consumables,
         * such as a bowl for stew, or a bottle for potions.
         * </p>
         */
        ItemTypeRegistryEntry.Builder usingConvertsTo(ItemType usingConvertsTo);

        /**
         * Sets the cooldown for this item.
         *
         * <p>
         * This is used by items like ender pearls and wind charges.
         * </p>
         */
        ItemTypeRegistryEntry.Builder useCooldown(float useCooldown);

        /**
         * Sets the max stack size of this item.
         */
        ItemTypeRegistryEntry.Builder stacksTo(int maxStackSize);

        /**
         * Sets the durability of this item, aka the max damage.
         */
        ItemTypeRegistryEntry.Builder durability(int maxDamage);

        /**
         * Sets the type of item that this item leaves behind when crafted with.
         */
        ItemTypeRegistryEntry.Builder craftRemainder(ItemType craftingRemainingItem);

        /**
         * Sets the rarity of this item.
         */
        ItemTypeRegistryEntry.Builder rarity(ItemRarity rarity);

        /**
         * Sets this item to be resistant to fire damage.
         */
        ItemTypeRegistryEntry.Builder fireResistant();

        /**
         * Plays the given song when this item is played in a jukebox.
         */
        ItemTypeRegistryEntry.Builder jukeboxPlayable(JukeboxSong song);

        /**
         * Makes this item enchantable.
         *
         * <p>
         * The value has something to do with the increase in level cost,
         * but I'm not sure.
         * </p>
         */
        ItemTypeRegistryEntry.Builder enchantable(int enchantmentValue);

        /**
         * Makes this item repairable in an anvil with the given item.
         */
        ItemTypeRegistryEntry.Builder repairable(ItemType repairItem);

        /**
         * Makes this item equippable in the given slot.
         */
        ItemTypeRegistryEntry.Builder equippable(EquipmentSlot slot);

        /**
         * Makes this item equippable in the given slot, but not automatically swappable
         * (like shields or mob heads).
         */
        ItemTypeRegistryEntry.Builder equippableUnswappable(EquipmentSlot slot);

        // Missing: tool, pickaxe, axe, hoe, shovel, sword, spear

        /**
         * Makes this item act as a spawn egg for the given entity type.
         */
        ItemTypeRegistryEntry.Builder spawnEgg(EntityType entityType);

        // Missing: humanoidArmor, wolfArmor, horseArmor, nautilusArmor

        /**
         * Makes it so this item provides the given trim material.
         */
        ItemTypeRegistryEntry.Builder trimMaterial(TrimMaterial trimMaterial);

        /**
         * Marks this item as requiring the given feature flags.
         */
        ItemTypeRegistryEntry.Builder requiredFeatures(FeatureFlag... requiredFeatures);

        // Missing: overrideDescription

        /**
         * Sets this item to use the block prefix in its translation key,
         * i.e. an item with namespaced key {@code example:willow_log} will have translation key
         * {@code block.example.willow_log}.
         *
         * <p>
         * This is typically only used for block items.
         * This is called automatically when you call {@link #inheritsFromBlock},
         * so you don't need to call it manually.
         * </p>
         */
        ItemTypeRegistryEntry.Builder useBlockDescriptionPrefix();

        /**
         * Sets this item to use the item prefix in its translation key,
         * i.e. an item with namespaced key {@code example:willow_stick} will have translation key
         * {@code item.example.willow_stick}.
         *
         * <p>
         * This is the default, so normally you don't need to call this.
         * </p>
         */
        ItemTypeRegistryEntry.Builder useItemDescriptionPrefix();

    }

}
