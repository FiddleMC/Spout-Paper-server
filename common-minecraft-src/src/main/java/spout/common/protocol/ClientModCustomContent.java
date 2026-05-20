package spout.common.protocol;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import org.jspecify.annotations.Nullable;
import spout.common.moredatadriven.minecraft.common.dependent.SortDependentDataDrivenResources;
import spout.common.moredatadriven.minecraft.item.SpoutDataDrivenItem;
import spout.common.util.minecraft.resources.KeyedValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all custom content sent by a Spout server.
 */
public final class ClientModCustomContent {

    /**
     * The blocks.
     */
    private final List<KeyedValue<JsonElement>> blocks;

    /**
     * The items.
     */
    private final List<KeyedValue<SpoutDataDrivenItem>> items;

    /**
     * The parsed blocks,
     * or null if not initialized yet.
     */
    private @Nullable List<Block> parsedBlocks;

    /**
     * The parsed items,
     * or null if not initialized yet.
     */
    private @Nullable List<Item> parsedItems;

    private ClientModCustomContent() {
        this.blocks = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public List<KeyedValue<JsonElement>> getBlocks() {
        return this.blocks;
    }

    public List<KeyedValue<SpoutDataDrivenItem>> getItems() {
        return this.items;
    }

    public List<Block> getParsedBlocks() {
        System.out.println("Parsing blocks:");
        this.blocks.forEach(System.out::println);
        if (this.parsedBlocks == null) {
            this.parsedBlocks = this.blocks.stream().map(ClientModCustomContent::parseBlock).toList();
        }
        return this.parsedBlocks;
    }

    public List<Item> getParsedItems() {
        System.out.println("Parsing items:");
        this.items.forEach(System.out::println);
        if (this.parsedItems == null) {
            this.parsedItems = SortDependentDataDrivenResources.sortedKeyedResources(Registries.ITEM, this.items.stream())
                .map(item -> {
                    item.second().initializeItemFromInput(true);
                    return item.second().getItem();
                }).toList();
        }
        return this.parsedItems;
    }

    public static ClientModCustomContent createFilled(
        List<Block> blocks,
        List<Item> items
    ) {
        ClientModCustomContent content = new ClientModCustomContent();
        content.blocks.addAll(
            blocks.stream().map(block -> KeyedValue.of(block.builtInRegistryHolder().key().identifier(), BlockTypes.CODEC.codec().encodeStart(JsonOps.INSTANCE, block).getOrThrow())).toList()
        );
        content.items.addAll(
            items.stream().map(item -> KeyedValue.of(item.builtInRegistryHolder().key().identifier(), new SpoutDataDrivenItem(item))).toList()
        );
        System.out.println("Created filled content with:");
        System.out.println("Blocks:");
        content.blocks.forEach(System.out::println);
        System.out.println("Items:");
        content.items.forEach(System.out::println);
        return content;
    }

    public static ClientModCustomContent createEmpty() {
        return new ClientModCustomContent();
    }

    private static Block parseBlock(KeyedValue<JsonElement> value) {
        return BlockTypes.CODEC.codec().decode(JsonOps.INSTANCE, value.value()).getOrThrow().getFirst();
    }

}
