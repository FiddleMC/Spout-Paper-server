package org.fiddlemc.fiddle.impl.packetmapping.block.datadriven;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.FromBlockStateRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.FromBlockTypeRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ToBlockStateRequestBuilder;
import org.fiddlemc.fiddle.api.packetmapping.block.automatic.ToBlockTypeRequestBuilder;
import org.fiddlemc.fiddle.impl.moredatadriven.minecraft.BlockRegistry;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * Some built-in {@link DataDrivenMappingType}s.
 */
public final class BuiltInDataDrivenMappingTypes {

    private BuiltInDataDrivenMappingTypes() {
        throw new UnsupportedOperationException();
    }

    public static <T> void parseFromBlockState(FromBlockStateRequestBuilder<?> builder, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        T fromInput = mapLike.get("from");
        if (fromInput != null) {
            builder.from(Bukkit.createBlockData(dynamicOps.getStringValue(fromInput).getOrThrow()));
        } else {
            builder.from(block.defaultBlockState().createCraftBlockData());
        }
    }

    public static <T> void parseToBlockState(ToBlockStateRequestBuilder<?> builder, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        T fallbackinput = mapLike.get("fallback");
        if (fallbackinput != null) {
            builder.fallback(Bukkit.createBlockData(dynamicOps.getStringValue(fallbackinput).getOrThrow()));
        }
    }

    public static <T> void parseFromBlockType(FromBlockTypeRequestBuilder<?> builder, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        T fromInput = mapLike.get("from");
        if (fromInput != null) {
            builder.from(CraftBlockType.minecraftToBukkitNew(BlockRegistry.get().getValue(Identifier.CODEC.parse(dynamicOps, fromInput).getOrThrow())));
        } else {
            builder.from(CraftBlockType.minecraftToBukkitNew(block));
        }
    }

    public static <T> void parseToBlockType(ToBlockTypeRequestBuilder<?> builder, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        T fallbackinput = mapLike.get("fallback");
        if (fallbackinput != null) {
            builder.fallback(CraftBlockType.minecraftToBukkitNew(BlockRegistry.get().getValue(Identifier.CODEC.parse(dynamicOps, fallbackinput).getOrThrow())));
        }
    }

    public static final DataDrivenMappingType FULL_BLOCK = new DataDrivenMappingType() {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
            event.automaticMappings().fullBlock(builder -> {
                parseFromBlockState(builder, block, dynamicOps, mapLike);
                parseToBlockState(builder, block, dynamicOps, mapLike);
            });
        }

    };

    public static final DataDrivenMappingType LEAVES = new DataDrivenMappingType() {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
            event.automaticMappings().leaves(builder -> {
                parseFromBlockType(builder, block, dynamicOps, mapLike);
                parseToBlockType(builder, block, dynamicOps, mapLike);
                T tintedInput = mapLike.get("tinted");
                if (tintedInput != null) {
                    builder.tinted(dynamicOps.getBooleanValue(tintedInput).getOrThrow());
                }
            });
        }

    };

    public static final DataDrivenMappingType SLAB = new DataDrivenMappingType() {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
            event.automaticMappings().slab(builder -> {
                parseFromBlockType(builder, block, dynamicOps, mapLike);
                parseToBlockType(builder, block, dynamicOps, mapLike);
                T fullBlockFallbackinput = mapLike.get("full_block_fallback");
                if (fullBlockFallbackinput != null) {
                    builder.fullBlockFallback(Bukkit.createBlockData(dynamicOps.getStringValue(fullBlockFallbackinput).getOrThrow()));
                }
            });
        }

    };

    public static final DataDrivenMappingType STAIRS = new DataDrivenMappingType() {

        @Override
        public <T> void apply(BlockMappingsComposeEventImpl event, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
            event.automaticMappings().stairs(builder -> {
                parseFromBlockType(builder, block, dynamicOps, mapLike);
                parseToBlockType(builder, block, dynamicOps, mapLike);
            });
        }

    };

}
