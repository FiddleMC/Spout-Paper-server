package spout.server.paper.impl.moredatadriven.datapack;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import spout.common.branding.SpoutNamespace;

/**
 * Analogous to {@link Registries}, but specifically for Spout registries populated from data packs.
 */
public final class SpoutDataPackRegistries {

    private SpoutDataPackRegistries() {
        throw new UnsupportedOperationException();
    }

    public static final ResourceKey<Registry<Block>> BLOCK_FROM_DATA_PACK = createRegistryKey("block_from_data_pack");
    public static final ResourceKey<Registry<Item>> ITEM_FROM_DATA_PACK = createRegistryKey("item_from_data_pack");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(SpoutNamespace.SPOUT, name));
    }

    static {
        DifferentDataPackRegistryDirectoryNames.register(BLOCK_FROM_DATA_PACK, "block");
        DifferentDataPackRegistryDirectoryNames.register(ITEM_FROM_DATA_PACK, "item");
    }

}
