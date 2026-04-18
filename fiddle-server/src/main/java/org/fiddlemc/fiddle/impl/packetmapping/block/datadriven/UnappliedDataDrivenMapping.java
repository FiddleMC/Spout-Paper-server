package org.fiddlemc.fiddle.impl.packetmapping.block.datadriven;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import net.minecraft.world.level.block.Block;
import org.fiddlemc.fiddle.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;
import java.util.List;

/**
 * The data-driven mappings for a {@link Block}.
 */
public final class UnappliedDataDrivenMapping {

    public static final Codec<UnappliedDataDrivenMapping> CODEC = new Codec<>() {

        @Override
        public <T> DataResult<T> encode(UnappliedDataDrivenMapping mapping, DynamicOps<T> ops, T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> DataResult<Pair<UnappliedDataDrivenMapping, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getMap(input).flatMap(mapLike -> DataResult.success(Pair.of(new UnappliedDataDrivenMapping(ops, mapLike), input)));
        }

    };

    public static final Decoder<List<UnappliedDataDrivenMapping>> LIST_CODEC = Codec.list(CODEC);

    private final DynamicOps<?> ops;
    private final MapLike<?> mapLike;

    private UnappliedDataDrivenMapping(DynamicOps<?> ops, MapLike<?> mapLike) {
        this.ops = ops;
        this.mapLike = mapLike;
    }

    public void apply(BlockMappingsComposeEventImpl event, Block block) {
        apply(event, block, (DynamicOps) this.ops, this.mapLike);
    }

    private static <T> void apply(BlockMappingsComposeEventImpl event, @Nullable Block block, DynamicOps<T> ops, MapLike<T> mapLike) {

        // Parse the type
        T typeInput = mapLike.get("type");
        if (typeInput == null) {
            throw new IllegalArgumentException("Missing mapping type for a mapping" + (block == null ? "" : " for block " + block));
        }
        DataResult<String> typeResult = ops.getStringValue(typeInput);
        if (typeResult.isError()) {
            throw new IllegalArgumentException("Invalid mapping type for a mapping" + (block == null ? "" : " for block " + block) + typeResult.error().map(error -> ": " + error.message()).orElse(""));
        }
        String typeString = typeResult.getOrThrow();
        @Nullable DataDrivenMappingType type = DataDrivenMappingTypeRegistry.get(typeString);
        if (type == null) {
            throw new IllegalArgumentException("Unknown mapping type for a mapping" + (block == null ? "" : " for block " + block) + ": " + typeString);
        }

        // Let the type apply the mapping
        type.apply(event, block, ops, mapLike);

    }

}
