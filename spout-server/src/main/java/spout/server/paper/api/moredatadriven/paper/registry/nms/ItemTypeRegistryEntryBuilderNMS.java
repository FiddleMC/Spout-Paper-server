package spout.server.paper.api.moredatadriven.paper.registry.nms;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import spout.server.paper.api.moredatadriven.paper.registry.ItemTypeRegistryEntry;

/**
 * An {@link ItemTypeRegistryEntry.Builder} that allows building an {@link Item} using Minecraft internals.
 */
public interface ItemTypeRegistryEntryBuilderNMS extends ItemTypeRegistryEntry.Builder, KeyAwareRegistryEntryNMS {

    /**
     * Sets the factory to use, and marks this builder as using NMS.
     */
    ItemTypeRegistryEntryBuilderNMS factoryNMS(Function<Item.Properties, Item> factory) ;

    /**
     * Sets the factory to use for an item for a block with the {@linkplain #getKeyNMS same} {@link Identifier},
     * calls {@link Item.Properties#useBlockDescriptionPrefix()},
     * and marks this builder as using NMS.
     */
    ItemTypeRegistryEntryBuilderNMS factoryForBlockNMS();

    /**
     * Sets the factory to use for an item for a block with the given {@link Identifier},,
     * calls {@link Item.Properties#useBlockDescriptionPrefix()},
     * and marks this builder as using NMS.
     *
     * @param blockIdentifier The identifier of the block.
     */
    ItemTypeRegistryEntryBuilderNMS factoryForBlockNMS(Identifier blockIdentifier);

    /**
     * Sets the factory to use for an item for a block with the {@linkplain #getKeyNMS same} {@link Identifier},,
     * calls {@link Item.Properties#useBlockDescriptionPrefix()},
     * and marks this builder as using NMS.
     */
    ItemTypeRegistryEntryBuilderNMS factoryForBlockNMS(BiFunction<Block, Item.Properties, BlockItem> factory);

    /**
     * Sets the factory to use for an item for a block with the given {@link Identifier},,
     * calls {@link Item.Properties#useBlockDescriptionPrefix()},
     * and marks this builder as using NMS.
     *
     * @param blockIdentifier The identifier of the block.
     */
    ItemTypeRegistryEntryBuilderNMS factoryForBlockNMS(Identifier blockIdentifier, BiFunction<Block, Item.Properties, BlockItem> factory);

    /**
     * Replaces the NMS properties for the item.
     */
    ItemTypeRegistryEntryBuilderNMS propertiesNMS(Item.Properties properties);

    /**
     * Modifies the NMS properties for the item.
     */
    ItemTypeRegistryEntryBuilderNMS propertiesNMS(Consumer<Item.Properties> properties);

}
