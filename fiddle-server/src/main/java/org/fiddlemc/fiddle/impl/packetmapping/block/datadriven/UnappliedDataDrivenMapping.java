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
        public <T> DataResult<T> encode(UnappliedDataDrivenMapping mapping, DynamicOps<T> dynamicOps, T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> DataResult<Pair<UnappliedDataDrivenMapping, T>> decode(DynamicOps<T> dynamicOps, T input) {
            return dynamicOps.getMap(input).flatMap(mapLike -> DataResult.success(Pair.of(new UnappliedDataDrivenMapping(dynamicOps, mapLike), input)));
        }

    };

    public static final Decoder<List<UnappliedDataDrivenMapping>> LIST_CODEC = Codec.list(CODEC);

    private final DynamicOps<?> dynamicOps;
    private final MapLike<?> mapLike;

    private UnappliedDataDrivenMapping(DynamicOps<?> dynamicOps, MapLike<?> mapLike) {
        this.dynamicOps = dynamicOps;
        this.mapLike = mapLike;
    }

    public void apply(BlockMappingsComposeEventImpl event, Block block) {
        apply(event, block, (DynamicOps) this.dynamicOps, (MapLike) this.mapLike);
    }

    private static <T> void apply(BlockMappingsComposeEventImpl event, Block block, DynamicOps<T> dynamicOps, MapLike<T> mapLike) {

        // Parse the type
        T typeInput = mapLike.get("type");
        if (typeInput == null) {
            throw new IllegalArgumentException("Missing mapping type for a mapping for block " + block);
        }
        DataResult<String> typeResult = dynamicOps.getStringValue(typeInput);
        if (typeResult.isError()) {
            throw new IllegalArgumentException("Invalid mapping type for a mapping for block " + block + typeResult.error().map(error -> ": " + error.message()).orElse(""));
        }
        String typeString = typeResult.getOrThrow();
        @Nullable DataDrivenMappingType type = DataDrivenMappingTypeRegistry.get(typeString);
        if (type == null) {
            throw new IllegalArgumentException("Unknown mapping type for a mapping for block " + block + ": " + typeString);
        }

        // Let the type apply the mapping
        type.apply(event, block, dynamicOps, mapLike);

        // .map(type -> {
        //     Decoder<? extends UnappliedDataDrivenMapping> decoder =  DataDrivenMappingTypeRegistry.getDecoder(type);
        // })
        // return
        //     UnappliedMapping mapping = new UnappliedMapping();
        // T automaticInput = mapLike.get("automatic");
        // if (automaticInput != null) {
        //     DataResult<Pair<Automatic, T>> automatic = Automatic.CODEC.decode(dynamicOps, automaticInput);
        //     if (automatic.isError()) {
        //         return automatic.map($ -> null);
        //     }
        //     mapping.automatic = automatic.getOrThrow().getFirst();
        // }
        // return DataResult.success(Pair.of(mapping, input));
    }

}
